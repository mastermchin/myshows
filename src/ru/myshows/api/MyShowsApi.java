package ru.myshows.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.05.2011
 * Time: 15:06:41
 * To change this template use File | Settings | File Templates.
 */
public interface MyShowsApi {

    public boolean login(String login, String password);

    public boolean register(String login, String password, String email, GENDER gender);

    public JSONObject getShows();

    public JSONObject getSeenEpisodes(Integer showId);

    public JSONObject getNextEpisodes();

    public JSONObject getUnwatchedEpisodes();

    public boolean checkEpisode(Integer episodeId);

    public boolean checkEpisode(Integer episodeId, RATIO ratio);

    public boolean uncheckEpisode(Integer episodeId);

    public boolean syncWatchedEpisodes(Integer showsId, String ids);

    public boolean syncAllShowEpisodes(Integer showId, String watchedIds, String unwatchedIds);

    public boolean changeShowStatus(Integer showId, STATUS status);

    public boolean changeShowRatio(Integer showId, int ratio);

    public boolean changeEpisodeRatio(int ratio, Integer episodeId);

    public JSONArray getFavoritesEpisodes();

    public boolean addFavoriteEpisode(Integer episodeId);

    public boolean removeFavoriteEpisode(Integer episodeId);

    public JSONArray getIgnoredEpisodes();

    public boolean addIgnoredEpisode(Integer episodeId);

    public boolean removeIgnoredEpisode(Integer episodeId);

    public JSONObject getNews();

    public JSONObject search(String searchString);

    public JSONObject searchByFile(String file);

    public JSONObject getShowInfo(Integer showId);

    public JSONObject getGenresList();

    public JSONArray getTopShows(GENDER gender);

    public JSONObject getProfile(String login);

    public InputStream getImage(String url);

    public static enum ACTION {
        add,
        remove
    }

    public static enum STATUS {
        finished,
        watching,
        later,
        cancelled,
        remove;


    }


    public static enum GENDER {
        a, // all
        m, // male
        f;  //female

        public static String getGender(GENDER g) {
            if (g.equals(a)) return "all";
            if (g.equals(m)) return "male";
            if (g.equals(f)) return "female";
            return "all";
        }
    }


    public static enum RATIO {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE;

        public static int getRatio(RATIO r) {
            switch (r) {
                case ONE:
                    return 1;
                case TWO:
                    return 2;
                case THREE:
                    return 3;
                case FOUR:
                    return 4;
                case FIVE:
                    return 5;
            }
            return 1;
        }
    }

    public static interface URL {
        public static final String URL_HOST = "http://api.myshows.ru";
        public static final String URL_LOGIN = "http://api.myshows.ru/profile/login?login=%1$s&password=%2$s";
        public static final String URL_GET_SHOWS = "http://api.myshows.ru/profile/shows/";
        public static final String URL_GET_SEEN_EPISODES = "http://api.myshows.ru/profile/shows/%1$d/";
        public static final String URL_GET_UNWATCHED_EPISODES = "http://api.myshows.ru/profile/episodes/unwatched/";
        public static final String URL_GET_NEXT_EPISODES = "http://api.myshows.ru/profile/episodes/next/";
        public static final String URL_CHECK_EPISODE = "http://api.myshows.ru/profile/episodes/check/%1$d/";
        public static final String URL_CHECK_EPISODE_RATIO = "http://api.myshows.ru/profile/episodes/rate/%1$d/%2$d";
        public static final String URL_UNCHECK_EPISODE = "http://api.myshows.ru/profile/episodes/uncheck/%1$d/";
        public static final String URL_SYNC_WATCHED = "http://api.myshows.ru/profile/shows/%1$d/sync?episodes=%2$s";
        public static final String URL_SYNC_ALL = "http://api.myshows.ru/profile/shows/%1$d/episodes?check=%2$s&uncheck=%3$s";
        public static final String URL_CHANGE_SHOW_STATUS = "http://api.myshows.ru/profile/shows/%1$d/%2$s";
        public static final String URL_CHANGE_SHOW_RATIO = "http://api.myshows.ru/profile/shows/%1$d/rate/%2$d";
        public static final String URL_CHANGE_EPISODE_RATIO = "http://api.myshows.ru/profile/episodes/rate/%1$d/%2$d";
        public static final String URL_FAVORITES_EPISODES = "http://api.myshows.ru/profile/episodes/favorites/list/";
        public static final String URL_ADD_FAVORITE_EPISODE = "http://api.myshows.ru/profile/episodes/favorites/add/%1$d";
        public static final String URL_REMOVE_FAVORITE_EPISODE = "http://api.myshows.ru/profile/episodes/favorites/remove/%1$d";
        public static final String URL_GET_IGNORED_EPISODES = "http://api.myshows.ru/profile/episodes/ignored/list/";
        public static final String URL_ADD_IGNORED_EPISODE = "http://api.myshows.ru/profile/episodes/ignored/add/%1$d";
        public static final String URL_REMOVE_IGNORED_EPISODE = "http://api.myshows.ru/profile/episodes/ignored/remove/%1$d";
        public static final String URL_GET_NEWS = "http://api.myshows.ru/profile/news/";
        public static final String URL_SEARCH = "http://api.myshows.ru/shows/search/?q=%1$s";
        public static final String URL_SEARCH_BY_FILE = "http://api.myshows.ru/shows/search/file/?q=%1$s";
        public static final String URL_GET_SHOW_INFO = "http://api.myshows.ru/shows/%1$d";
        public static final String URL_GET_GENRES_LIST = "http://api.myshows.ru/genres/";
        public static final String URL_GET_TOP_SHOWS = "http://api.myshows.ru/shows/top/%1$s/";
        public static final String URL_PROFILE = "http://api.myshows.ru/profile/%1$s";


    }


}
