package com.jforce.chapelhillnextbus;



import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PredictionsFragment extends Fragment implements OnDismissCallback {

    public final int LEVEL_ROUTE = 0;
    public final int LEVEL_DIRECTION = 1;
    public final int LEVEL_STOP = 2;
    public final int LEVEL_PREDICTION = 3;


    private final int INITIAL_DELAY_MILLIS = 300;

    private int navigationLevel;

    private TextView routeNav;
    private TextView dirNav;
    private TextView stopNav;

    private ListView listView;

    private ViewGroup rootView;

    private View bottomLine;

    private Route storedRoute;
    private Direction storedDirection;
    private Stop storedStop;

    private ArrayList<Route> routeList;
    private ArrayList<Direction> directionList;
    private ArrayList<Stop> stopList;
    private ArrayList<Stop> stopByDirectionList;
    private ArrayList<NodeList> stopsByDirectionNodeListList;
    private ArrayList<Prediction> predictionList;

    private Document directionsAndStopDocument;

    private boolean shouldRefresh;
    Timer timer;


    private NodeList directionNodeList;

    private TreeMap<String, Stop> stopMap;

    private RouteArrayAdapter routeAdapter;
    private DirectionArrayAdapter directionAdapter;
    private StopArrayAdapter stopAdapter;

    private SwitchCompat refreshSwitch;

    private FloatingActionButton fab;

    private SwipeRefreshLayout swipeLayout;

    private SlidingUpPanelLayout slidingPanel;

    Bundle savedInstanceState;


    public PredictionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_predictions, container, false);

        init();
        this.savedInstanceState = savedInstanceState;










        return rootView;




    }

    @Override
    public void onStart(){
        super.onStart();
        initListeners();

    }

    @Override
    public void onPause(){
        super.onPause();
        cancelRefresh();
        //mapView.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        cancelRefresh();

    }

    @Override
    public void onResume(){
        super.onResume();

        int level = getNavigationLevel();

        if(level == LEVEL_PREDICTION) {

            if (shouldRefresh) {
                scheduleRefresh();
            }
//            else {
//                HomeActivity homeActivity = (HomeActivity) getActivity();
//                homeActivity.fetchPredictions(storedStop.getID());
//            }
        }
        else if(level == LEVEL_STOP){
            showListView();


            int index = directionList.indexOf(storedDirection);

            generateStopList(index, true);
        }
        else if(level == LEVEL_DIRECTION){
            showListView();
            generateDirectionList(directionsAndStopDocument, true);
        }
        else{
            showListView();
            HomeActivity homeActivity = (HomeActivity) getActivity();
            ArrayList<Route> routeListCache = homeActivity.getRouteListCache();

            if((routeListCache != null) && (routeListCache.size() != 0)) {

                generateRouteListBackground(routeListCache);


            }
            else{
                homeActivity.fetchRouteList(RoutesResponseHandler.PREDICTIONS_ID);
            }
        }

       //


    }

    public void initListeners(){

        routeNav = (TextView) getActivity().findViewById(R.id.route_nav_text);
        routeNav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                routeClick();
            }
        });

        dirNav = (TextView) getActivity().findViewById(R.id.dir_nav_text);
        dirNav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                directionClick();
            }
        });

        stopNav = (TextView) getActivity().findViewById(R.id.stop_nav_text);
        stopNav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                stopClick();
            }
        });

        swipeLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener((HomeActivity) getActivity());
        swipeLayout.setColorScheme(R.color.NAVY,
                R.color.CAROLINA_BLUE,
                R.color.NAVY,
                R.color.CAROLINA_BLUE);
        swipeLayout.setSoundEffectsEnabled(true);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
                swipeContainer.setEnabled(topRowVerticalPosition >= 0);
            }
        });

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide(false);

        slidingPanel = (SlidingUpPanelLayout) getActivity().findViewById(R.id.predictions_text_group);
        slidingPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelExpanded(View panel) {


            }

            @Override
            public void onPanelCollapsed(View panel) {


            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });


        slidingPanel.setDragView(getActivity().findViewById(R.id.drag_area));


        HomeActivity homeActivity = (HomeActivity) getActivity();

        ArrayList<Route> routeListCache = homeActivity.getRouteListCache();

        if((routeListCache != null) && (routeListCache.size() != 0)) {

            generateRouteListBackground(routeListCache);


        }
        else{
            homeActivity.fetchRouteList(RoutesResponseHandler.PREDICTIONS_ID);
        }






    }




    public void init(){

        routeList = new ArrayList<Route>();
        directionList = new ArrayList<Direction>();
        stopList = new ArrayList<Stop>();
        stopByDirectionList = new ArrayList<Stop>();
        predictionList = new ArrayList<Prediction>();

        stopMap = new TreeMap<String, Stop>();

        routeAdapter = new RouteArrayAdapter(getActivity(), routeList);
        directionAdapter = new DirectionArrayAdapter(getActivity(), directionList, null);
        stopAdapter = new StopArrayAdapter(getActivity(), stopByDirectionList, null);

        navigationLevel = 0;

        listView = (ListView) rootView.findViewById(R.id.predictions_listview);

        shouldRefresh = true;

        timer = new Timer();

    }


    @Override
    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            routeAdapter.remove(null);
        }
    }

    public void generateRouteList(NodeList nodeList){

        routeList.clear();

        listView.setAdapter(routeAdapter);


//        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(routeAdapter, this));
//        swingBottomInAnimationAdapter.setAbsListView(listView);
//
//        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
//        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
//
//        listView.setAdapter(swingBottomInAnimationAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                //final String item = (String) parent.getItemAtPosition(position);

                //hideListView();
                storedRoute = routeList.get(position);


                //HomeActivity homeActivity = (HomeActivity) getActivity();

                HomeActivity homeActivity = (HomeActivity) getActivity();

                homeActivity.fetchDirectionStops(storedRoute.getTag(), RoutesResponseHandler.PREDICTIONS_ID, true);

                setNavigationLevel(LEVEL_DIRECTION);

//                Toast toast = Toast.makeText(getActivity(), "getting directions for: " + routeTagList.get(position), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
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

        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.setRouteListCache(routeList);



        showListView();
        colorizeRoute(true);


    }


    public void generateRouteListBackground(ArrayList<Route> routeListCache){

        routeList.clear();

        listView.setAdapter(routeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                //final String item = (String) parent.getItemAtPosition(position);

                //hideListView();
                storedRoute = routeList.get(position);


                //HomeActivity homeActivity = (HomeActivity) getActivity();

                HomeActivity homeActivity = (HomeActivity) getActivity();

                homeActivity.fetchDirectionStops(storedRoute.getTag(), RoutesResponseHandler.PREDICTIONS_ID, true);

                setNavigationLevel(LEVEL_DIRECTION);

//                Toast toast = Toast.makeText(getActivity(), "getting directions for: " + routeTagList.get(position), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
            }

        });

        routeList.addAll(routeListCache);

        routeAdapter.notifyDataSetChanged();

        colorizeRoute(false);

    }



    public void generateDirectionList(Document doc, boolean animate){

        directionList.clear();

        directionsAndStopDocument = null;

        directionsAndStopDocument = doc;

        directionNodeList = doc.getElementsByTagName("direction");

        for (int i = 0; i < directionNodeList.getLength(); i++) {

            Node nNode = directionNodeList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                String tag = eElement.getAttribute("tag");
                String title = eElement.getAttribute("title");

                directionList.add(new Direction(tag, title));



            }

        }


        directionAdapter = new DirectionArrayAdapter(getActivity(), directionList, storedRoute.getTitle());

        listView.setAdapter(directionAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                //hideListView();

                storedDirection = directionList.get(position);
                generateStopList(position, true);
//                Toast toast = Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
                setNavigationLevel(LEVEL_STOP);
            }

        });

        directionAdapter.notifyDataSetChanged();

        if(animate) {
            colorizeDirection(true);
            showListView();
        }
        else{
            for(int i = 0; i < directionList.size(); i++){

                if(storedDirection.getTag().equals(directionList.get(i).getTag())){
                    generateStopList(i, false);
                }
            }
        }

    }

    public void generateStopList(int directionPosition, boolean animate){

        stopList.clear();
        stopByDirectionList.clear();

        stopsByDirectionNodeListList =  new ArrayList<NodeList>();

        //separate the stops by direction and put them into an
        //ArrayList of NodeLists, with position in ArrayList mapped to directionPosition

        for (int i = 0; i < directionNodeList.getLength(); i++) {

            Node node = directionNodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {


                Element element = (Element) node;


                stopsByDirectionNodeListList.add(element.getElementsByTagName("stop"));

            }

        }


        //get the detailed list of un-directioned stops
        //the list will repeat each stop once because that's how the API works...
        //we will only iterate through the first half of stopNodeList to compensate

        NodeList routeNodeList = directionsAndStopDocument.getElementsByTagName("route");

        NodeList stopNodeList = null;
        for (int i = 0; i < routeNodeList.getLength(); i++) {

            Node node = routeNodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {


                Element element = (Element) node;

                stopNodeList = element.getElementsByTagName("stop");


            }

        }

        //Iterate through stopNodeList
        //Populate stopList
        //Populate stopMap so that stopTags can be mapped to stopIds + stopTitles

        for (int i = 0; i < stopNodeList.getLength()/2; i++) {

            Node node = stopNodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {


                Element element = (Element) node;

                String stopID = element.getAttribute("stopId");
                String stopTitle = element.getAttribute("title");
                String stopTag = element.getAttribute("tag");
                String stopLat = element.getAttribute("lat");
                String stopLon = element.getAttribute("lon");

                Stop stop = new Stop(stopTag, stopTitle, stopID, stopLat, stopLon);

                stopList.add(stop);

                //Log.e("test", "adding... tag: " + stopTag + ", title: " + stopTitle + ", id: " + stopID);

                stopMap.put(stopTag, stop);

            }

        }


        NodeList stopsByDirectionNodeList = stopsByDirectionNodeListList.get(directionPosition);

        //map the stop tag to the detailed tag information
        //populate directed stop lists
        if (stopNodeList != null){


            for (int i = 0; i < stopsByDirectionNodeList.getLength(); i++) {

                Node node = stopsByDirectionNodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {


                    Element element = (Element) node;

                    String stopTag = element.getAttribute("tag");

                    Stop stop = stopMap.get(stopTag);

                    stopByDirectionList.add(stop);

                }

            }


        }

        stopAdapter = new StopArrayAdapter(getActivity(), stopByDirectionList, storedRoute.getTitle());

        listView.setAdapter(stopAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                storedStop = stopByDirectionList.get(position);

                if(shouldRefresh){
                    scheduleRefresh();
                }
                else{

                    HomeActivity homeActivity = (HomeActivity) getActivity();

                    homeActivity.fetchPredictions(storedStop.getID());

                }

                swipeLayout.setEnabled(false);

                setNavigationLevel(LEVEL_PREDICTION);
//                Toast toast = Toast.makeText(getActivity(), stopID, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
            }

        });

        stopAdapter.notifyDataSetChanged();

        if(animate){
            showListView();
            colorizeStop(true);
        }


    }


    class RefreshPredictionsTimerTask extends TimerTask{

        public void run() {



            //HomeActivity homeActivity = (HomeActivity) getActivity();
            HomeActivity homeActivity = (HomeActivity) getActivity();

            homeActivity.fetchPredictions(storedStop.getID());


        }


    }


    public void generatePredictions(Document doc){


        predictionList.clear();

        final RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.other_preds);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                relativeLayout.removeAllViews();
                hidePredictionTexts();

            }
        });




        NodeList predictionsNodeList = doc.getElementsByTagName("predictions");

        for (int i = 0; i < predictionsNodeList.getLength(); i++) {

                Node predictionsNode = predictionsNodeList.item(i);

                if (predictionsNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element predictionsElement = (Element) predictionsNode;
                    String routeTag = predictionsElement.getAttribute("routeTag");
                    String routeTitle = predictionsElement.getAttribute("routeTitle");
                    Route route = new Route(routeTag, routeTitle);

                    String directionTitle = null;
                    String directionTag = null;

                    String timeInMinutes = null;

                    boolean alreadyAdded = false;


                    if(predictionsElement.hasAttribute("dirTitleBecauseNoPredictions")){
                       directionTitle = predictionsElement.getAttribute("dirTitleBecauseNoPredictions");
                    }

                    if(predictionsElement.hasChildNodes()){

                        NodeList directionNodeList = predictionsElement.getElementsByTagName("direction");




                        for (int j = 0; j < directionNodeList.getLength(); j++) {

                            Node directionNode = directionNodeList.item(j);

                            if (directionNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element directionElement = (Element) directionNode;

                                directionTitle = directionElement.getAttribute("title");


                                if(directionElement.hasChildNodes()) {

                                    NodeList predictionNodeList = directionElement.getElementsByTagName("prediction");

                                    for (int k = 0; k < predictionNodeList.getLength(); k++) {

                                        Node predictionNode = predictionNodeList.item(k);

                                        if (predictionNode.getNodeType() == Node.ELEMENT_NODE) {

                                            Element predictionElement = (Element) predictionNode;

                                            timeInMinutes = predictionElement.getAttribute("minutes");
                                            directionTag = predictionElement.getAttribute("dirTag");

                                            Direction direction = new Direction(directionTag, directionTitle);

                                            Prediction prediction = new Prediction(route, direction, timeInMinutes);

                                            predictionList.add(prediction);

                                            alreadyAdded = true;





                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!alreadyAdded) {

                        Direction direction = new Direction(directionTag, directionTitle);

                        Prediction prediction = new Prediction(route, direction, timeInMinutes);

                        predictionList.add(prediction);

                    }

                }

        }



        final TextView routeInfoTV = (TextView) getActivity().findViewById(R.id.route_info_text);
        final TextView dirInfoTV = (TextView) getActivity().findViewById(R.id.dir_info_text);
        final TextView stopInfoTV = (TextView) getActivity().findViewById(R.id.stop_info_text);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                routeInfoTV.setText(storedRoute.getTitle() + " Route");
                dirInfoTV.setText(storedDirection.getTitle());
                stopInfoTV.setText(storedStop.getTitle());

            }
        });



        final ArrayList<Prediction> nonMatchingPredictionList = new ArrayList<Prediction>();

        int matchingPredictions = 0;



        for(int i = 0; i < predictionList.size(); i++){

            Prediction prediction  = predictionList.get(i);
            String routeTitle = prediction.getRoute().getTitle();
            String directionTitle = prediction.getDirection().getTitle();
            final String timeInMinutes = prediction.getTimeInMinutes();

            if (!(timeInMinutes == null)){


                if(routeTitle.equals(storedRoute.getTitle()) && directionTitle.equals(storedDirection.getTitle())){

                    matchingPredictions++;

                    if(matchingPredictions > 3){
                        break;
                    }



                    if(matchingPredictions == 1){

                        final TextView firstPredText = (TextView) getActivity().findViewById(R.id.first_pred_text);
                        final TextView firstPredMin = (TextView) getActivity().findViewById(R.id.first_pred_min);

                        final LinearLayout firstPredContainer = (LinearLayout) getActivity().findViewById(R.id.first_pred);
                        final LinearLayout secondPredContainer = (LinearLayout) getActivity().findViewById(R.id.second_pred);
                        final LinearLayout thirdPredContainer = (LinearLayout) getActivity().findViewById(R.id.third_pred);


                        ViewGroup.LayoutParams params = firstPredContainer.getLayoutParams();


                        final float scale = getActivity().getResources().getDisplayMetrics().density;
                        final int pixels = (int) (120 * scale + 0.5f);

                        params.width = pixels;

                        final ViewGroup.LayoutParams newParams = params;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                firstPredText.setVisibility(View.VISIBLE);
                                firstPredMin.setVisibility(View.VISIBLE);


                                //timeInMinutes
                                firstPredText.setText(timeInMinutes);
                                firstPredMin.setText(" min");

                                firstPredContainer.setVisibility(View.VISIBLE);
                                firstPredContainer.setLayoutParams(newParams);

                                secondPredContainer.setVisibility(View.GONE);

                                thirdPredContainer.setVisibility(View.GONE);



                            }
                        });




                    }
                    else if(matchingPredictions == 2){

                        final TextView secondPredText = (TextView) getActivity().findViewById(R.id.second_pred_text);
                        final TextView secondPredMin = (TextView) getActivity().findViewById(R.id.second_pred_min);


                        final LinearLayout secondPredContainer = (LinearLayout) getActivity().findViewById(R.id.second_pred);
                        final LinearLayout thirdPredContainer = (LinearLayout) getActivity().findViewById(R.id.third_pred);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                secondPredText.setVisibility(View.VISIBLE);
                                secondPredMin.setVisibility(View.VISIBLE);
                                //timeInMinutes
                                secondPredText.setText(timeInMinutes);
                                secondPredMin.setText(" min");

                                secondPredContainer.setVisibility(View.VISIBLE);

                                thirdPredContainer.setVisibility(View.GONE);

                            }
                        });




                    }
                    else if(matchingPredictions == 3){

                        final TextView thirdPredText = (TextView) getActivity().findViewById(R.id.third_pred_text);
                        final TextView thirdPredMin = (TextView) getActivity().findViewById(R.id.third_pred_min);
                        final LinearLayout thirdPredContainer = (LinearLayout) getActivity().findViewById(R.id.third_pred);



                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                thirdPredText.setVisibility(View.VISIBLE);
                                thirdPredMin.setVisibility(View.VISIBLE);
                                thirdPredContainer.setVisibility(View.VISIBLE);

                                thirdPredText.setText(timeInMinutes);
                                thirdPredMin.setText(" min");

                            }
                        });




                    }






                }
                else{

                   nonMatchingPredictionList.add(prediction);


                }

            }


        }

        if(matchingPredictions == 0){

            final TextView firstPredText = (TextView) getActivity().findViewById(R.id.first_pred_text);
            final TextView firstPredMin = (TextView) getActivity().findViewById(R.id.first_pred_min);

            final LinearLayout firstPredContainer = (LinearLayout) getActivity().findViewById(R.id.first_pred);
            final LinearLayout secondPredContainer = (LinearLayout) getActivity().findViewById(R.id.second_pred);
            final LinearLayout thirdPredContainer = (LinearLayout) getActivity().findViewById(R.id.third_pred);

            ViewGroup.LayoutParams params = firstPredContainer.getLayoutParams();


            final float scale = getActivity().getResources().getDisplayMetrics().density;
            final int pixels = (int) (200 * scale + 0.5f);

            params.width = pixels;

            final ViewGroup.LayoutParams newParams = params;



            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    firstPredText.setVisibility(View.VISIBLE);
                    firstPredText.setText("No predictions");
                    firstPredMin.setVisibility(View.GONE);

                    firstPredContainer.setVisibility(View.VISIBLE);
                    firstPredContainer.setLayoutParams(newParams);

                    secondPredContainer.setVisibility(View.GONE);

                    thirdPredContainer.setVisibility(View.GONE);

                }
            });




        }






        //FAKE PREDS FOR SCEENS
        //---------------------------------------


//        final TextView firstPredText = (TextView) getActivity().findViewById(R.id.first_pred_text);
//        final TextView firstPredMin = (TextView) getActivity().findViewById(R.id.first_pred_min);
//
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                firstPredText.setVisibility(View.VISIBLE);
//                firstPredMin.setVisibility(View.VISIBLE);
//
//
//                //timeInMinutes
//                firstPredText.setText("5");
//                firstPredMin.setText(" min");
//
//            }
//        });
//
//
//
//        final TextView secondPredText = (TextView) getActivity().findViewById(R.id.second_pred_text);
//        final TextView secondPredMin = (TextView) getActivity().findViewById(R.id.second_pred_min);
//
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                secondPredText.setVisibility(View.VISIBLE);
//                secondPredMin.setVisibility(View.VISIBLE);
//                //timeInMinutes
//                secondPredText.setText("9");
//                secondPredMin.setText(" min");
//
//            }
//        });
//
//
//
//
//
//
//        final TextView thirdPredText = (TextView) getActivity().findViewById(R.id.third_pred_text);
//        final TextView thirdPredMin = (TextView) getActivity().findViewById(R.id.third_pred_min);
//
//
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                thirdPredText.setVisibility(View.VISIBLE);
//                thirdPredMin.setVisibility(View.VISIBLE);
//
//                thirdPredText.setText("15");
//                thirdPredMin.setText(" min");
//
//            }
//        });


        //END FAKE PREDS
        //-------------------------------------

        //nonMatchingPredictionList.size() > 0

        if(nonMatchingPredictionList.size() > 0){

            final TextView otherPredsText = (TextView) getActivity().findViewById(R.id.other_preds_text);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    otherPredsText.setText("Other predictions serving same stop");
                    generateNonMatchingPredictions(nonMatchingPredictionList);
                    slidingPanel.expandPanel();
                    //Open the SlideUpLayout
//
//                    ArrayList<Prediction> preds = new ArrayList<Prediction>();
//
//                    Route r1 = new Route("U", "U");
//                    Route r2 = new Route("N", "N");
//                    Route r3 = new Route("C", "C");
////                    Route r4 = new Route("A", "A");
////                    Route r5 = new Route("B", "B");
////                    Route r6 = new Route("Z", "Z");
////                    Route r7 = new Route("X", "X");
////                    Route r8 = new Route("W", "W");
//
//
//                    Direction d1 = new Direction("franklin", "To Dean Dome");
//                    Direction d2 = new Direction("franklin", "To Nat'l Championship");
//                    Direction d3 = new Direction("franklin", "To Title Town, USA");
//                    String t1 = "57";
//                    String t2 = "82";
//                    String t3 = "93";
//
//                    preds.add(new Prediction(r1, d1, t1));
//                    preds.add(new Prediction(r2, d2, t2));
//                    preds.add(new Prediction(r3, d3, t3));
////                    preds.add(new Prediction(r4, d1, t1));
////                    preds.add(new Prediction(r5, d2, t2));
////                    preds.add(new Prediction(r6, d3, t3));
////                    preds.add(new Prediction(r7, d1, t1));
////                    preds.add(new Prediction(r8, d2, t2));
//
//
//                    generateNonMatchingPredictions(preds);



                }
            });





        }
        else{
            final TextView otherPredsText = (TextView) getActivity().findViewById(R.id.other_preds_text);


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {



                    otherPredsText.setText("No other predictions serving same stop");
                    slidingPanel.collapsePanel();

                }
            });



        }


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                hideListViewShowPredictions();
                colorizeStopArrow();

            }
        });

        refreshSwitch = (SwitchCompat) getActivity().findViewById(R.id.refresh_switch);

        refreshSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                toggleRefresh(v);
            }


        });

        SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm aa");
        Calendar ATime = Calendar.getInstance();
        final String lastUpdate = "Updated " + TimeFormat.format(ATime.getTime());

        final TextView lastUpdateTV = (TextView) getActivity().findViewById(R.id.updated_text);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                lastUpdateTV.setText(lastUpdate);
                swipeLayout.setEnabled(false);
                setImageBackground();

            }
        });










    }

    public void generateNonMatchingPredictions(ArrayList<Prediction> list){

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.other_preds);

        ArrayList<Prediction> sanitizedPreds = new ArrayList<Prediction>();


        ArrayList<Prediction> alreadyAdded = new ArrayList<Prediction>();

        int count = 0;


        for(int i = 0; i < list.size(); i++){

            Prediction prediction = list.get(i);

            boolean skip = false;


            for(int j = 0; j < alreadyAdded.size(); j++){

                if (prediction.getRoute().getTag().equals(alreadyAdded.get(j).getRoute().getTag()) &&
                    prediction.getDirection().getTag().equals(alreadyAdded.get(j).getDirection().getTag())) {

                    skip = true;

                }
            }

            alreadyAdded.add(prediction);

            if(!skip) {


                sanitizedPreds.add(list.get(i));

//                LinearLayout horizontal = new LinearLayout(getActivity());
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, 10, 0, 0);
//                horizontal.setLayoutParams(params);
//                horizontal.setOrientation(LinearLayout.HORIZONTAL);
//
//                LinearLayout vertical = new LinearLayout(getActivity());
//                vertical.setLayoutParams(new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//                vertical.setOrientation(LinearLayout.VERTICAL);
//
//                TextView route = new TextView(getActivity());
//                route.setLayoutParams(new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//                if(list.get(i).getRoute().getTitle().equals("Route 420")){
//                    route.setText(list.get(i).getRoute().getTitle());
//                }
//                else{
//                    route.setText(list.get(i).getRoute().getTitle() + " Route");
//                }
//
//                route.setTextColor(HomeActivity.NAVY);
//                route.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//
//                TextView direction = new TextView(getActivity());
//                direction.setLayoutParams(new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//                direction.setText(list.get(i).getDirection().getTitle());
//                direction.setTextColor(HomeActivity.NAVY);
//                direction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//
//                vertical.addView(route);
//                vertical.addView(direction);
//
//                TextView divider = new TextView(getActivity());
//                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                dividerParams.setMargins(30, 0, 30, 0);
//                divider.setLayoutParams(dividerParams);
//                divider.setText("-");
//                divider.setTextColor(HomeActivity.CAROLINA_BLUE);
//                divider.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//
//
//                TextView time = new TextView(getActivity());
//                LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                time.setLayoutParams(timeParams);
//                time.setText(list.get(i).getTimeInMinutes());
//                time.setTextColor(HomeActivity.NAVY);
//                time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//
//                TextView min = new TextView(getActivity());
//                min.setLayoutParams(new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//                min.setText(" min");
//                min.setTextColor(HomeActivity.NAVY);
//                min.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//
//                horizontal.addView(vertical);
//                horizontal.addView(divider);
//                horizontal.addView(time);
//                horizontal.addView(min);
//
//                horizontal.setId(count+ 1);
//
//                if (i > 0) {
//                    params.addRule(RelativeLayout.BELOW, count);
//                }
//
//                count++;
//
//                horizontal.setLayoutParams(params);
//
//                relativeLayout.addView(horizontal);
            }

        }


        final ListView listView = new ListView(getActivity());

        listView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));


        Collections.sort(sanitizedPreds);

        PredictionArrayAdapter adapter = new PredictionArrayAdapter(getActivity(), sanitizedPreds);

        listView.setAdapter(adapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
//                SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
//                swipeContainer.setEnabled(topRowVerticalPosition >= 0);


//                if(topRowVerticalPosition >= 0){
//                    slidingPanel
//                }


            }
        });


        relativeLayout.addView(listView);

        //fab.attachToListView(listView);

        swipeLayout.setEnabled(false);



    }





    public void hidePredictionTexts(){

         LinearLayout firstPredContainer = (LinearLayout) getActivity().findViewById(R.id.first_pred);
         LinearLayout secondPredContainer = (LinearLayout) getActivity().findViewById(R.id.second_pred);
         LinearLayout thirdPredContainer = (LinearLayout) getActivity().findViewById(R.id.third_pred);

        firstPredContainer.setVisibility(View.GONE);
        secondPredContainer.setVisibility(View.GONE);
        thirdPredContainer.setVisibility(View.GONE);

//        TextView firstPredText = (TextView) getActivity().findViewById(R.id.first_pred_text);
//        TextView firstPredMin = (TextView) getActivity().findViewById(R.id.first_pred_min);
//
//        TextView secondPredText = (TextView) getActivity().findViewById(R.id.second_pred_text);
//        TextView secondPredMin = (TextView) getActivity().findViewById(R.id.second_pred_min);
//
//        TextView thirdPredText = (TextView) getActivity().findViewById(R.id.third_pred_text);
//        TextView thirdPredMin = (TextView) getActivity().findViewById(R.id.third_pred_min);

        //TextView otherPredsText = (TextView) getActivity().findViewById(R.id.other_preds_text);

//        firstPredText.setVisibility(View.GONE);
//        firstPredMin.setVisibility(View.GONE);
//
//        secondPredText.setVisibility(View.GONE);
//        secondPredMin.setVisibility(View.GONE);
//
//        thirdPredText.setVisibility(View.GONE);
//        thirdPredMin.setVisibility(View.GONE);

        //otherPredsText.setVisibility(View.GONE);

    }

    public void hideListView(){

        View bottomLine = getActivity().findViewById(R.id.predictions_divider_line_bottom);

        YoYo.with(Techniques.FadeOut)
                .playOn(listView);

        YoYo.with(Techniques.FadeOut)
                .playOn(bottomLine);


        listView.setVisibility(View.GONE);

        bottomLine.setVisibility(View.GONE);


    }

    public void showListView(){

        View bottomLine = getActivity().findViewById(R.id.predictions_divider_line_bottom);

        listView.setVisibility(View.VISIBLE);

        bottomLine.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.FadeIn)
                .playOn(listView);

        YoYo.with(Techniques.FadeIn)
                .playOn(bottomLine);





    }

    public void hideListViewShowPredictions(){

        View bottomLine = getActivity().findViewById(R.id.predictions_divider_line_bottom);

        YoYo.with(Techniques.FadeOut)
                .playOn(listView);

        YoYo.with(Techniques.FadeOut)
                .playOn(bottomLine);


        listView.setVisibility(View.GONE);

        bottomLine.setVisibility(View.GONE);

        SlidingUpPanelLayout panel = (SlidingUpPanelLayout) getActivity().findViewById(R.id.predictions_text_group);
        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.predictions_image_area);

        panel.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .playOn(panel);


        //fab.show(true);


    }

    public void showListViewHidePredictions(){

        View bottomLine = getActivity().findViewById(R.id.predictions_divider_line_bottom);

        SlidingUpPanelLayout relativeLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.predictions_text_group);

        YoYo.with(Techniques.FadeOut)
                .playOn(relativeLayout);

        relativeLayout.setVisibility(View.GONE);

        listView.setVisibility(View.VISIBLE);
        bottomLine.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.FadeIn)
                .playOn(listView);

        YoYo.with(Techniques.FadeIn)
                .playOn(bottomLine);

        //fab.hide(true);


    }

    public int getNavigationLevel() {
        return navigationLevel;
    }

    public void setNavigationLevel(int navigationLevel) {
        this.navigationLevel = navigationLevel;
    }



    public void colorizeRoute(boolean animate){

        TextView tvStop = (TextView) getActivity().findViewById(R.id.route_nav_text);


        tvStop.setTextColor(getResources().getColor(R.color.accent2));


        if(animate) {

            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(tvStop);
        }

    }

    public void greyifyRoute(){

        TextView tvStop = (TextView) getActivity().findViewById(R.id.route_nav_text);

        tvStop.setTextColor(getResources().getColor(R.color.accent3));

//        YoYo.with(Techniques.Flash)
//                .duration(1000)
//                .playOn(tvStop);

    }




    public void colorizeDirection(boolean animate){

        TextView tvDir = (TextView) getActivity().findViewById(R.id.dir_nav_text);
        TextView tvStopArrow = (TextView) getActivity().findViewById(R.id.route_arrow_nav_text);

        tvDir.setTextColor(getResources().getColor(R.color.accent2));
        tvStopArrow.setTextColor(getResources().getColor(R.color.accent1));

        if(animate) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(tvDir);
        }

    }

    public void greyifyDirection(){

        TextView tvDir = (TextView) getActivity().findViewById(R.id.dir_nav_text);
        TextView tvStopArrow = (TextView) getActivity().findViewById(R.id.route_arrow_nav_text);

        tvDir.setTextColor(getResources().getColor(R.color.accent3));
        tvStopArrow.setTextColor(getResources().getColor(R.color.accent3));

//        YoYo.with(Techniques.Flash)
//                .duration(1000)
//                .playOn(tvDir);

    }

    public void colorizeStop(boolean animate){

        TextView tvStop = (TextView) getActivity().findViewById(R.id.stop_nav_text);
        TextView tvDirArrow = (TextView) getActivity().findViewById(R.id.dir_arrow_nav_text);


        tvStop.setTextColor(getResources().getColor(R.color.accent2));
        tvDirArrow.setTextColor(getResources().getColor(R.color.accent1));

        if (animate) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(tvStop);
        }

    }

    public void greyifyStop(){

        TextView tvStop = (TextView) getActivity().findViewById(R.id.stop_nav_text);
        TextView tvDirArrow = (TextView) getActivity().findViewById(R.id.dir_arrow_nav_text);

        tvStop.setTextColor(getResources().getColor(R.color.accent3));
        tvDirArrow.setTextColor(getResources().getColor(R.color.accent3));

//        YoYo.with(Techniques.Flash)
//                .duration(1000)
//                .playOn(tvStop);

    }

    public void colorizeStopArrow(){

        TextView tvStopArrow = (TextView) getActivity().findViewById(R.id.stop_arrow_nav_text);
        tvStopArrow.setTextColor(getResources().getColor(R.color.accent1));



    }

    public void greyifyStopArrow(){

        TextView tvStopArrow = (TextView) getActivity().findViewById(R.id.stop_arrow_nav_text);
        tvStopArrow.setTextColor(getResources().getColor(R.color.accent3));


    }

    public void routeClick(){

        cancelRefresh();

        if(getNavigationLevel() < LEVEL_ROUTE){
            return;
        }

        setNavigationLevel(LEVEL_ROUTE);

        listView.setAdapter(routeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                //final String item = (String) parent.getItemAtPosition(position);

                //hideListView();
                storedRoute = routeList.get(position);
                //HomeActivity homeActivity = (HomeActivity) getActivity();
                HomeActivity homeActivity = (HomeActivity) getActivity();

                homeActivity.fetchDirectionStops(storedRoute.getTag(), RoutesResponseHandler.PREDICTIONS_ID, true);
                setNavigationLevel(LEVEL_DIRECTION);

//                Toast toast = Toast.makeText(getActivity(), "getting directions for: " + routeTagList.get(position), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
            }

        });

        routeAdapter.notifyDataSetChanged();

        showListViewHidePredictions();

        colorizeRoute(true);
        greyifyDirection();
        greyifyStop();
        greyifyStopArrow();

        swipeLayout.setEnabled(true);

    }


    public void directionClick(){

        cancelRefresh();

        if(getNavigationLevel() < LEVEL_DIRECTION){
            return;
        }

        setNavigationLevel(LEVEL_DIRECTION);

        listView.setAdapter(directionAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                //hideListView();

                storedDirection = directionList.get(position);
                generateStopList(position, true);
//                Toast toast = Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();

                setNavigationLevel(LEVEL_STOP);
            }

        });

        directionAdapter.notifyDataSetChanged();
        showListViewHidePredictions();

        colorizeRoute(false);
        colorizeDirection(true);
        greyifyStop();
        greyifyStopArrow();

        swipeLayout.setEnabled(true);

    }

    public void stopClick(){

        cancelRefresh();

        if(getNavigationLevel() < LEVEL_STOP){
            return;
        }
        setNavigationLevel(LEVEL_STOP);

        listView.setAdapter(stopAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                //hideListView();
                storedStop = stopByDirectionList.get(position);
                //HomeActivity homeActivity = (HomeActivity) getActivity();

                HomeActivity homeActivity = (HomeActivity) getActivity();

                homeActivity.fetchPredictions(storedStop.getID());
//                Toast toast = Toast.makeText(getActivity(), stopID, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
                setNavigationLevel(LEVEL_PREDICTION);
                swipeLayout.setEnabled(false);
            }

        });

        stopAdapter.notifyDataSetChanged();
        showListViewHidePredictions();

        colorizeRoute(false);
        colorizeDirection(false);
        colorizeStop(true);
        greyifyStopArrow();

        swipeLayout.setEnabled(true);



    }

    public void toggleRefresh(View view){

        if (shouldRefresh){
            shouldRefresh = false;
            cancelRefresh();
        }
        else{
            shouldRefresh = true;
            scheduleRefresh();
        }

    }

    public void cancelRefresh(){

        timer.cancel();
        timer.purge();


    }

    public void scheduleRefresh(){

        RefreshPredictionsTimerTask task = new RefreshPredictionsTimerTask();

        timer = new Timer();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String intervalString = sharedPreferences.getString("refreshInterval", "30000");

        int interval = Integer.parseInt(intervalString);

        timer.schedule(task, 0, interval);


    }

    public void saveFavoriteOld(){


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int numFavorites = sharedPreferences.getInt("numFavorites", 0);

        numFavorites = numFavorites + 1;

        Favorite favorite = new Favorite(storedRoute, storedDirection, storedStop, numFavorites);

        ArrayList<Favorite> favorites = FavoritesFragment.buildFavoritesListOld(this.getActivity());


        for (int i = 0; i < favorites.size(); i++) {
            if (favorite.equals(favorites.get(i))) {
                Toast toast = Toast.makeText(getActivity(), "Already a favorite!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
        }

        Gson gson = new Gson();
        String json = gson.toJson(favorite);
        editor.putString("favorite" + numFavorites, json);
        editor.putInt("numFavorites", numFavorites);
        editor.commit();

        Toast toast = Toast.makeText(getActivity(), "Favorite added!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();



    }

    public void saveFavorite(){

        Favorite favorite = new Favorite(storedRoute, storedDirection, storedStop, 0);

        ArrayList<Favorite> favorites = FavoritesFragment.buildFavoritesList(getActivity());

        for (int i = 0; i < favorites.size(); i++) {
            if (favorite.equals(favorites.get(i))) {
                Toast toast = Toast.makeText(getActivity(), "Already a favorite!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
        }

        favorites.add(favorite);

        FavoritesFragment.syncFavorites(getActivity(), favorites);


        Toast toast = Toast.makeText(getActivity(), "Favorite added!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    public void fetchInForeground(Route route, Direction direction, Stop stop){

//
//        final HomeActivity homeActivity = (HomeActivity) getActivity();
//
//        /*------------------------------
//            Route fetch
//
//         */
//
//
//        homeActivity.fetchRouteList();
//
//
//        /* ---------------------------
//           Direction and Stop fetch
//           Generate Directions
//
//         */
//
//
//        homeActivity.fetchDirectionStops(storedRoute.getTag());
//
//
//        /* ----------------------------
//           Generate Stops
//         */
//
//
//
//        //find the position that the direction would have been in the listview
//        int position = 0;
//
//        for(int i = 0; i < directionList.size(); i++){
//
//            if(direction.getTag().equals(directionList.get(i).getTag())){
//                position = i;
//            }
//
//        }
//
//        generateStopList(position);
//
//
//
//        /* -------------------------
//            Prediction fetch
//
//         */




    }

    private ProgressDialog progressDialog;

    public void favoriteClick(Favorite favorite){



        HomeActivity homeActivity = (HomeActivity) getActivity();
        cancelRefresh();

        Route route = favorite.getRoute();
        Direction direction = favorite.getDirection();
        Stop stop = favorite.getStop();

        storedRoute = route;
        storedDirection = direction;
        storedStop = stop;

        stopByDirectionList.clear();
        directionList.clear();

        new RetrieveListsInBackground(this).execute();

        colorizeRoute(false);
        colorizeDirection(false);
        colorizeStop(false);


        if(shouldRefresh){
            scheduleRefresh();
        }
        else{

            homeActivity.fetchPredictions(storedStop.getID());


        }

        setNavigationLevel(LEVEL_PREDICTION);

        hideListViewShowPredictions();

        swipeLayout.setEnabled(false);


    }

    public void mapClick(){



//        Intent intent = new Intent(getActivity(), MapStopActivity.class);
        Intent intent = new Intent(getActivity(), MapActivity.class);

        intent.putExtra("routeTitle", storedRoute.getTitle());
        intent.putExtra("routeTag", storedRoute.getTag());

        intent.putExtra("dirTitle", storedDirection.getTitle());
        intent.putExtra("dirTag", storedDirection.getTag());

        intent.putExtra("stopTitle", storedStop.getTitle());
        intent.putExtra("stopTag", storedStop.getTag());
        intent.putExtra("stopLat", storedStop.getLat());
        intent.putExtra("stopLon", storedStop.getLon());

        intent.putExtra("routeListCache", routeList);

        startActivity(intent);

    }

    public void setImageBackground(){

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.predictions_image_area_image);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if(hour >= 1 && hour < 6){//night
            imageView.setImageResource(R.drawable.belltower_night_crop_blur);
        }
        else if(hour >= 6 && hour < 17){//day
            imageView.setImageResource(R.drawable.belltower_day2_crop_blur_dull);
        }
        else if(hour >= 17 && hour < 20){//dusk
            imageView.setImageResource(R.drawable.belltower_dusk_crop_blur);
        }
        else{//night
            imageView.setImageResource(R.drawable.belltower_night_crop_blur);
        }


    }


    public class RetrieveListsInBackground extends AsyncTask<Void, Integer, Boolean> {

        Fragment fragment;


        public RetrieveListsInBackground(Fragment fragment){
            this.fragment = fragment;



        }

        @Override
        protected Boolean doInBackground(Void... v) {

            PredictionsFragment predictionsFragment = (PredictionsFragment) fragment;

            Route route = predictionsFragment.getStoredRoute();
            Direction direction = predictionsFragment.getStoredDirection();
            Stop stop = predictionsFragment.getStoredStop();

            HomeActivity homeActivity = (HomeActivity) fragment.getActivity();

            //Method chains and gets directions AND stops
            homeActivity.fetchDirectionStops(route.getTag(), RoutesResponseHandler.PREDICTIONS_ID, false);


            return true;

        }


        @Override
        protected void onPostExecute(Boolean result) {

//            if(result){
//                Toast toast = Toast.makeText(fragment.getActivity(), "yes", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//
//            }
//            else{
//                Toast toast = Toast.makeText(fragment.getActivity(), "no", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            }




        }
    }

    public Stop getStoredStop() {
        return storedStop;
    }

    public Route getStoredRoute() {
        return storedRoute;
    }

    public Direction getStoredDirection() {
        return storedDirection;
    }
}
