package ru.myshows.activity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
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
    private CheckBoxPreference showNextEpisodes;
    private CheckBoxPreference showProfile;
    private ListPreference sortShowsPref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        showFriendsNews = (CheckBoxPreference) getPreferenceScreen().findPreference(Settings.PREF_SHOW_NEWS);
        showNextEpisodes = (CheckBoxPreference) getPreferenceScreen().findPreference(Settings.PREF_SHOW_NEXT);
        showProfile = (CheckBoxPreference) getPreferenceScreen().findPreference(Settings.PREF_SHOW_PROFILE);
        sortShowsPref = (ListPreference) getPreferenceScreen().findPreference(Settings.PREF_SHOW_SORT);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String on = getRString(R.string.on);
        String off = getRString(R.string.off);

        showFriendsNews.setSummary(Settings.getBoolean(Settings.PREF_SHOW_NEWS) ? on : off);
        showNextEpisodes.setSummary(Settings.getBoolean(Settings.PREF_SHOW_NEXT) ? on : off);
        showProfile.setSummary(Settings.getBoolean(Settings.PREF_SHOW_PROFILE) ? on : off);
        String s = Settings.getString(Settings.PREF_SHOW_SORT);
        sortShowsPref.setSummary(s.equals("status") ? getRString(R.string.pref_sort_status) : getRString(R.string.pref_sort_alph));

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String on = getResources().getString(R.string.on);
        String off = getResources().getString(R.string.off);

        if (key.equals(Settings.PREF_SHOW_NEWS)) {
            showFriendsNews.setSummary(sharedPreferences.getBoolean(key, false) ? on : off);
        } else if (key.equals(Settings.PREF_SHOW_SORT)) {
            String s = Settings.getString(key);
            sortShowsPref.setSummary(s.equals("status") ? getRString(R.string.pref_sort_status) : getRString(R.string.pref_sort_alph));
        } else if (key.equals(Settings.PREF_SHOW_NEXT)) {
            showNextEpisodes.setSummary(Settings.getBoolean(Settings.PREF_SHOW_NEXT) ? on : off);
        } else if (key.equals(Settings.PREF_SHOW_PROFILE)) {
            showProfile.setSummary(Settings.getBoolean(Settings.PREF_SHOW_PROFILE) ? on : off);
        }
    }

    private String getRString(int id) {
        return getResources().getString(id);
    }
}
