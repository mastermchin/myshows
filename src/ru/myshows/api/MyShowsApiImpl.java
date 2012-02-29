package ru.myshows.api;

import android.content.Context;
import android.util.Log;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.myshows.prefs.Prefs;
import ru.myshows.util.MyShowsUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        //HttpConnectionParams.setConnectionTimeout(params, 3000);
        //HttpConnectionParams.setSoTimeout(params, 5000);
        this.httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
    }


    private void setCredentials(String login, String password) {
        if (login != null && password != null)
            httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(MyShowsApi.URL.URL_HOST, 80), new UsernamePasswordCredentials(login, password));

    }

    private InputStream execute(String url) {
        setCredentials(login, password);
        if (httpClient == null || url == null) {
            return null;
        }
        HttpResponse response = null;
        try {
            HttpGet get = new HttpGet(url.replaceAll("\\s", "+"));
            Log.d("", "Request = " + get.getRequestLine().toString());
            response = httpClient.execute(get);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpURLConnection.HTTP_OK) {
                return response.getEntity().getContent();
            } else {
                Log.e("", "Wrong response code " + code + " for request " + get.getRequestLine().toString());
                return null;
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }


    private InputStream execute(String url, String login, String password) {
        setCredentials(login, password);
        if (httpClient == null || url == null) {
            return null;
        }
        HttpGet get = new HttpGet(url);
        try {
            Log.d("", "Request = " + get.getRequestLine().toString());
            HttpResponse response = httpClient.execute(get);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpURLConnection.HTTP_OK) {
                return response.getEntity().getContent();
                //return convertStreamToString(responseStream);
            } else {
                Log.e("", "Wrong response code " + code + " for request " + get.getRequestLine().toString());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private JSONObject toJson(String s) {
        if (s == null) return null;
        System.out.println("s = " + s);
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
    public boolean login(String login, String password) {
        System.out.println("Login!");
        if (login == null || password == null) return false;
        String passwordHash = getPasswordHash(password);
        String url = String.format(MyShowsApi.URL.URL_LOGIN, login, passwordHash);
        if (execute(url, login, passwordHash) != null) {
            System.out.println("Login is successful");
            this.login = login;
            this.password = passwordHash;
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

    @Override
    public JSONObject getShows() {
        System.out.println("Get User Shows!");
        String url = MyShowsApi.URL.URL_GET_SHOWS;
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getSeenEpisodes(Integer showId) {
        String url = String.format(MyShowsApi.URL.URL_GET_SEEN_EPISODES, showId);
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getNextEpisodes() {
        String url = MyShowsApi.URL.URL_GET_NEXT_EPISODES;
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getUnwatchedEpisodes() {
        String url = URL.URL_GET_UNWATCHED_EPISODES;
        return toJson(executeWithStringResult(url));

    }

    @Override
    public boolean checkEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_CHECK_EPISODE, episodeId);
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean checkEpisode(Integer episodeId, RATIO ratio) {
        String url = String.format(MyShowsApi.URL.URL_CHECK_EPISODE_RATIO, episodeId, RATIO.getRatio(ratio));
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean uncheckEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_UNCHECK_EPISODE, episodeId);
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean syncWatchedEpisodes(Integer showsId, String ids) {
        String url = String.format(MyShowsApi.URL.URL_SYNC_WATCHED, showsId, ids);
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean syncAllShowEpisodes(Integer showId, String watchedIds, String unwatchedIds) {
        String url = String.format(MyShowsApi.URL.URL_SYNC_ALL, showId, watchedIds, unwatchedIds);
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean changeShowStatus(Integer showId, MyShowsApi.STATUS status) {
        System.out.println("Change show " + showId + " status to : " + status.toString());
        String url = String.format(MyShowsApi.URL.URL_CHANGE_SHOW_STATUS, showId, status.toString());
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean changeShowRatio(Integer showId, int ratio) {
        String url = String.format(MyShowsApi.URL.URL_CHANGE_SHOW_RATIO, showId, ratio);
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean changeEpisodeRatio(int ratio, Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_CHANGE_EPISODE_RATIO, ratio, episodeId);
        return execute(url) != null ? true : false;
    }

    @Override
    public JSONArray getFavoritesEpisodes() {
        String url = MyShowsApi.URL.URL_FAVORITES_EPISODES;
        return toJsonArray(executeWithStringResult(url));
    }

    @Override
    public boolean addFavoriteEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_ADD_FAVORITE_EPISODE, episodeId);
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean removeFavoriteEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_REMOVE_FAVORITE_EPISODE, episodeId);
        return execute(url) != null ? true : false;
    }

    @Override
    public JSONArray getIgnoredEpisodes() {
        String url = MyShowsApi.URL.URL_GET_IGNORED_EPISODES;
        return toJsonArray(executeWithStringResult(url));
    }

    @Override
    public boolean addIgnoredEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_ADD_IGNORED_EPISODE, episodeId);
        return execute(url) != null ? true : false;
    }

    @Override
    public boolean removeIgnoredEpisode(Integer episodeId) {
        String url = String.format(MyShowsApi.URL.URL_REMOVE_IGNORED_EPISODE, episodeId);
        return execute(url) != null ? true : false;
    }

    @Override
    public JSONObject getNews() {
        String url = MyShowsApi.URL.URL_GET_NEWS;
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject search(String searchString) {
        String url = String.format(MyShowsApi.URL.URL_SEARCH, searchString);
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject searchByFile(String file) {
        String url = String.format(MyShowsApi.URL.URL_SEARCH_BY_FILE, file);
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getShowInfo(Integer showId) {
        String url = String.format(MyShowsApi.URL.URL_GET_SHOW_INFO, showId);
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONObject getGenresList() {
        String url = MyShowsApi.URL.URL_GET_GENRES_LIST;
        return toJson(executeWithStringResult(url));
    }

    @Override
    public JSONArray getTopShows(GENDER gender) {
        if (gender == null) gender = GENDER.a;
        String url = String.format(MyShowsApi.URL.URL_GET_TOP_SHOWS, GENDER.getGender(gender));
        return toJsonArray(executeWithStringResult(url));
    }

    @Override
    public JSONObject getProfile(String login) {
        String url = String.format(MyShowsApi.URL.URL_PROFILE, login);
        return toJson(executeWithStringResult(url));
    }


    @Override
    public InputStream getImage(String url) {
        return execute(url);
    }

    private String executeWithStringResult(String url) {
        return convertStreamToString(execute(url));
    }
}
