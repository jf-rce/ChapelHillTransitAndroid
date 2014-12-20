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
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by justinforsyth on 10/4/14.
 */
public class DirectionsStopsPathsResponseHandler extends AsyncHttpResponseHandler {

    private Activity activity;
    private int fragmentID;
    private boolean zoom;
    private Boolean animate;




    public DirectionsStopsPathsResponseHandler(Activity activity, int fragmentID, boolean zoom, Boolean animate){

        this.activity = activity;
        this.fragmentID = fragmentID;
        this.zoom = zoom;
        this.animate = animate;


    }



    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        // called when response HTTP status is "200 OK"

        //HomeActivity homeActivity = (HomeActivity) activity;


//        String s = new String(response);
//
//        Toast toast = Toast.makeText(activity, s, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();


        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            final Document doc = documentBuilder.parse(new ByteArrayInputStream(response));

            doc.getDocumentElement().normalize();

            //NodeList directionNodeList = doc.getElementsByTagName("direction");



            //ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) homeActivity.getPagerAdapter();

            //PredictionsFragment fragment = (PredictionsFragment) adapter.getRegisteredFragment(0);

            FragmentManager fragmentManager = activity.getFragmentManager();

            if(activity.getClass() == HomeActivity.class){



                if(fragmentID == RoutesResponseHandler.PREDICTIONS_ID){
                    final PredictionsFragment fragment = (PredictionsFragment) fragmentManager.findFragmentById(R.id.content_frame);



                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            fragment.generateDirectionList(doc, animate);
                        }
                    });

                    return;

                }
                else if(fragmentID == RoutesResponseHandler.MAP_ID){
                    MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame);
                    fragment.drawPath(doc, zoom);
                    return;

                }




            }
            else if(activity.getClass() == MapActivity.class){



                if(zoom == RoutesResponseHandler.NO_ZOOM){
                    MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame2);
                    fragment.drawPath(doc, false);

                    return;

                }
                else{
                    MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame2);
                    fragment.drawPath(doc, true);

                    return;

                }






            }





        }catch (Exception e) {

            if(activity.getClass() == HomeActivity.class){

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.directionListError), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                });

            }
            else if(activity.getClass() == MapActivity.class){


                activity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.routeDrawError), Toast.LENGTH_SHORT);
                           toast.setGravity(Gravity.CENTER, 0, 0);
                           toast.show();
                           return;
                       }
                });


            }





        }


    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        // called when response HTTP status is "4XX" (eg. 401, 403, 404)

        if(activity.getClass() == HomeActivity.class){

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.directionListError), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            });

        }
        else if (activity.getClass() == MapActivity.class){

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.routeDrawError), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;

                }
            });

        }
    }


}
