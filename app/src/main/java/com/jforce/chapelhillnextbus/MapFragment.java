package com.jforce.chapelhillnextbus;



import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.melnykov.fab.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MapFragment extends Fragment {

    MapView mapView;
    GoogleMap map;

    private String routeTag;
    private String routeTitle;

    private String dirTag;
    private String dirTitle;

    private String stopTag;
    private String stopTitle;
    private double stopLat;
    private double stopLon;

    private ArrayList<Marker> stopMarkers;
    private ArrayList<Route> routeList;

    private RouteArrayAdapter routeAdapter;

    private Document doc;

    private ListView listView;

    private MapHost mCallback;

    private Route currentlyDrawnRoute;

    private LatLngBounds currentBounds;

    private Prediction passedPrediction;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        //map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }



        return v;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (MapHost) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        init();



    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onResume() {
        mapView.onResume();

        if(currentBounds != null){

            int padding = 60; // offset from edges of the map in pixels
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(currentBounds,
                    padding);
            //map.moveCamera(cameraUpdate);
            map.animateCamera(cameraUpdate);

        }



        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void init() {



        routeList = new ArrayList<Route>();

        stopMarkers = new ArrayList<Marker>();

        routeAdapter = new RouteArrayAdapter(getActivity(), routeList);

        listView = (ListView) getActivity().findViewById(R.id.map_listview);

        ArrayList<Route> routeListCache = mCallback.getRouteListCache();


        if((routeListCache != null) && (routeListCache.size() != 0)) {

                routeList.addAll(routeListCache);

        }
        else{

            mCallback.fetchRouteList(RoutesResponseHandler.MAP_ID);

//            Toast toast = Toast.makeText(getActivity(), "fetch", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();

        }

        listView.setAdapter(routeAdapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                map.clear();

                fetchPath(routeList.get(position).getTag(), true);

                String title = routeList.get(position).getTitle();


                if(title.equals("Route 420")){
                    ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(routeList.get(position).getTitle());

                }
                else{
                    ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(title + " Route");



                }

                setCurrentlyDrawnRoute(routeList.get(position));

                backFromRoutes();

            }

        });

        routeAdapter.notifyDataSetChanged();



        currentlyDrawnRoute = null;

        String stopLatString = "";
        String stopLonString = "";



        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {


            stopLatString = extras.getString("stopLat");
            stopLonString = extras.getString("stopLon");

            if(stopLatString != null){

                stopLat = Double.parseDouble(stopLatString);
                stopLon = Double.parseDouble(stopLonString);

                routeTag = extras.getString("routeTag");
                routeTitle = extras.getString("routeTitle");

                dirTag = extras.getString("dirTag");
                dirTitle = extras.getString("dirTitle");

                stopTitle = extras.getString("stopTitle");
                stopTag = extras.getString("stopTag");





                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(stopLat, stopLon), 18.0f);
                map.animateCamera(cameraUpdate);



                if(routeTitle.equals("Route 420")){
                    ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(routeTitle);

                }
                else{
                    ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(routeTitle + " Route");
                }

                setCurrentlyDrawnRoute(new Route(routeTag, routeTitle));

                fetchPath(routeTag, false);




            }
            else{


                zoomToLocation();
            }




        }else{

            zoomToLocation();

        }





        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

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


    }

    public void zoomToLocation(){


        String locationProvider = LocationManager.NETWORK_PROVIDER;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Or use LocationManager.GPS_PROVIDER

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15.0f);
        map.animateCamera(cameraUpdate);

    }



    public void fetchPath(String routeTag, boolean zoom){

        RestClientNextBus.get("routeConfig&a=chapel-hill&r=" + routeTag, null, new DirectionsStopsPathsResponseHandler(getActivity(), RoutesResponseHandler.MAP_ID, zoom));


    }


    public void drawPath(Document doc, boolean zoomToBounds) {


        this.doc = doc;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

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

                            LatLng point = new LatLng(lat, lon);

                            polyOptions.add(point);

                            builder.include(point);



                        }
                    }

                    // Get back the mutable Polyline
                    polyOptions.color(getResources().getColor(R.color.main));
                    map.addPolyline(polyOptions);

                }
            }
        }

        LatLngBounds bounds = builder.build();

        currentBounds = bounds;

        drawStops(doc, bounds, zoomToBounds);

    }


    public void drawStops(Document doc, LatLngBounds bounds, boolean zoomToBounds){

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

                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon))
                            .title(title)
                            .flat(true)
                            .anchor(0.5f, 0.5f)
                                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image_lens)));
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image_brightness_1)));

                    if(title.equals(stopTitle)){
                        marker.showInfoWindow();
                    }

                    stopMarkers.add(marker);



                }

            }
        }
        if(zoomToBounds) {

            int padding = 60; // offset from edges of the map in pixels
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,
                    padding);
            //map.moveCamera(cameraUpdate);
            map.animateCamera(cameraUpdate);
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


    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }


    public void fabClick(){

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.map_fab);

        LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.map_route_selection_area);

        fab.hide();

        ll.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.Landing)
                .duration(500)
                .playOn(ll);


    }

    public void backFromRoutes(){

        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.map_fab);

        final LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.map_route_selection_area);



        YoYo.with(Techniques.TakingOff)
                .duration(500)
                .playOn(ll);

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                ll.setVisibility(View.GONE);
                fab.show();


            }
        }, 250);


    }

    public void generateRouteList(NodeList nodeList){

        routeList.clear();

//        listView.setAdapter(routeAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view,
//                                    int position, long id) {
//                map.clear();
//                fetchPath(routeList.get(position).getTag(), true);
//                backFromRoutes();
//
//            }
//
//        });
//


        for (int i = 0; i < nodeList.getLength(); i++) {

            Node nNode = nodeList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                String tag = eElement.getAttribute("tag");
                String title = eElement.getAttribute("title");

                routeList.add(new Route(tag, title));



            }

        }

        routeAdapter.notifyDataSetChanged();



    }


    public interface MapHost {



        public ArrayList<Route> getRouteListCache();

        public void setRouteListCache(ArrayList<Route> routeListCache);

        public void fetchRouteList(int fragmentID);

        public void mapFabClick(View view);

        public void mapBackFromRoutes(View view);

    }


    public void setCurrentlyDrawnRoute(Route route){
        currentlyDrawnRoute = route;

    }

    public Route getCurrentlyDrawnRoute(){

        return currentlyDrawnRoute;


    }

    public void resetSubtitle(){

        if(currentlyDrawnRoute != null) {

            if (currentlyDrawnRoute.getTitle().equals("Route 420")) {
                ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(currentlyDrawnRoute.getTitle());

            } else {
                ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(currentlyDrawnRoute.getTitle() + " Route");
            }
        }
    }




}