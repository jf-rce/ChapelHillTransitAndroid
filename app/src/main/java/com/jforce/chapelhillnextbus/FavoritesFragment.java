package com.jforce.chapelhillnextbus;



import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.util.SparseBooleanArray;
//import android.view.ActionMode;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FavoritesFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ActionMode.Callback{

    private ViewGroup rootView;

    private ListView listView;

    ActionMode mActionMode;

    ArrayList<Favorite> favorites;

    FavoritesArrayAdapter adapter;

    FavoritesExpandingArrayAdapter eAdapter;



    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_favorites, container, false);
        refresh();
        return rootView;
    }


    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onPause(){
        super.onPause();

    }

    @Override
    public void onStop(){
        super.onStop();

    }

    @Override
    public void onResume(){
        super.onResume();

        refresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refresh();
        }
        else {

        }
    }


    public void refresh(){

        favorites = buildFavoritesList(this.getActivity());


        listView = (ListView) rootView.findViewById(R.id.favorites_listview);

        //eListView = (ExpandingListView) rootView.findViewById(R.id.expanding_listview);


        TextView tv = (TextView) rootView.findViewById(R.id.favorites_header);

        if(favorites.size() > 0){
            tv.setText("TAP FOR PREDICTION");
            tv.setTextColor(getResources().getColor(R.color.accent2));
        }
        else{
            tv.setText("NO FAVORITES");
            tv.setTextColor(getResources().getColor(R.color.accent3));
        }

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
//                CustomSwipeToRefresh swipeContainer = (CustomSwipeToRefresh) getActivity().findViewById(R.id.swipe_container);
//                swipeContainer.setEnabled(topRowVerticalPosition >= 0);
//            }
//        });


        adapter = new FavoritesArrayAdapter(getActivity(), favorites);

//        eAdapter = new FavoritesExpandingArrayAdapter(getActivity(), favorites);
//        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(eAdapter);
//        alphaInAnimationAdapter.setAbsListView(listView);
//
//        assert alphaInAnimationAdapter.getViewAnimator() != null;
//        alphaInAnimationAdapter.getViewAnimator().setInitialDelayMillis(500);

        listView.setAdapter(adapter);
        //listView.setAdapter(eAdapter);
        //listView.setAdapter(alphaInAnimationAdapter);

        listView.setOnItemClickListener(this);

        listView.setOnItemLongClickListener(this);

        adapter.notifyDataSetChanged();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view,
                            int position, long id) {

        if (mActionMode == null) {

            final Favorite favorite = favorites.get(position);


            HomeActivity homeActivity = (HomeActivity) getActivity();

            homeActivity.selectItem(0, false);

            final FragmentManager fragmentManager = homeActivity.getFragmentManager();

            //wait a second before finding fragment or else app will crash

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    PredictionsFragment fragment = (PredictionsFragment) fragmentManager.findFragmentById(R.id.content_frame);

                    fragment.favoriteClick(favorite);

                }
            }, 1000);



        } else {

            onListItemSelect(position);


        }
    }

    private void onListItemSelect(int position) {

        HomeActivity activity = (HomeActivity) getActivity();

        adapter.toggleSelection(position);
        boolean hasCheckedItems = adapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = activity.startSupportActionMode(this);
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(adapter.getSelectedCount()) + " selected");
    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                   int position, long id) {
        HomeActivity activity = (HomeActivity) getActivity();


        // if actionmode is null "not started"
        if (mActionMode != null) {
            return false;
        }

        // Start the CAB
        mActionMode = activity.startSupportActionMode(this);
        view.setSelected(true);
        onListItemSelect(position);
        return true;


    }

    public static ArrayList<Favorite> buildFavoritesListOld(Context context){

        //returns null if no favorites
        //returns arraylist of favorites if favorites have been saved

        ArrayList<Favorite> favorites = new ArrayList<Favorite>();
        Gson gson = new Gson();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int numFavorites = sharedPreferences.getInt("numFavorites", 0);

        if(numFavorites == 0){
            return favorites;
        }


        for(int i = 1; i <= numFavorites; i++){
            String json = sharedPreferences.getString("favorite" + i, "");
            Favorite favorite = gson.fromJson(json, Favorite.class);
            favorites.add(favorite);

        }

        return favorites;

    }


    public static ArrayList<Favorite> buildFavoritesList(Context context){

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<Favorite> emptyFavorites = new ArrayList<Favorite>();
        String jsonEmptyFavorites = gson.toJson(emptyFavorites);

        String jsonFavorites = sharedPreferences.getString("favorites", jsonEmptyFavorites);

        Type type = new TypeToken<ArrayList<Favorite>>(){}.getType();
        ArrayList<Favorite> favorites = gson.fromJson(jsonFavorites, type);

        return favorites;


    }


    // 5. Called when the user clicks delete icon
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                // retrieve selected items and delete them out
                SparseBooleanArray selected = adapter
                        .getSelectedIds();
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        Favorite selectedItem = adapter
                                .getItem(selected.keyAt(i));
                        adapter.remove(selectedItem);
                        adapter.notifyDataSetChanged();
                    }
                }
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
        }
    }


    // 4. Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.home_favorites_context, menu);
        return true;
    }

    // 6. Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }

    // 7. Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.removeSelection();
        syncFavorites(getActivity(), adapter.getValues());
        refresh();
        mActionMode = null;
    }

    public void syncFavoritesOld(ArrayList<Favorite> newFavorites){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear all previous entries
        editor.clear();

        //add the new entries in proper order
        int numFavorites = newFavorites.size();

        for(int i = 1; i <= numFavorites; i++){

            Favorite favorite = newFavorites.get(i - 1);
            favorite.setId(i);

            Gson gson = new Gson();
            String json = gson.toJson(favorite);
            editor.putString("favorite" + i, json);

        }

        editor.putInt("numFavorites", numFavorites);

        editor.commit();

    }

    public static void syncFavorites(Context context, ArrayList<Favorite> newFavorites){

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        String jsonNewFavorites = gson.toJson(newFavorites);

        editor.remove("favorites");

        editor.putString("favorites", jsonNewFavorites);

        editor.commit();



    }


    public ActionMode getActionMode(){
        return mActionMode;
    }


}
