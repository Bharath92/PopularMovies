package com.example.upadhyb1.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by UpadhyB1 on 3/7/2016.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {



    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vote_count)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
        // Add 'general' preferences, defined in the XML file
        // TODO: Add preferences from XML

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        // TODO: Add preferences
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        /*Preference preference = findPreference(getString(R.string.pref_vote_count));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                Log.d("SettingsActivity","Number changed");
                try{
                    int num = Integer.parseInt(stringValue);
                    Log.d("SettingsActivity",num+"");
                    if(num < 1 || num >10000){
                        Toast.makeText(getApplicationContext(), "Please enter only numbers between 1 - 1000!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Please enter only numbers between 1 - 1000!", Toast.LENGTH_LONG).show();
                    return false;
                }
                //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vote_count)));
                preference.setSummary(stringValue);
                return true;
            }
        });*/

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.

        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        Log.d("SettingsActivity","Coming here");
        if (preference instanceof ListPreference) {
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            Log.d("SettingsActivity","Number changed");
            try{
                int num = Integer.parseInt(stringValue);
                Log.d("SettingsActivity",num+"");
                if(num < 1 || num >10000){
                    Toast.makeText(getApplicationContext(), "Please enter only numbers between 1 - 1000!", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Please enter only numbers between 1 - 1000!", Toast.LENGTH_LONG).show();
                return false;
            }
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vote_count)));
            preference.setSummary(stringValue);
        }
        return true;
    }


}
