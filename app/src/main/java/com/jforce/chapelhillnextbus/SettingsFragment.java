package com.jforce.chapelhillnextbus;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SettingsFragment extends PreferenceFragment {

    private int checkedRefreshIndex;


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

        Preference refreshPreference = getPreferenceManager().findPreference("refreshInterval");
        String[] entries = getResources().getStringArray(R.array.refresh_interval_entries);
        String[] values = getResources().getStringArray(R.array.refresh_interval_values);

        int index = Arrays.asList(values).indexOf(intervalString);

        refreshPreference.setSummary(entries[index]);


        //Build pref
        String versionName = BuildConfig.VERSION_NAME;
        Preference buildPreference = getPreferenceManager().findPreference("pref_build");
        buildPreference.setSummary("Version " + versionName);






    }

    public void setListeners(){

        final String[] entries = getResources().getStringArray(R.array.refresh_interval_entries);
        final String[] values = getResources().getStringArray(R.array.refresh_interval_values);
        final SharedPreferences sp = getPreferenceManager().getSharedPreferences();




        //Refresh pref

        String currentRefreshSetting = sp.getString("refreshInterval", "30000");
        checkedRefreshIndex = Arrays.asList(values).indexOf(currentRefreshSetting);

        final Preference refreshPreference = getPreferenceManager().findPreference("refreshInterval");

//        refreshPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//
//
//                String string = (String) newValue;
//
//                int index = Arrays.asList(values).indexOf(string);
//
//
//
//                preference.setSummary(entries[index]);
//
//                return true;
//            }
//        });

        refreshPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference pref) {

                MaterialDialog dialog = new MaterialDialog.Builder(pref.getContext())
                        .theme(Theme.DARK)
                        .callback(new MaterialDialog.SimpleCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                dialog.dismiss();

                            }
                        })
                        .title("Auto-refresh interval")
                        .titleColorRes(R.color.accent2)
                        .items(entries)
                        .itemsCallbackSingleChoice(checkedRefreshIndex, new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {


                                refreshPreference.setSummary(entries[which]);


                                SharedPreferences.Editor editor = sp.edit();

                                editor.putString(refreshPreference.getKey(), values[which]);

                                editor.commit();

                                checkedRefreshIndex = which;

                                //dialog.dismiss();




                            }
                        })
                        .contentColorRes(R.color.accent1)
                        .positiveText("Back")
                        .positiveColorRes(R.color.accent2)
                        .build();

                ((LinearLayout)dialog.getTitleFrame().getParent().getParent().getParent()).setBackgroundResource(R.color.main);

                dialog.show();
                return false;
            }
        });





        //LICENSE PREF


        final Preference licensePreference = getPreferenceManager().findPreference("pref_license");

        licensePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference pref) {

                MaterialDialog dialog = new MaterialDialog.Builder(pref.getContext())
                        .theme(Theme.DARK)
                        .callback(new MaterialDialog.SimpleCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                dialog.dismiss();

                            }
                        })
                        .title("Licenses")
                        .titleColorRes(R.color.accent2)
                        .customView(R.layout.dialog_license)
                        .positiveText("Back")
                        .positiveColorRes(R.color.accent2)
                        .build();

                ((LinearLayout)dialog.getTitleFrame().getParent().getParent().getParent().getParent()).setBackgroundResource(R.color.main);

                dialog.show();
                return false;
            }
        });


        //Confirm exit pref
        final CheckBoxPreference confirmExitPreference = (CheckBoxPreference) getPreferenceManager().findPreference("confirmExit");
//        boolean confirmExitValue = sp.getBoolean("confirmExit", true);
//        Toast toast = Toast.makeText(getActivity(), "Already a favorite!", Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
//        confirmExitPreference.setChecked(confirmExitValue);

//        confirmExitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//
//
//                boolean condition = false;
//                // set condition true or false here according to your needs.
//                buyPref.setChecked(condition);
//                Editor edit = preference.getEditor();
//                edit.putBoolean("pref_billing_buy", condition);
//                edit.commit();
//                return false;
//            }
//        });



    }


}
