package com.gmail.alexellingsen.namelesswol.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.gmail.alexellingsen.namelesswol.R;

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}