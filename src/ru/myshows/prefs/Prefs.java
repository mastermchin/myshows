package ru.myshows.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ru.myshows.activity.R;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.06.2011
 * Time: 15:03:23
 * To change this template use File | Settings | File Templates.
 */
public class Prefs {

    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CURRENT_THEME = "defaultTheme";
    public static final int VALUE_DARK_THEME = R.style.DarkTheme;
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";


    public static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getStringPrefs(Context context, String key) {
        return getPrefs(context).getString(key, null);
    }

    public static void setStringPrefs(Context context, String key, String value) {
        getPrefs(context).edit().putString(key, value).commit();
    }

    public static void setBooleanPrefs(Context context, String key, boolean value) {
        getPrefs(context).edit().putBoolean(key, value).commit();
    }

    public static boolean getBooleanPrefs(Context context, String key, boolean defaultValue) {
        return getPrefs(context).getBoolean(key, defaultValue);
    }

    public static int getIntPrefs(Context context, String key, int defaultValue) {
        return getPrefs(context).getInt(key, defaultValue);
    }

    public static void setIntPrefs(Context context, String key, int value) {
        getPrefs(context).edit().putInt(key, value).commit();
    }

}
