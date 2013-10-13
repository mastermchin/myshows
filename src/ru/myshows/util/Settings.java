package ru.myshows.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;


public class Settings {

    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_LOGGED_IN = "loggedIn";
    public static final String PREF_SHOW_NEWS = "show_news_key";
    public static final String PREF_SHOW_NEXT = "show_next_key";
    public static final String PREF_SHOW_PROFILE = "show_profile_key";
    public static final String PREF_SHOW_SORT = "shows_sort_key";
    public static final String PREF_SEASONS_SORT = "seasons_sort_key";


    public static String TWITTER_CONSUMER_KEY = "LnjZiH7g5XzmmiYwOwrg";
    public static String TWITTER_CONSUMER_SECRET = "iVW8PHRYkkTcvqscL6yjwcoDwJxR3esNaThnxHmU";
    public static String TWITTER_CALLBACK_URL = "oauth://ru.myshows.activity.Twitter_oAuth";
    public static String URL_PARAMETER_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN="TWITTER_OAUTH_TOKEN";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET="TWITTER_OAUTH_TOKEN_SECRET";
    public static String PREFERENCE_TWITTER_IS_LOGGED_IN="TWITTER_IS_LOGGED_IN";

    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MyShows.context);
    }

    public static String getString(String name) {
        return getPreferences().getString(name, "");
    }

    public static void setString(String key, String value) {
        getPreferences().edit().putString(key, value).commit();
    }

    public static Boolean getBoolean(String name) {
        return getPreferences().getBoolean(name, false);
    }

    public static void setBoolean(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).commit();
    }

    public static int getInt(String name) {
        return Integer.parseInt(getPreferences().getString(name, "-1"));
    }


}
