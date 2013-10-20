package ru.myshows.api;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.myshows.activity.MyShows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.security.MessageDigest;

import static ru.myshows.api.MyShowsApi.OAUTH_TYPE.*;
import static ru.myshows.api.MyShowsApi.URL.*;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.05.2011
 * Time: 16:22:34
 * To change this template use File | Settings | File Templates.
 */
public class MyShowsApiImpl implements MyShowsApi {

    private DefaultHttpClient httpClient = null;


    public MyShowsApiImpl() {
        httpClient = new DefaultHttpClient();
        ClientConnectionManager mgr = httpClient.getConnectionManager();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 15000);
        HttpConnectionParams.setSoTimeout(params, 20000);
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

    }


    private InputStream executeWithResult(String url) {
        HttpGet get = new HttpGet(url.replaceAll("\\s", "+"));

        try {
            Log.d("MyShows", "Request = " + get.getRequestLine().toString());
            HttpResponse response = httpClient.execute(get);

//            for (Header h : response.getAllHeaders()) {
//                Log.d("MyShows", h.getName() + "=" + h.getValue());
//            }

            int code = response.getStatusLine().getStatusCode();
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream stream = response.getEntity().getContent();
                if (stream == null)
                    Log.d("MyShows", "Response = null");
                else
                    Log.d("MyShows", "Response = " +  stream.available());
                return stream;
            } else {
                Log.e("MyShows", "Wrong response code " + code + " for request " + get.getRequestLine().toString());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int execute(String url) {
        HttpGet get = new HttpGet(url.replaceAll("\\s", "+"));
        try {
            Log.d("MyShows", "Request = " + get.getRequestLine().toString());
            HttpResponse response = httpClient.execute(get);
            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    @Override
    public synchronized boolean login(String login, String password) {
        if (login == null || password == null)
            return false;
        String url = String.format(URL_LOGIN, login, getPasswordHash(password));
        if (execute(url) == HttpStatus.SC_OK) {
            Log.d("MyShows", "Login is successful");
            MyShows.isLoggedIn = true;
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean loginSocial(int socialLoginType, String token, String userId, String secret) {
        if (token == null || userId == null)
            return false;

        String loginUrl = null;

        if (socialLoginType == OAUTH_FACEBOOK) loginUrl = URL_LOGIN_FACEBOOK;
        if (socialLoginType == OAUTH_TWITTER)  loginUrl = URL_LOGIN_TWITTER;
        if (socialLoginType == OAUTH_VK)       loginUrl = URL_LOGIN_VK;

        if (loginUrl == null) return false;

        String url = String.format(loginUrl, token, userId, secret);
        if (execute(url) == HttpStatus.SC_OK) {
            Log.d("MyShows", "Login is successful");
            MyShows.isLoggedIn = true;
            return true;
        }
        return false;
    }

    private String getPasswordHash(String _password) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(_password.getBytes());
            byte[] hashDigest = algorithm.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hashDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & hashDigest[i]);
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean register(String login, String password, String email, GENDER gender) {
        if (login == null || password == null || gender == null) return false;
        return false;
    }


    private JSONObject toJson(String s) {
        if (s == null) return null;
        Log.d("MyShows", "s = " + s);
        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            return null;
        }
    }

    private JSONArray toJsonArray(String s) {
        System.out.println("s = " + s);
        if (s == null) return null;
        try {
            return new JSONArray(s);
        } catch (JSONException e) {
            return null;
        }
    }

    private String convertStreamToString(InputStream is) {
        if (is == null) return null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    @Override
    public JSONObject getShows() {
        String url = URL_GET_SHOWS;
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getSeenEpisodes(Integer showId) {
        String url = String.format(URL_GET_SEEN_EPISODES, showId);
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getNextEpisodes() {
        String url = URL_GET_NEXT_EPISODES;
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getUnwatchedEpisodes() {
        String url = URL.URL_GET_UNWATCHED_EPISODES;
        return toJson(executeWithStringResult(url));

    }

    @Override
    public boolean checkEpisode(Integer episodeId) {
        String url = String.format(URL_CHECK_EPISODE, episodeId);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean checkEpisode(Integer episodeId, RATIO ratio) {
        String url = String.format(URL_CHECK_EPISODE_RATIO, episodeId, RATIO.getRatio(ratio));
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean uncheckEpisode(Integer episodeId) {
        String url = String.format(URL_UNCHECK_EPISODE, episodeId);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean syncWatchedEpisodes(Integer showsId, String ids) {
        String url = String.format(URL_SYNC_WATCHED, showsId, ids);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean syncAllShowEpisodes(Integer showId, String watchedIds, String unwatchedIds) {
        String url = String.format(URL_SYNC_ALL, showId, watchedIds, unwatchedIds);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean changeShowStatus(Integer showId, MyShowsApi.STATUS status) {
        System.out.println("Change show " + showId + " status to : " + status.toString());
        String url = String.format(URL_CHANGE_SHOW_STATUS, showId, status.toString());
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean changeShowRatio(Integer showId, int ratio) {
        String url = String.format(URL_CHANGE_SHOW_RATIO, showId, ratio);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean changeEpisodeRatio(int ratio, Integer episodeId) {
        String url = String.format(URL_CHANGE_EPISODE_RATIO, ratio, episodeId);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean changeEpisodesRatio(int ratio, String episodeIds) {
        String url = String.format(URL_CHANGE_EPISODES_RATIO, ratio, episodeIds);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public JSONArray getFavoritesEpisodes() {
        String url = URL_FAVORITES_EPISODES;
        return toJsonArray(executeWithStringResult(url));
    }

    @Override
    public boolean addFavoriteEpisode(Integer episodeId) {
        String url = String.format(URL_ADD_FAVORITE_EPISODE, episodeId);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean removeFavoriteEpisode(Integer episodeId) {
        String url = String.format(URL_REMOVE_FAVORITE_EPISODE, episodeId);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public JSONArray getIgnoredEpisodes() {
        String url = URL_GET_IGNORED_EPISODES;
        return toJsonArray(executeWithStringResult(url));
    }

    @Override
    public boolean addIgnoredEpisode(Integer episodeId) {
        String url = String.format(URL_ADD_IGNORED_EPISODE, episodeId);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public boolean removeIgnoredEpisode(Integer episodeId) {
        String url = String.format(URL_REMOVE_IGNORED_EPISODE, episodeId);
        return execute(url) == HttpStatus.SC_OK;
    }

    @Override
    public JSONObject getNews() {
        String url = URL_GET_NEWS;
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject search(String searchString) {
        String url = String.format(URL_SEARCH, searchString);
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject searchByFile(String file) {
        String url = String.format(URL_SEARCH_BY_FILE, file);
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getShowInfo(Integer showId) {
        String url = String.format(URL_GET_SHOW_INFO, showId);
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getGenresList() {
        String url = URL_GET_GENRES_LIST;
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONArray getTopShows(GENDER gender) {
        if (gender == null) gender = GENDER.a;
        String url = String.format(URL_GET_TOP_SHOWS, GENDER.getGender(gender));
        return toJsonArray(executeWithStringResult(url));
    }

    @Override
    public JSONObject getProfile(String login) {
        String url = String.format(URL_PROFILE, login);
        return toJson(executeWithStringResult(url));
    }


    @Override
    public InputStream getImage(String url) {
        return executeWithResult(url);
    }

    private String executeWithStringResult(String url) {
        return convertStreamToString(executeWithResult(url));
    }


    public JSONObject executeExternalWithJson(String url){
        return toJson(executeWithStringResult(url));
    }
}
