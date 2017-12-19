package com.example.tango.mobdev_assignment1.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.example.tango.mobdev_assignment1.R;

import java.util.Map;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PrefsFragment prefsFragment = new PrefsFragment();
        fragmentTransaction.replace(android.R.id.content, prefsFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicEnabled = settings.getBoolean("bgnd_music_on", true);
        boolean sfxEnabled = settings.getBoolean("sound_fx_on", true);
        boolean vibrateEnabled = settings.getBoolean("vibrate_on", true);
        String numberColumns = settings.getString("num_columns", "7");

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("bgnd_music_on", musicEnabled);
        editor.putBoolean("sound_fx_on", sfxEnabled);
        editor.putBoolean("vibrate_on", vibrateEnabled);
        editor.putString("num_columns", numberColumns);
        editor.apply();
    }

    public static class PrefsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(R.xml.preferences);
        }
    }
}
