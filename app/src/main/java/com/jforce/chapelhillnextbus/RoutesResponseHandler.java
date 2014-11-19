package com.jforce.chapelhillnextbus;

import android.app.Activity;

import android.app.FragmentManager;
import android.view.Gravity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by justinforsyth on 10/3/14.
 */
public class RoutesResponseHandler extends AsyncHttpResponseHandler{


    public final static int PREDICTIONS_ID = 0;
    public final static int FAVORITES_ID = 1;
    public final static int MAP_ID = 2;
    public final static int MAP_ID_STANDALONE = 3;

    public static boolean ZOOM_TO_BOUNDS = true;
    public static boolean NO_ZOOM = false;


    private Activity activity;
    private int fragmentID;


    public RoutesResponseHandler(Activity activity, int fragmentID){

        this.activity = activity;
        this.fragmentID = fragmentID;


    }



    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        // called when response HTTP status is "200 OK"

        //TODO:

        NodeList nList;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new ByteArrayInputStream(response));

            doc.getDocumentElement().normalize();

            nList = doc.getElementsByTagName("route");


            //TODO:
            //ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) homeActivity.getPagerAdapter();

            //PredictionsFragment fragment = (PredictionsFragment) adapter.getRegisteredFragment(0);




        }catch (Exception e) {
            Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.routeListError), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        FragmentManager fragmentManager = activity.getFragmentManager();

        if(activity.getClass() == HomeActivity.class) {

            if (fragmentID == PREDICTIONS_ID) {
                PredictionsFragment fragment = (PredictionsFragment) fragmentManager.findFragmentById(R.id.content_frame);

                fragment.generateRouteList(nList);
            } else if (fragmentID == MAP_ID) {
                MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame);

                fragment.generateRouteList(nList);
            }
        }
        else if(activity.getClass() == MapActivity.class){


            MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame2);

            fragment.generateRouteList(nList);


        }



    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        // called when response HTTP status is "4XX" (eg. 401, 403, 404)

        Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.routeListError), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
