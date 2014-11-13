package com.jforce.chapelhillnextbus;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by justinforsyth on 10/24/14.
 */
public class DrawerArrayAdapter extends ArrayAdapter<String> {


    private Context context;
    private String[] values;

    public DrawerArrayAdapter(Context context, String[] values) {
        super(context, R.layout.drawer_list_item, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);


        TextView rowText = (TextView) rowView.findViewById(R.id.drawer_item_text);

        rowText.setText(values[position]);


        ImageView icon = (ImageView) rowView.findViewById(R.id.drawer_item_icon);

        if(position ==  0){//preds

            icon.setImageResource(R.drawable.ic_hardware_watch);

        }
        else if (position == 1){//faves

            icon.setImageResource(R.drawable.ic_action_grade);
        }
        else if (position == 2){

            icon.setImageResource(R.drawable.ic_maps_map_navy);

        }





        return rowView;
    }





}
