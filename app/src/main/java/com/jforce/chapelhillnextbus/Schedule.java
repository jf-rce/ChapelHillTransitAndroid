package com.jforce.chapelhillnextbus;

import java.util.Comparator;

/**
 * Created by justinforsyth on 10/11/14.
 */
public class Schedule implements Comparable<Schedule>{

    private String link;
    private String routeTitle;



    public Schedule(String routeTitle, String link){

        this.link = link;
        this.routeTitle = routeTitle;


    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRouteTitle() {
        return routeTitle;
    }

    public void setRouteTitle(String routeTitle) {
        this.routeTitle = routeTitle;
    }


    @Override
    public int compareTo(Schedule schedule){

        return this.getRouteTitle().compareTo(schedule.getRouteTitle());

    }


}
