package com.jforce.chapelhillnextbus;

import android.app.Activity;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by justinforsyth on 10/4/14.
 */
public class PredictionsResponseHandler extends AsyncHttpResponseHandler {

    private Activity activity;

    public PredictionsResponseHandler(Activity activity){

        this.activity = activity;

    }



    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        // called when response HTTP status is "200 OK"



        //HomeActivity homeActivity = (HomeActivity) activity;
        HomeActivity homeActivity = (HomeActivity) activity;

        String s = new String(response);

        //Log.w("predictions", s);

//        Toast toast = Toast.makeText(activity, s, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();

        Document doc = null;


        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            doc = documentBuilder.parse(new ByteArrayInputStream(response));

            doc.getDocumentElement().normalize();





        }catch (Exception e) {
//            e.printStackTrace();
//            return;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.predictionsError), Toast.LENGTH_SHORT);
                    //Toast toast = Toast.makeText(activity, "ayy", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            });
            return;
        }

        if (doc == null){

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.predictionsError), Toast.LENGTH_SHORT);
                    //Toast toast = Toast.makeText(activity, "lmao", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            });

            return;

        }


//        ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) homeActivity.getPagerAdapter();
//
//        PredictionsFragment fragment = (PredictionsFragment) adapter.getRegisteredFragment(0);
        FragmentManager fragmentManager = homeActivity.getFragmentManager();

        PredictionsFragment fragment = (PredictionsFragment) fragmentManager.findFragmentById(R.id.content_frame);


        fragment.generatePredictions(doc);



    }

    @Override
    public void onFailure(final int statusCode, Header[] headers, final byte[] errorResponse, Throwable e) {
        // called when response HTTP status is "4XX" (eg. 401, 403, 404)




        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.predictionsError), Toast.LENGTH_SHORT);
                //Toast toast = Toast.makeText(activity, Integer.toString(statusCode), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                //Log.d("cht", errorResponse.toString());

            }
        });



    }


}
