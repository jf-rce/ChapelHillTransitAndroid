package com.jforce.chapelhillnextbus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by justinforsyth on 10/4/14.
 */
public class ScheduleArrayAdapter extends ArrayAdapter<Schedule> {
    private final Context context;
    private final ArrayList<Schedule> values;

    public ScheduleArrayAdapter(Context context, ArrayList<Schedule> values) {
        super(context, R.layout.list_item_route, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_route, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.route_list_text);

        textView.setText(values.get(position).getRouteTitle());

        return rowView;
    }
}