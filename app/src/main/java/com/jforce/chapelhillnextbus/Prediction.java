package com.jforce.chapelhillnextbus;

/**
 * Created by justinforsyth on 10/4/14.
 */
public class Prediction implements Comparable<Prediction>{

    private Route route;
    private Direction direction;
    private String timeInMinutes;

    public Prediction(Route route, Direction direction, String timeInMinutes){
        this.route = route;
        this.direction = direction;
        this.timeInMinutes = timeInMinutes;
    }

    public String getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(String timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public int compareTo(Prediction prediction){

        int thisTime = Integer.parseInt(this.timeInMinutes);

        int thatTime = Integer.parseInt(prediction.getTimeInMinutes());

        return thisTime - thatTime;

    }



}
