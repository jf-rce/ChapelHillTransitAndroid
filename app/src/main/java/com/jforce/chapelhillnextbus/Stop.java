package com.jforce.chapelhillnextbus;

/**
 * Created by justinforsyth on 10/4/14.
 */
public class Stop {

    private String tag;
    private String title;
    private String ID;
    private String lat;
    private String lon;




    public Stop(String tag, String title, String ID, String lat, String lon){
        this.tag = tag;
        this.title = title;
        this.ID = ID;
        this.lat = lat;
        this.lon = lon;


    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }


}
