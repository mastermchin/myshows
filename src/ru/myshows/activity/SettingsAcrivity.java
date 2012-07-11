package ru.myshows.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import ru.myshows.util.Settings;


/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 28.08.2011
 * Time: 1:58:01
 * To change this template use File | Settings | File Templates.
 */
public class SettingsAcrivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private CheckBoxPreference showFriendsNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        showFriendsNews = (CheckBoxPreference) getPreferenceScreen().findPreference("show_news_key");

    }


    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
