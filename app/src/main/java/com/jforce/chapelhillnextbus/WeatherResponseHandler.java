package com.jforce.chapelhillnextbus;

import android.app.Activity;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by justinforsyth on 10/24/14.
 */
public class WeatherResponseHandler extends JsonHttpResponseHandler {

    private Activity activity;

    public WeatherResponseHandler(Activity activity) {

        this.activity = activity;

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

        HomeActivity homeActivity = (HomeActivity) activity;

        JSONArray weatherArray;
        JSONObject mainObj;

        try {
            weatherArray = response.getJSONArray("weather");
            mainObj = response.getJSONObject("main");
        } catch (JSONException e) {

            Log.d("jsonerror1", e.toString());

            homeActivity.setWeatherText(getRandomUNCString(), false);

            return;
        }




        double tempHi;
        double tempLo;
        double temp;
        JSONObject weatherObject;

        try{
            temp = mainObj.getDouble("temp");
            tempHi = mainObj.getDouble("temp_max");
            tempLo = mainObj.getDouble("temp_min");
            weatherObject = weatherArray.getJSONObject(0);
        }
        catch (JSONException e){

            Log.d("jsonerror2", e.toString());

            homeActivity.setWeatherText(getRandomUNCString(), false);

            return;
        }


        String weatherDesc;


        try{

            weatherDesc = weatherObject.getString("main");
        }
        catch (JSONException e){

            Log.d("jsonerror3", e.toString());



            homeActivity.setWeatherText(getRandomUNCString(), false);


            return;
        }




        tempHi = kelvinToFahrenheit(tempHi);
        tempLo = kelvinToFahrenheit(tempLo);
        temp = kelvinToFahrenheit(temp);

        int intTempHi = (int) tempHi;
        int intTempLo = (int) tempLo;
        int intTemp = (int) temp;


        homeActivity.setWeatherText(intTemp, weatherDesc);




    }



    @Override
    public void onFailure(int code, Header[] headers, String string, Throwable throwable) {

        Log.d("weather", string);

//        Toast toast = Toast.makeText(activity,  string, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();

        HomeActivity homeActivity = (HomeActivity) activity;


        homeActivity.setWeatherText(getRandomUNCString(), false);




    }


    public static double kelvinToFahrenheit(double k){

        return ((k - 273.15)*1.8) + 32;

    }

    public String getRandomUNCString(){

        String[] array = activity.getResources().getStringArray(R.array.noweather_array);

        return array[randInt(0, array.length -2)];

        //^^ excluding "GO TO HELL DUKE" for now :)



    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


}
