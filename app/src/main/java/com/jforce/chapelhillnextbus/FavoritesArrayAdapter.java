package com.jforce.chapelhillnextbus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by justinforsyth on 10/22/14.
 */
public class FavoritesArrayAdapter extends ArrayAdapter<Favorite> {

    private final Context context;
    private final ArrayList<Favorite> values;
    private SparseBooleanArray mSelectedItemsIds;

    public FavoritesArrayAdapter(Context context, ArrayList<Favorite> values) {
        super(context, R.layout.list_item_route, values);
        this.context = context;
        this.values = values;
        mSelectedItemsIds = new SparseBooleanArray();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_favorite, parent, false);

        TextView textViewRoute = (TextView) rowView.findViewById(R.id.route_info_text_favorite);

        if((values.get(position).getRoute().getTitle().equals("Route 420"))){

            textViewRoute.setText(values.get(position).getRoute().getTitle());

        }
        else{
            textViewRoute.setText(values.get(position).getRoute().getTitle() + " Route");
        }

        TextView textViewDir = (TextView) rowView.findViewById(R.id.dir_info_text_favorite);
        textViewDir.setText(values.get(position).getDirection().getTitle());

        TextView textViewStop = (TextView) rowView.findViewById(R.id.stop_info_text_favorite);
        textViewStop.setText(values.get(position).getStop().getTitle());

        rowView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);



        TextView iconTextMain = (TextView)rowView.findViewById(R.id.favorite_icon_text_main);
        TextView iconTextSub = (TextView)rowView.findViewById(R.id.favorite_icon_text_sub);


        String title = values.get(position).getRoute().getTitle();
        Scanner scanner = new Scanner(title);
        ArrayList<String> tokens = new ArrayList<String>();


        while(scanner.hasNext()){
            tokens.add(scanner.next());
        }

        if(title.equals("Route 420")){

            iconTextMain.setText(tokens.get(1));
            iconTextSub.setText("");
        }
        else if(tokens.get(0).equals("Safe")){

            iconTextMain.setText(tokens.get(2));
            iconTextSub.setText(tokens.get(0) + " " + tokens.get(1));

        }
        else if(tokens.size() >= 2){
            if(tokens.get(1).equals("Weekend") || tokens.get(1).equals("Saturday")) {


                iconTextMain.setText(tokens.get(0));
                iconTextSub.setText(tokens.get(1));
            }
            else{

                iconTextMain.setText(tokens.get(0));
                iconTextSub.setText("");
            }

        }
        else{

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


    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public ArrayList<Favorite> getValues(){
        return values;
    }


}
