package ru.myshows.api;

import android.util.Log;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
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
import ru.myshows.util.Settings;

import java.io.*;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.05.2011
 * Time: 16:22:34
 * To change this template use File | Settings | File Templates.
 */
public class MyShowsApiImpl implements MyShowsApi {

    private String login = null;
    private String password = null;
    private DefaultHttpClient httpClient = null;


    public MyShowsApiImpl() {
        this.httpClient = new DefaultHttpClient();
        ClientConnectionManager mgr = httpClient.getConnectionManager();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 15000);
        HttpConnectionParams.setSoTimeout(params, 20000);
        this.httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
    }

    private void setCredentials(String login, String password) {
        if (login != null && password != null)
            httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(MyShowsApi.URL.URL_HOST, 80), new UsernamePasswordCredentials(login, password));

    }

    private InputStream execute(String url, boolean isPublicRequest) {
        if (isPublicRequest)
            return execute(url,null,null);
        if (login == null || password == null) {
            String lgn = Settings.getString(Settings.KEY_LOGIN);
            String pwd = Settings.getString(Settings.KEY_PASSWORD);
            if (!login(lgn, pwd))
                return null;
        }
        return execute(url, login, password);

    }

    private InputStream execute(String url, String login, String password) {
        setCredentials(login, password);
        HttpGet get = new HttpGet(url.replaceAll("\\s", "+"));
        try {
            Log.d("MyShows", "Request = " + get.getRequestLine().toString());
            HttpResponse response = httpClient.execute(get);
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


    @Override
    public synchronized boolean login(String login, String password) {
        if (login == null || password == null) return false;
        String passwordHash = getPasswordHash(password);
        String url = String.format(MyShowsApi.URL.URL_LOGIN, login, passwordHash);
        if (execute(url, login, passwordHash) != null) {
            Log.d("MyShows", "Login is successful");
            this.login = login;
            this.password = passwordHash;
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
        String url = MyShowsApi.URL.URL_GET_SHOWS;
        return toJson(executeWithStringResult(url, false));
    }

    @Override
    public JSONObject getSeenEpisodes(Integer showId) {
        String url = String.format(MyShowsApi.URL.URL_GET_SEEN_EPISODES, showId);
        return toJson(executeWithStringResult(url, false));
    }

    @Override
    public JSONObject getNextEpisodes() {
        String url = MyShowsApi.URL.URL_GET_NEXT_EPISODES;
        return toJson(executeWithStringResult(url, false));
    }

    @Override
    public JSONObject getUnwatchedEpisodes() {
        String url = URL.URL_GET_UNWATCHED_EPISODES;
        return toJson(executeWithStringResult(url, false));

    }

    @Override
    public boolean checkEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_CHECK_EPISODE, episodeId);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean checkEpisode(Integer episodeId, RATIO ratio) {
        String url = String.format(MyShowsApi.URL.URL_CHECK_EPISODE_RATIO, episodeId, RATIO.getRatio(ratio));
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean uncheckEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_UNCHECK_EPISODE, episodeId);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean syncWatchedEpisodes(Integer showsId, String ids) {
        String url = String.format(MyShowsApi.URL.URL_SYNC_WATCHED, showsId, ids);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean syncAllShowEpisodes(Integer showId, String watchedIds, String unwatchedIds) {
        String url = String.format(MyShowsApi.URL.URL_SYNC_ALL, showId, watchedIds, unwatchedIds);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean changeShowStatus(Integer showId, MyShowsApi.STATUS status) {
        System.out.println("Change show " + showId + " status to : " + status.toString());
        String url = String.format(MyShowsApi.URL.URL_CHANGE_SHOW_STATUS, showId, status.toString());
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean changeShowRatio(Integer showId, int ratio) {
        String url = String.format(MyShowsApi.URL.URL_CHANGE_SHOW_RATIO, showId, ratio);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean changeEpisodeRatio(int ratio, Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_CHANGE_EPISODE_RATIO, ratio, episodeId);
        return executeWithStringResult(url,false) != null ? true : false;
    }

    @Override
    public boolean changeEpisodesRatio(int ratio, String episodeIds) {
        String url = String.format(MyShowsApi.URL.URL_CHANGE_EPISODES_RATIO, ratio, episodeIds);
        return executeWithStringResult(url,false) != null ? true : false;
    }

    @Override
    public JSONArray getFavoritesEpisodes() {
        String url = MyShowsApi.URL.URL_FAVORITES_EPISODES;
        return toJsonArray(executeWithStringResult(url, false));
    }

    @Override
    public boolean addFavoriteEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_ADD_FAVORITE_EPISODE, episodeId);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean removeFavoriteEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_REMOVE_FAVORITE_EPISODE, episodeId);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public JSONArray getIgnoredEpisodes() {
        String url = MyShowsApi.URL.URL_GET_IGNORED_EPISODES;
        return toJsonArray(executeWithStringResult(url, false));
    }

    @Override
    public boolean addIgnoredEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_ADD_IGNORED_EPISODE, episodeId);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public boolean removeIgnoredEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_REMOVE_IGNORED_EPISODE, episodeId);
        return executeWithStringResult(url, false) != null ? true : false;
    }

    @Override
    public JSONObject getNews() {
        String url = MyShowsApi.URL.URL_GET_NEWS;
        return toJson(executeWithStringResult(url, false));
    }

    @Override
    public JSONObject search(String searchString) {
        String url = String.format(MyShowsApi.URL.URL_SEARCH, searchString);
        return toJson(executeWithStringResult(url, true));
    }

    @Override
    public JSONObject searchByFile(String file) {
        String url = String.format(MyShowsApi.URL.URL_SEARCH_BY_FILE, file);
        return toJson(executeWithStringResult(url, true));
    }

    @Override
    public JSONObject getShowInfo(Integer showId) {
        String url = String.format(MyShowsApi.URL.URL_GET_SHOW_INFO, showId);
        return toJson(executeWithStringResult(url, true));
    }

    @Override
    public JSONObject getGenresList() {
        String url = MyShowsApi.URL.URL_GET_GENRES_LIST;
        return toJson(executeWithStringResult(url, true));
    }

    @Override
    public JSONArray getTopShows(GENDER gender) {
        if (gender == null) gender = GENDER.a;
        String url = String.format(MyShowsApi.URL.URL_GET_TOP_SHOWS, GENDER.getGender(gender));
        return toJsonArray(executeWithStringResult(url, true));
    }

    @Override
    public JSONObject getProfile(String login) {
        String url = String.format(MyShowsApi.URL.URL_PROFILE, login);
        return toJson(executeWithStringResult(url, true));
    }


    @Override
    public InputStream getImage(String url) {
        return execute(url , true);
    }

    private String executeWithStringResult(String url, boolean isPublicRequest) {
        return convertStreamToString(execute(url, isPublicRequest));
    }
}
