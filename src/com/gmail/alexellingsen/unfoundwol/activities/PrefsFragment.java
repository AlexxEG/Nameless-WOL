package com.gmail.alexellingsen.unfoundwol.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.gmail.alexellingsen.unfoundwol.R;

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}