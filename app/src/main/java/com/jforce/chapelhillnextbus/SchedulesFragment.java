package com.jforce.chapelhillnextbus;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SchedulesFragment extends Fragment {

    private ViewGroup rootView;

    ListView listView;


    public SchedulesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_schedules, container, false);
        init();
        return rootView;
    }

    public void init(){

        final ArrayList<Schedule> schedules = new ArrayList<Schedule>();

        schedules.add(new Schedule("A Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23927"));
        schedules.add(new Schedule("CCX Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23926"));
        schedules.add(new Schedule("CL Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23925"));
        schedules.add(new Schedule("CM Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23923"));
        schedules.add(new Schedule("CW Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23921"));
        schedules.add(new Schedule("D Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23920"));
        schedules.add(new Schedule("DX Route", "http://www.ci.chapel-hill.nc.us/Modules/ShowDocument.aspx?documentid=23918"));
        schedules.add(new Schedule("FCX Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23916"));
        schedules.add(new Schedule("G Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23915"));
        schedules.add(new Schedule("Safe Ride G Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23906"));
        schedules.add(new Schedule("HS Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23914"));
        schedules.add(new Schedule("HU Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23913"));
        schedules.add(new Schedule("J Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23911"));
        schedules.add(new Schedule("Safe Ride J Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23906"));
        schedules.add(new Schedule("N Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23910"));
        schedules.add(new Schedule("NS Route", "http://www.townofchapelhill.org/modules/ShowDocument.aspx?documentid=23909"));
        schedules.add(new Schedule("NU Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23907"));
        schedules.add(new Schedule("PX Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=24172"));
        schedules.add(new Schedule("Route 420 Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=19010"));
        schedules.add(new Schedule("RU Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23901"));
        schedules.add(new Schedule("S Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23905"));
        schedules.add(new Schedule("T Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23902"));
        schedules.add(new Schedule("Safe Ride T Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23906"));
        schedules.add(new Schedule("U Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23901"));
        schedules.add(new Schedule("V Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23900"));
        schedules.add(new Schedule("CM Saturday Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23924"));
        schedules.add(new Schedule("CW Saturday Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23924"));
        schedules.add(new Schedule("D Saturday Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23919"));
        schedules.add(new Schedule("FG Saturday Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23919"));
        schedules.add(new Schedule("JN Saturday Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23903"));
        schedules.add(new Schedule("NU Weekend Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23908"));
        schedules.add(new Schedule("T Saturday Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23903"));
        schedules.add(new Schedule("U Weekend Route", "http://www.townofchapelhill.org/Modules/ShowDocument.aspx?documentid=23908"));


        Collections.sort(schedules);

        listView = (ListView) rootView.findViewById(R.id.schedules_listview);

//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int topRowVerticalPosition =
//                        (listView == null || listView.getChildCount() == 0) ?
//                                0 : listView.getChildAt(0).getTop();
//                SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
//                swipeContainer.setEnabled(topRowVerticalPosition >= 0);
//            }
//        });


        ScheduleArrayAdapter adapter = new ScheduleArrayAdapter(getActivity(), schedules);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                String link = schedules.get(position).getLink();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);


            }

        });

        adapter.notifyDataSetChanged();


    }


}
