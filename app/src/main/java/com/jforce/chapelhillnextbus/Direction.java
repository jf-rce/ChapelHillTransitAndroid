package com.jforce.chapelhillnextbus;

/**
 * Created by justinforsyth on 10/4/14.
 */
public class Direction {

    private String tag;
    private String title;

    public Direction(String tag, String title){
        this.tag = tag;
        this.title = title;

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


}
