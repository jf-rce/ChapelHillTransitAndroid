package com.jforce.chapelhillnextbus;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by justinforsyth on 11/13/14.
 */
public class MapActivity extends ActionBarActivity implements MapFragment.MapHost {

    ArrayList<Route> routeListCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_logo_white_nobezel_small);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();



        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.content_frame2) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MapFragment fragment = new MapFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.content_frame2, fragment).commit();
        }


    }

    public void init(){

        routeListCache = getIntent().getParcelableArrayListExtra("routeListCache");


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    public ArrayList<Route> getRouteListCache(){

        return routeListCache;

    }

    public void setRouteListCache(ArrayList<Route> routeListCache){

        this.routeListCache = routeListCache;



    }

    public void fetchRouteList(int fragmentID){

        RestClientNextBus.get("routeList&a=chapel-hill", null, new RoutesResponseHandler(this, fragmentID));


    }

    public void mapFabClick(View view){

        FragmentManager fragmentManager = getFragmentManager();

        MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame2);

        fragment.fabClick();


    }

    public void mapBackFromRoutes(View view){

        FragmentManager fragmentManager = getFragmentManager();

        MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame2);

        fragment.backFromRoutes();


    }




}
