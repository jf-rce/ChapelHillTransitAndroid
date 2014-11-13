package com.jforce.chapelhillnextbus;

/**
 * Created by justinforsyth on 10/21/14.
 */
public class Favorite {

    Route route;
    Direction direction;
    Stop stop;
    int id;

    public Favorite(Route route, Direction direction, Stop stop, int id){

        this.route = route;
        this.direction = direction;
        this.stop = stop;
        this.id = id;

    }


    @Override
    public boolean equals(Object obj){

        Favorite favorite = (Favorite) obj;

        if(this.route.getTag().equals(favorite.route.getTag()) &&
                this.direction.getTag().equals(favorite.direction.getTag()) &&
                this.stop.getTag().equals(favorite.stop.getTag())){

            return true;


        }
        else{

            return false;

        }




    }

    public Route getRoute() {
        return route;
    }

    public Direction getDirection() {
        return direction;
    }

    public Stop getStop() {
        return stop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
