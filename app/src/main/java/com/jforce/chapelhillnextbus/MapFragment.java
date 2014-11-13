package com.jforce.chapelhillnextbus;



import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Or use LocationManager.GPS_PROVIDER

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15.0f);
        map.animateCamera(cameraUpdate);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        init();
    }

    @Override
    public void onResume() {
        mapView.onResume();
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





        String stopLatString = "";
        String stopLonString = "";



//        Bundle extras = getActivity().getIntent().getExtras();
//        if (extras != null) {
//
//            routeTag = extras.getString("routeTag");
//            routeTitle = extras.getString("routeTitle");
//
//            dirTag = extras.getString("dirTag");
//            dirTitle = extras.getString("dirTitle");
//
//
//            stopTitle = extras.getString("stopTitle");
//            stopTag = extras.getString("stopTag");
//            stopLatString = extras.getString("stopLat");
//            stopLonString = extras.getString("stopLon");
//
//
//            stopLat = Double.parseDouble(stopLatString);
//            stopLon = Double.parseDouble(stopLonString);
//
//
//        }else{
//
//
//
//        }


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

    public void fetchPath(String routeTag){

        RestClientNextBus.get("routeConfig&a=chapel-hill&r=" + routeTag, null, new DirectionsStopsPathsResponseHandler(getActivity(), RoutesResponseHandler.MAP_ID));


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
                    map.addPolyline(polyOptions);

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

                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon))
                            .title(title)
                            .flat(true)
                            .anchor(0.5f, 0.5f)
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


    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }


    public void fabClick(){

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.map_fab);

        RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.map_route_selection_area);

        fab.hide();

        rl.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.Landing)
                .duration(500)
                .playOn(rl);

        HomeActivity homeActivity = (HomeActivity) getActivity();

        homeActivity.fetchRouteList(RoutesResponseHandler.MAP_ID);

    }

    public void backFromRoutes(){

        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.map_fab);

        final RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.map_route_selection_area);



        YoYo.with(Techniques.TakingOff)
                .duration(500)
                .playOn(rl);

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                rl.setVisibility(View.GONE);
                fab.show();


            }
        }, 250);


    }

    public void generateRouteList(NodeList nodeList){

        routeList.clear();

        listView.setAdapter(routeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                map.clear();
                fetchPath(routeList.get(position).getTag());
                backFromRoutes();

            }

        });


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




}