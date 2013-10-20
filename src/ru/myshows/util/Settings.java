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

    public static final String TWITTER_CONSUMER_KEY = "LnjZiH7g5XzmmiYwOwrg";
    public static final String TWITTER_CONSUMER_SECRET = "iVW8PHRYkkTcvqscL6yjwcoDwJxR3esNaThnxHmU";
    public static final String TWITTER_CALLBACK_URL = "oauth://ru.myshows.activity.Twitter_oAuth";
    public static final String TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static final String TWITTER_OAUTH_TOKEN ="TWITTER_OAUTH_TOKEN";
    public static final String TWITTER_OAUTH_TOKEN_SECRET ="TWITTER_OAUTH_TOKEN_SECRET";
    public static final String TWITTER_IS_LOGGED_IN ="TWITTER_IS_LOGGED_IN";

    public static final String FACEBOOK_IS_LOGGED_IN ="FACEBOOK_IS_LOGGED_IN";


    public static final String VK_IS_LOGGED_IN ="FACEBOOK_IS_LOGGED_IN";



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
