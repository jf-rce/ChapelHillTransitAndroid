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
 * Created by justinforsyth on 10/4/14.
 */
public class RouteArrayAdapter extends ArrayAdapter<Route> {
    private Context context;
    private ArrayList<Route> values;

    public RouteArrayAdapter(Context context, ArrayList<Route> values) {
        super(context, R.layout.list_item_route, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_route, parent, false);


        TextView rowText = (TextView) rowView.findViewById(R.id.route_list_text);
        TextView iconTextMain = (TextView)rowView.findViewById(R.id.route_icon_text_main);
        TextView iconTextSub = (TextView)rowView.findViewById(R.id.route_icon_text_sub);


        String title = values.get(position).getTitle();
        Scanner scanner = new Scanner(title);
        ArrayList<String> tokens = new ArrayList<String>();


        while(scanner.hasNext()){
            tokens.add(scanner.next());
        }

        if(title.equals("Route 420")){
            rowText.setText(title);
            iconTextMain.setText(tokens.get(1));
            iconTextSub.setText("");
        }
        else if(tokens.get(0).equals("Safe")){
            rowText.setText(title + " Route");
            iconTextMain.setText(tokens.get(2));
            iconTextSub.setText(tokens.get(0) + " " + tokens.get(1));

        }
        else if(tokens.size() >= 2){
            if(tokens.get(1).equals("Weekend") || tokens.get(1).equals("Saturday")) {

                rowText.setText(title + " Route");
                iconTextMain.setText(tokens.get(0));
                iconTextSub.setText(tokens.get(1));
            }
            else{
                rowText.setText(title + " Route");
                iconTextMain.setText(tokens.get(0));
                iconTextSub.setText("");
            }

        }
        else{
            rowText.setText(title + " Route");
            iconTextMain.setText(tokens.get(0));
            iconTextSub.setText("");
        }


        if(iconTextMain.getText().toString().length() > 3){

            iconTextMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        }

        if(iconTextMain.getText().toString().equals("CCX")){

            iconTextMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

        }




        return rowView;
    }
}