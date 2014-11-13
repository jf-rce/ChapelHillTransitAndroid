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

    public DirectionsStopsPathsResponseHandler(Activity activity, int fragmentID){

        this.activity = activity;
        this.fragmentID = fragmentID;


    }



    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        // called when response HTTP status is "200 OK"

        //TODO:
        //HomeActivity homeActivity = (HomeActivity) activity;


//        String s = new String(response);
//
//        Toast toast = Toast.makeText(activity, s, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();


        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new ByteArrayInputStream(response));

            doc.getDocumentElement().normalize();

            //NodeList directionNodeList = doc.getElementsByTagName("direction");


            //TODO:

            //ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) homeActivity.getPagerAdapter();

            //PredictionsFragment fragment = (PredictionsFragment) adapter.getRegisteredFragment(0);

            if(activity.getClass() == HomeActivity.class){

                HomeActivity homeActivity = (HomeActivity) activity;
                FragmentManager fragmentManager = homeActivity.getFragmentManager();

                if(fragmentID == RoutesResponseHandler.PREDICTIONS_ID){
                    PredictionsFragment fragment = (PredictionsFragment) fragmentManager.findFragmentById(R.id.content_frame);
                    fragment.generateDirectionList(doc);
                    return;

                }
                else if(fragmentID == RoutesResponseHandler.MAP_ID){
                    MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.content_frame);
                    fragment.drawPath(doc);
                    return;

                }




            }
            else if(activity.getClass() == MapStopActivity.class){

                MapStopActivity mapActivity = (MapStopActivity) activity;
                mapActivity.drawPath(doc);



            }





        }catch (Exception e) {

            if(activity.getClass() == HomeActivity.class){

                Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.directionListError), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            else if(activity.getClass() == MapStopActivity.class){

                Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.routeDrawError), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }





        }


    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        // called when response HTTP status is "4XX" (eg. 401, 403, 404)

        if(activity.getClass() == HomeActivity.class){
            Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.directionListError), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if (activity.getClass() == MapStopActivity.class){

            Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.routeDrawError), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;

        }
    }


}
