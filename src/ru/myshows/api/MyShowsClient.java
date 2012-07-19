package ru.myshows.api;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.myshows.domain.*;
import ru.myshows.util.JsonUtil;

import java.io.InputStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 14:30:57
 * To change this template use File | Settings | File Templates.
 */
public class MyShowsClient {

    private static MyShowsApi api;
    private static MyShowsClient instance = null;


    public static MyShowsClient getInstance() {
        if (instance == null) {
            instance = new MyShowsClient();
        }
        return instance;
    }

    private MyShowsClient() {
        api = new MyShowsApiImpl();
    }

    public boolean login(String login, String password) {
        return api.login(login, password);
    }

    public boolean register(String login, String password, String email, MyShowsApi.GENDER gender) {
        return api.register(login, password, email, gender);
    }

    public List<UserShow> getShows() {
        JSONObject shows = api.getShows();
        return JsonUtil.listFromJson(shows, UserShow.class);
    }

    public List<WatchedEpisode> getSeenEpisodes(Integer showId) {
        JSONObject episodes = api.getSeenEpisodes(showId);
        return JsonUtil.listFromJson(episodes, WatchedEpisode.class);
    }

    public List<Episode> getNextEpisodes() {
        JSONObject episodes = api.getNextEpisodes();
        return JsonUtil.listFromJson(episodes, Episode.class);
    }

    public List<Episode> getUnwatchedEpisodes() {
        JSONObject episodes = api.getUnwatchedEpisodes();
        return JsonUtil.listFromJson(episodes, Episode.class);

    }

    public boolean checkEpisode(Integer episodeId) {
        return api.checkEpisode(episodeId);
    }

    public boolean checkEpisode(Integer episodeId, MyShowsApi.RATIO ratio) {
        return api.checkEpisode(episodeId, ratio);
    }

    public boolean uncheckEpisode(Integer episodeId) {
        return api.uncheckEpisode(episodeId);
    }

    public boolean syncWatchedEpisodes(Integer showId, String ids) {
        return api.syncWatchedEpisodes(showId, ids);
    }

    public boolean syncAllShowEpisodes(Integer showId, String watchedIds, String unwatchedIds) {
        return api.syncAllShowEpisodes(showId, watchedIds, unwatchedIds);
    }

    public boolean changeShowStatus(Integer showId, MyShowsApi.STATUS status) {
        return api.changeShowStatus(showId, status);
    }

    public boolean changeShowRatio(Integer showId, int ratio) {
        return api.changeShowRatio(showId, ratio);
    }

    public boolean changeEpisodeRatio(int ratio, Integer episodeId) {
        return api.changeEpisodeRatio(ratio, episodeId);
    }

    public JSONArray getFavoritesEpisodes() {
        JSONArray episodes = api.getFavoritesEpisodes();
        return episodes;
    }

    public boolean addFavoriteEpisode(Integer episodeId) {
        return api.addFavoriteEpisode(episodeId);
    }

    public boolean removeFavoriteEpisode(Integer episodeId) {
        return api.removeFavoriteEpisode(episodeId);
    }

    public JSONArray getIgnoredEpisodes() {
        JSONArray episodes = api.getIgnoredEpisodes();
        return episodes;
    }

    public boolean addIgnoredEpisode(Integer episodeId) {
        return api.addIgnoredEpisode(episodeId);
    }

    public boolean removeIgnoredEpisode(Integer episodeId) {
        return api.removeIgnoredEpisode(episodeId);
    }

    public Map<String, List<UserNews>> getNews() {
        JSONObject json = api.getNews();
        return JsonUtil.mapFromJson(json, UserNews.class);
    }

    public List<IShow> search(String searchString) {
        JSONObject json = api.search(searchString);
        return JsonUtil.listFromJson(json, Show.class);
    }

    public List<FileSearchResult> searchByFile(String file) {
        JSONObject json = api.searchByFile(file);
        return JsonUtil.listFromJson(json, FileSearchResult.class);
    }

    public Show getShowInfo(Integer showId) {
        JSONObject json = api.getShowInfo(showId);
        return (Show) JsonUtil.objectFromJson(json, Show.class);
    }

    public List<Genre> getGenresList() {
        JSONObject json = api.getGenresList();
        return JsonUtil.listFromJson(json, Genre.class);
    }

    public Map<Integer, Genre> getGenresListAsMap() {
        JSONObject json = api.getGenresList();
        return JsonUtil.mapFromJson2(json, Genre.class);
    }

    public List<Show> getTopShows(MyShowsApi.GENDER gender) {
        JSONArray json = api.getTopShows(gender);
        return JsonUtil.listFromJsonArray(json, Show.class);
    }

    public Profile getProfile(String login) {
        JSONObject json = api.getProfile(login);
        return (Profile) JsonUtil.objectFromJson(json, Profile.class);
    }

    public InputStream getImage(String url) {
        return api.getImage(url);
    }

}
