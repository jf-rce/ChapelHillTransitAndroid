package com.jforce.chapelhillnextbus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by justinforsyth on 12/15/14.
 */
public class FavoritesExpandingArrayAdapter extends ExpandableListItemAdapter<Favorite> {

    private Context context;
    private final ArrayList<Favorite> values;
    private SparseBooleanArray mSelectedItemsIds;

    public FavoritesExpandingArrayAdapter(final Context context, ArrayList<Favorite> values) {
        super(context, R.layout.list_item_favorite_expandable, R.id.list_item_favorite_expandable_title, R.id.list_item_favorite_expandable_content);

        this.context = context;
        this.values = values;

    }

    @NonNull
    @Override
    public View getTitleView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_favorite_expandable_title, parent, false);

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

        //rowView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);

        Drawable drawable_white = context.getResources().getDrawable(R.drawable.card_background_white);

        Drawable drawable_selected = context.getResources().getDrawable(R.drawable.card_background_selected);


        int left = rowView.getPaddingLeft();
        int top = rowView.getPaddingTop();
        int right = rowView.getPaddingRight();
        int bottom = rowView.getPaddingBottom();
        rowView.setBackgroundDrawable(mSelectedItemsIds.get(position) ? drawable_selected : drawable_white);

        /*

        setBackGroundDrawable erases padding
        need to reset it after the call

         */

        rowView.setPadding(left, top, right, bottom);


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


        TextView tv = (TextView) convertView;

        tv.setText("hello");

        return tv;



        //return rowView;
    }

    @NonNull
    @Override
    public View getContentView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_favorite_expandable_content, parent, false);


        TextView tv = (TextView) convertView;

        tv.setText("world");

        return tv;


        //return rowView;
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
