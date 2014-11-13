package com.jforce.chapelhillnextbus;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by justinforsyth on 10/25/14.
 */
public class PredictionArrayAdapter extends ArrayAdapter<Prediction> {

    private Context context;
    private ArrayList<Prediction> values;

    public PredictionArrayAdapter(Context context, ArrayList<Prediction> values) {
        super(context, R.layout.list_item_prediction, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_prediction, parent, false);


        TextView rowText = (TextView) rowView.findViewById(R.id.route_info_text_pred);
        TextView iconTextMain = (TextView)rowView.findViewById(R.id.pred_icon_text_main);
        TextView iconTextSub = (TextView)rowView.findViewById(R.id.pred_icon_text_sub);


        Prediction prediction = values.get(position);

        String title = prediction.getRoute().getTitle();
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

            iconTextMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        }

        if(iconTextMain.getText().toString().equals("CCX")){

            iconTextMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        }


        TextView dirText = (TextView)rowView.findViewById(R.id.dir_info_text_pred);
        dirText.setText(prediction.getDirection().getTitle());

        TextView icon2Main = (TextView)rowView.findViewById(R.id.pred_icon2_text_main);
        TextView icon2Sub = (TextView)rowView.findViewById(R.id.pred_icon2_text_sub);

        icon2Main.setText(prediction.getTimeInMinutes());
        icon2Sub.setText("min");


        return rowView;
    }
}
