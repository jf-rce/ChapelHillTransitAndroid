/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jforce.chapelhillnextbus;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;


/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class HomeActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, MapFragment.MapHost{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView mDrawerListSecondary;
    private LinearLayout mDrawerArea;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private CharSequence mSubtitle;
    private String[] mDrawerTitles;
    private String[] mDrawerTitlesSecondary;

    private ArrayList<Route> routeListCache;
    private Fragment[] fragmentCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTitle = mDrawerTitle = getTitle();
        mSubtitle = "";
        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerTitlesSecondary = getResources().getStringArray(R.array.drawer_array2);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerArea = (LinearLayout) findViewById(R.id.drawer_area);

        fragmentCache = new Fragment[] {null, null, null};


        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerListSecondary = (ListView) findViewById(R.id.left_drawer2);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerArrayAdapter(this, mDrawerTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerListSecondary.setAdapter(new DrawerArrayAdapter2(this, mDrawerTitlesSecondary));
        mDrawerListSecondary.setOnItemClickListener(new DrawerItemClickListener2());


        routeListCache = null;

        // enable ActionBar app icon to behave as action to toggle nav drawer
        //getSupportActionBar().setIcon(R.drawable.ic_logo_white_nobezel_small);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                FragmentManager fragmentManager = getFragmentManager();

                Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);

                if(fragment.getClass() == MapFragment.class){
                    MapFragment mf = (MapFragment) fragment;
                    mf.resetSubtitle();
                }




            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setSubtitle("");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()


            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0, true);
        }




        fetchWeather();


        initPreferences();






    }


    public void initPreferences(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Migration from old favorites system (1.3 and below)
        boolean hasMigrated = sharedPreferences.getBoolean("hasMigrated", false);

        if(!hasMigrated){
            migrateFavorites();
            editor.putBoolean("hasMigrated", true);

        }


        //confirmExit init
        boolean confirmExit = sharedPreferences.getBoolean("confirmExit", true);
        editor.putBoolean("confirmExit", confirmExit);




        //numStarts
        int numStarts = sharedPreferences.getInt("numStarts", 0);

        numStarts++;
        if(numStarts == 10){
            showRateAppDialog();


        }
        editor.putInt("numStarts", numStarts);



        editor.commit();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dev_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerArea);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
        return false;
//
//        // Handle action buttons
//        switch(item.getItemId()) {
//            case R.id.action_websearch:
//                // create intent to perform web search for this planet
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position, true);
        }
    }

    private class DrawerItemClickListener2 implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemSecondary(position);
        }
    }

    public void selectItem(int position, boolean alreadyOpen) {
        // update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        final int finalPosition = position;


        // update selected item and title, then close the drawer
        if(!alreadyOpen){

            mDrawerLayout.openDrawer(mDrawerArea);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mDrawerList.setItemChecked(finalPosition, true);
                    setTitle(mDrawerTitles[finalPosition]);



                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            FragmentManager fragmentManager = getFragmentManager();

                            if(finalPosition == 0) {


                                Fragment fragment = new PredictionsFragment();

                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                            }

                            if(finalPosition == 1){

                                Fragment fragment = new FavoritesFragment();
                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                            }

                            if(finalPosition == 2){
                                Fragment fragment = new MapFragment();
                                getSupportActionBar().setSubtitle(mSubtitle);
                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                            }

                            mDrawerLayout.closeDrawer(mDrawerArea);


                        }
                    }, 325);




                }
            }, 400);






        }
        else{
            mDrawerList.setItemChecked(position, true);
            setTitle(mDrawerTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerArea);



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    FragmentManager fragmentManager = getFragmentManager();

                    if(finalPosition == 0) {


                        Fragment fragment = new PredictionsFragment();

                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }

                    if(finalPosition == 1){

                        Fragment fragment = new FavoritesFragment();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    }

                    if(finalPosition == 2){
                        Fragment fragment = new MapFragment();
                        getSupportActionBar().setSubtitle(mSubtitle);
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    }


                }
            }, 325);


        }
















    }


    public void selectItemSecondary(int position) {
        // update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();

        if(position == 0) {//refresh weather

            fetchWeather();
        }





        if(position == 1){//about






            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .callback(new MaterialDialog.Callback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            String url = "http://www.forsyth.im";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);

                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();

                        }
                    })
                    .title("About")
                    .icon(R.drawable.ic_logo_white_nobezel)
                    .titleColorRes(R.color.accent2)
                    .content("Chapel Hill Transit for Android is developed by Justin Forsyth\n\nThis application uses data from NextBus Inc., OpenWeatherMap Inc., and Google Maps")
                    .positiveText("Visit developer page")
                    .positiveColor(Color.WHITE)
                    .negativeText("Back")
                    .negativeColor(Color.WHITE)
                    .contentColorRes(R.color.accent1)
                    .theme(Theme.DARK)
                    .build();

            ((LinearLayout)dialog.getTitleFrame().getParent().getParent()).setBackgroundResource(R.color.main);

            dialog.show();





        }

        if(position == 2){//settings

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }




    public void fetchRouteList(int fragmentID){

        RestClientNextBus.get("routeList&a=chapel-hill", null, new RoutesResponseHandler(this, fragmentID));



    }

    public void fetchDirectionStops(String routeTag, int fragmentID, Boolean animate){


            RestClientNextBus.get("routeConfig&a=chapel-hill&r=" + routeTag + "&terse", null, new DirectionsStopsPathsResponseHandler(this, fragmentID, false, animate));


            //RestClientNextBus.get("routeConfig&a=chapel-hill&r=" + routeTag, null, new DirectionsStopsPathsResponseHandler(this, true));

    }

    public void fetchPredictions(String stopID){
        RestClientNextBus.get("predictions&a=chapel-hill&stopId=" + stopID, null, new PredictionsResponseHandler(this));
    }

    public void fetchWeather(){

        if(isOnline()){
            String cityID = getResources().getString(R.string.cityid);
            RestClientOpenWeather.get(cityID, null, new WeatherResponseHandler(this));
        }
        else{

            WeatherResponseHandler wrh = new WeatherResponseHandler(this);
            setWeatherText(wrh.getRandomUNCString(), false);

        }



    }

    public void saveFavorite(View view){


        FragmentManager fragmentManager = getFragmentManager();

        PredictionsFragment fragment = (PredictionsFragment) fragmentManager.findFragmentById(R.id.content_frame);

        fragment.saveFavorite();



    }


    public void mapClick(View view){

        FragmentManager fragmentManager = getFragmentManager();

        PredictionsFragment fragment = (PredictionsFragment) fragmentManager.findFragmentById(R.id.content_frame);

        fragment.mapClick();



    }


    public void mapFabClick(View view){


        FragmentManager fragmentManager = getFragmentManager();

        MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame);

        fragment.fabClick();

    }

    public void mapBackFromRoutes(View view){

        FragmentManager fragmentManager = getFragmentManager();

        MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame);

        fragment.backFromRoutes();



    }




    public void setWeatherText(int temp, String weather) {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String time = timeFormat.format(calendar.getTime());


        String string = "" + temp + "°F\n" + weather.toUpperCase() + "\n"
                        + time;

        TextView tv = (TextView) findViewById(R.id.weather_text);

        tv.setText(string);

        YoYo.with(Techniques.FadeIn)
                .playOn(tv);


    }

    public void setWeatherText(String string, boolean weatherConnection){

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String time = timeFormat.format(calendar.getTime());

        if(weatherConnection){
            string = string + "\n" + time;
        }

        TextView tv = (TextView) findViewById(R.id.weather_text);

        tv.setText(string);

        YoYo.with(Techniques.FadeIn)
                .playOn(tv);


    }

    public void updateDrawerTime(){

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String time = timeFormat.format(calendar.getTime());

        TextView tv = (TextView) findViewById(R.id.weather_text);

        String currentText = tv.getText().toString();

        Scanner scanner = new Scanner(currentText);

        ArrayList<String> lines = new ArrayList<String>();

        while(scanner.hasNextLine()){
            lines.add(scanner.nextLine());
        }

        String string = "";

        for(int i = 0; i < lines.size() - 1; i++){

            string = string + lines.get(i) + "\n";

        }

        string = string + time;

        tv.setText(string);

//        YoYo.with(Techniques.FadeIn)
//                .playOn(tv);


    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed()
    {

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean confirmExit = sharedPreferences.getBoolean("confirmExit", true);


        if(((Object) fragment).getClass() == PredictionsFragment.class){
            PredictionsFragment predictionsFragment = (PredictionsFragment) fragment;

            int navLevel = predictionsFragment.getNavigationLevel();

            if (navLevel == 0){

                if(confirmExit){


                    showConfirmExitDialog();



                }else{
                    super.onBackPressed();
                }


            }
            else if(navLevel == 1){
                predictionsFragment.routeClick();
            }
            else if(navLevel == 2){
                predictionsFragment.directionClick();
            }
            else if(navLevel == 3){
                predictionsFragment.stopClick();
            }

        }
        else if(((Object) fragment).getClass() == MapFragment.class){

            MapFragment mapFragment = (MapFragment) fragment;

            if(mapFragment.isRouteListVisible()){
                mapFragment.backFromRoutes();
            }
            else{
                if(confirmExit){


                    showConfirmExitDialog();



                }else{
                    super.onBackPressed();
                }
            }



        }
        else{
            if(confirmExit){


                showConfirmExitDialog();



            }else{
                super.onBackPressed();
            }
        }


    }


    public void showConfirmExitDialog(){

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        HomeActivity.super.onBackPressed();


                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();

                    }
                })
                .title("Exit Chapel Hill Transit?")
                .titleColorRes(R.color.accent2)
                .customView(R.layout.dialog_confirm_exit)
                .positiveText("Yes")
                .positiveColor(Color.WHITE)
                .negativeText("No")
                .negativeColor(Color.WHITE)
                .theme(Theme.DARK)
                .build();

        //for customView (as above), call getParent() 4x
        //for listDialog, call getParent() 3x
        //for .content, call getParent() 2x
        ((LinearLayout)dialog.getTitleFrame().getParent().getParent().getParent().getParent()).setBackgroundResource(R.color.main);

        dialog.show();

    }

    public void showRateAppDialog(){


        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(myAppLinkToMarket);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(dialog.getContext(), "Unable to find app in Play Store", Toast.LENGTH_LONG).show();
                        }


                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();

                    }
                })
                .title("Rate Chapel Hill Transit?")
                .titleColorRes(R.color.accent2)
                .content("Ratings in the Play Store help a ton\n\nIf you could take a moment to give Chapel Hill Transit for Android" +
                        " a rating and a review, the developer of this application would greatly appreciate it")
                .contentColorRes(R.color.accent1)
                .positiveText("Rate in Play Store")
                .positiveColor(Color.WHITE)
                .negativeText("No thanks")
                .negativeColor(Color.WHITE)
                .theme(Theme.DARK)
                .build();

        //for customView (as above), call getParent() 4x
        //for listDialog, call getParent() 3x
        //for .content, call getParent() 2x
        ((LinearLayout)dialog.getTitleFrame().getParent().getParent()).setBackgroundResource(R.color.main);

        dialog.show();



    }

    public void confirmExitChecked(View view){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        CheckBox checkBox = (CheckBox) view;

        if(checkBox.isChecked()){
            editor.putBoolean("confirmExit", true);
            editor.commit();
            return;
        }
        else{
            editor.putBoolean("confirmExit", false);
            editor.commit();
            return;

        }


    }


    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {




                FragmentManager fm = getFragmentManager();
                Fragment ofragment = fm.findFragmentById(R.id.content_frame);

                PredictionsFragment fragment = (PredictionsFragment) ofragment;

                int navLevel = fragment.getNavigationLevel();


                if(navLevel == fragment.LEVEL_DIRECTION){
                    fragment.directionClick();
                }
                else if(navLevel == fragment.LEVEL_STOP){
                    fragment.stopClick();
                }
                else if(navLevel == fragment.LEVEL_PREDICTION) {
                    fragment.cancelRefresh();
                    fragment.scheduleRefresh();
                }
                else{

                    fragment.routeClick();

                }

                SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
                swipeLayout.setRefreshing(false);


            }
        }, 1500);


    }

    public ArrayList<Route> getRouteListCache() {
        return routeListCache;
    }

    public void setRouteListCache(ArrayList<Route> routeListCache) {
        this.routeListCache = routeListCache;
    }

    public void migrateFavorites(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        ArrayList<Favorite> oldFavorites = FavoritesFragment.buildFavoritesListOld(this);

        ArrayList<Favorite> emptyFavorites = new ArrayList<Favorite>();

        String jsonOldFavorites = gson.toJson(oldFavorites);
        String jsonEmptyFavorites = gson.toJson(emptyFavorites);



        editor.putString("favorites", jsonOldFavorites);

        editor.commit();

        String jsonFavorites = sharedPreferences.getString("favorites", jsonEmptyFavorites);


        Type type = new TypeToken<ArrayList<Favorite>>(){}.getType();
        ArrayList<Favorite> favorites = gson.fromJson(jsonFavorites, type);

        if((oldFavorites.size() == favorites.size()) && (favorites.size() > 0)){
            //Toast.makeText(this, "Successfully migrated " + Integer.toString(favorites.size()) + " favorites to new version", Toast.LENGTH_LONG).show();

        }



    }


}