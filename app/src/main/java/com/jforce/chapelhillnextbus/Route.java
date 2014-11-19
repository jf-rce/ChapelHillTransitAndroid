package com.jforce.chapelhillnextbus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by justinforsyth on 10/4/14.
 */
public class Route implements Parcelable {

    private String tag;
    private String title;

    public Route(String tag, String title){
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



    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tag);
        out.writeString(title);
    }

    public static final Parcelable.Creator<Route> CREATOR
            = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    private Route(Parcel in) {
        tag = in.readString();
        title = in.readString();
    }



}
