package com.gmail.alexellingsen.namelesswol.activities;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.gmail.alexellingsen.namelesswol.R;

public class PrefsActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preferences);
        } else {
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new PrefsFragment()).commit();
        }
    }
}