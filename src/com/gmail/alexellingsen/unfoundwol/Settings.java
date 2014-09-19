package com.gmail.alexellingsen.unfoundwol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

    enum Theme {dark, light, darkactionbar}

    public static int getThemeID(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String theme = sharedPref.getString("pref_theme", "dark");

        switch (Theme.valueOf(theme)) {
            case darkactionbar:
                return R.style.AppTheme_Light_DarkActionBar;
            case light:
                return R.style.AppTheme_Light;
            case dark:
            default:
                return R.style.AppTheme;
        }
    }
}