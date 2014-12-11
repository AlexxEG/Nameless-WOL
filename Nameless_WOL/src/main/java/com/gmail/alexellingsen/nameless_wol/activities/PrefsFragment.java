package com.gmail.alexellingsen.nameless_wol.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.gmail.alexellingsen.nameless_wol.R;

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}