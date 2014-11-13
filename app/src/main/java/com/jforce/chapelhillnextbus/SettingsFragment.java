package com.jforce.chapelhillnextbus;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        initSummaries();
        setListeners();


    }

    public void initSummaries(){


        //Refresh preference

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String intervalString = sharedPreferences.getString("refreshInterval", "30000");

        ListPreference refreshPreference = (ListPreference) getPreferenceManager().findPreference("refreshInterval");
        String[] entries = getResources().getStringArray(R.array.refresh_interval_entries);
        String[] values = getResources().getStringArray(R.array.refresh_interval_values);

        int index = Arrays.asList(values).indexOf(intervalString);

        refreshPreference.setSummary(entries[index]);


        //Build pref
        String versionName = BuildConfig.VERSION_NAME;
        Preference buildPreference = getPreferenceManager().findPreference("pref_build");
        buildPreference.setSummary("Version " + versionName);



        //Button


//        Preference preference = getPreferenceManager().findPreference("button");
//
//        preference.setSummary(entries[index]);






    }

    public void setListeners(){

        final String[] entries = getResources().getStringArray(R.array.refresh_interval_entries);
        final String[] values = getResources().getStringArray(R.array.refresh_interval_values);

        //Refresh pref

        ListPreference refreshPreference = (ListPreference) getPreferenceManager().findPreference("refreshInterval");

        refreshPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {


                String string = (String) newValue;

                int index = Arrays.asList(values).indexOf(string);



                preference.setSummary(entries[index]);

                return true;
            }
        });




//        final Preference preference = getPreferenceManager().findPreference("button");
//
//        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//
//            @Override
//            public boolean onPreferenceClick(Preference pref) {
//
//                AlertDialog levelDialog = null;
//
//                // Creating and Building the Dialog
//                //(new ContextThemeWrapper(pref.getContext(), R.style.CustomDialogTheme))
//                AlertDialog.Builder builder = new AlertDialog.Builder((new ContextThemeWrapper(pref.getContext(), R.style.CustomDialogTheme)));
//                builder.setTitle(pref.getTitle());
//                builder.setSingleChoiceItems(entries, -1, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int item) {
//
//
//                        switch(item)
//                        {
//                            case 0:
//                                // Your code when first option seletced
//                                break;
//                            case 1:
//                                // Your code when 2nd  option seletced
//
//                                break;
//                            case 2:
//                                // Your code when 3rd option seletced
//                                break;
//
//                        }
//                        dialog.dismiss();
//                    }
//                });
//                levelDialog = builder.create();
//                levelDialog.show();
//                return false;
//            }
//        });



    }


}
