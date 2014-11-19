package com.jforce.chapelhillnextbus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class MapStopActivity extends ActionBarActivity {

    private GoogleMap mMap;

    private String routeTag;
    private String routeTitle;

    private String dirTag;
    private String dirTitle;

    private String stopTag;
    private String stopTitle;
    private double stopLat;
    private double stopLon;

    private ArrayList<Marker> stopMarkers;

    private Document doc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_stop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_logo_white_nobezel_small);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        getSupportActionBar().setTitle("Map");

        if(routeTitle.equals("Route 420")){
            getSupportActionBar().setSubtitle(routeTitle);
        }
        else{
            getSupportActionBar().setSubtitle(routeTitle + " Route");
        }


        //getSupportActionBar().setSubtitle(dirTitle);

        fetchPath(routeTag);



    }


    public void init(){

        stopMarkers = new ArrayList<Marker>();

        String stopLatString = "";
        String stopLonString = "";


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            routeTag = extras.getString("routeTag");
            routeTitle = extras.getString("routeTitle");

            dirTag = extras.getString("dirTag");
            dirTitle = extras.getString("dirTitle");


            stopTitle = extras.getString("stopTitle");
            stopTag = extras.getString("stopTag");
            stopLatString = extras.getString("stopLat");
            stopLonString = extras.getString("stopLon");


        }

        stopLat = Double.parseDouble(stopLatString);
        stopLon = Double.parseDouble(stopLonString);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(stopLat, stopLon), 15.0f) );


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {


                float zoom = cameraPosition.zoom;

                if (zoom >= 15.0f) {
                    showStops();
                } else if (zoom < 15.0f) {
                    hideStops();
                }

            }
        });





//        Marker marker = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(stopLat, stopLon))
//                .title(stopTitle)
//                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image_lens)));
//
//        marker.showInfoWindow();




    }

    public void fetchPath(String routeTag){

        RestClientNextBus.get("routeConfig&a=chapel-hill&r=" + routeTag, null, new DirectionsStopsPathsResponseHandler(this, RoutesResponseHandler.MAP_ID, true));



    }


    public void drawPath(Document doc) {


        this.doc = doc;


        NodeList pathNodeList = doc.getElementsByTagName("path");

        for (int i = 0; i < pathNodeList.getLength(); i++) {

            Node pathNode = pathNodeList.item(i);

            if (pathNode.getNodeType() == Node.ELEMENT_NODE) {

                Element pathElement = (Element) pathNode;

                if (pathElement.hasChildNodes()) {

                    NodeList pointNodeList = pathElement.getElementsByTagName("point");

                    PolylineOptions polyOptions = new PolylineOptions();

                    for (int j = 0; j < pointNodeList.getLength(); j++) {

                        Node pointNode = pointNodeList.item(j);

                        if (pointNode.getNodeType() == Node.ELEMENT_NODE){

                            Element pointElement = (Element) pointNode;

                            String latString = pointElement.getAttribute("lat");
                            String lonString = pointElement.getAttribute("lon");

                            double lat = Double.parseDouble(latString);
                            double lon = Double.parseDouble(lonString);

                            polyOptions.add(new LatLng(lat, lon));



                        }
                    }

                    // Get back the mutable Polyline
                    polyOptions.color(getResources().getColor(R.color.main));
                    mMap.addPolyline(polyOptions);

                }
            }
        }

        drawStops(doc);

    }


    public void drawStops(Document doc){

        NodeList stopNodeList = doc.getElementsByTagName("stop");

        for (int i = 0; i < stopNodeList.getLength(); i++) {

            Node stopNode = stopNodeList.item(i);

            if (stopNode.getNodeType() == Node.ELEMENT_NODE) {

                Element stopElement = (Element) stopNode;

                if(stopElement.hasAttribute("lat")){

                    String title = stopElement.getAttribute("title");
                    String latString = stopElement.getAttribute("lat");
                    String lonString = stopElement.getAttribute("lon");

                    double lat = Double.parseDouble(latString);
                    double lon = Double.parseDouble(lonString);

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon))
                            .title(title)
                            .flat(true)
                                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image_lens)));

                    if(title.equals(stopTitle)){
                        marker.showInfoWindow();
                    }

                    stopMarkers.add(marker);



                }

            }
        }
    }


    public void showStops(){



        for(Marker marker : stopMarkers){
            marker.setVisible(true);


        }
    }

    public void hideStops(){

        for (Marker marker : stopMarkers){

            marker.setVisible(false);

        }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_stop, menu);
        return true;
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

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }







}
