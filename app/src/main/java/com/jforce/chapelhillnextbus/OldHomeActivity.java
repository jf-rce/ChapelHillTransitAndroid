//package com.jforce.chapelhillnextbus;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//
//import com.astuetz.PagerSlidingTabStrip;
//
//
////implements CustomSwipeToRefresh.OnRefreshListener
//
//public class OldHomeActivity extends FragmentActivity{
//
//
//    public static final int CAROLINA_BLUE = Color.parseColor("#9FC0DF");
//    public static final int NAVY = Color.parseColor("#1D2951");
//    public static final int WHITE = Color.parseColor("#FFFFFF");
//    public static final int LIGHT_GRAY = Color.parseColor("#D3D3D3");
//
//
//    /**
//     * The number of pages (wizard steps) to show in this demo.
//     */
//    public static final int NUM_PAGES = 2;
//
//    /**
//     * The pager widget, which handles animation and allows swiping horizontally to access previous
//     * and next wizard steps.
//     */
//    private ViewPager mPager;
//
//    /**
//     * The pager adapter, which provides the pages to the view pager widget.
//     */
//    private PagerAdapter mPagerAdapter;
//
//    //swipe to refresh layout
//
////    private CustomSwipeToRefresh swipeLayout;
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        // Instantiate a ViewPager and a PagerAdapter.
//        mPager = (ViewPager) findViewById(R.id.pager);
//
//        mPager.setOffscreenPageLimit(NUM_PAGES); // No of fragments to be preserved
//        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
//        mPager.setAdapter(mPagerAdapter);
//
//        //Create tabs
//        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//
//
//        //Customize tabs
//        tabs.setShouldExpand(true);
//        tabs.setDividerColor(NAVY);
//        tabs.setIndicatorColor(NAVY);
//        tabs.setTextColor(WHITE);
//        tabs.setBackgroundColor(CAROLINA_BLUE);
//
//
//        // Bind the tabs to the ViewPager
//        tabs.setViewPager(mPager);
//
//
//        //fetchRouteList();
//
////        swipeLayout = (CustomSwipeToRefresh) findViewById(R.id.swipe_container);
////        swipeLayout.setOnRefreshListener(this);
////        swipeLayout.setColorScheme(R.color.NAVY,
////                R.color.CAROLINA_BLUE,
////                R.color.NAVY,
////                R.color.CAROLINA_BLUE);
////        swipeLayout.setSoundEffectsEnabled(true);
//
//
//
//
////        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
////            @Override
////            public void onPageScrolled(int i, float v, int i2) {
////
////            }
////
////            @Override
////            public void onPageSelected(int position) {
////
////
////                if(position != 1){
////                    ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) mPagerAdapter;
////                    FavoritesFragment fragment = (FavoritesFragment) adapter.getRegisteredFragment(1);
////
////                    if(fragment.getActionMode() != null){
////                        fragment.onDestroyActionMode(fragment.getActionMode());
////                    }
////
////
////                }
////            }
////
////            @Override
////            public void onPageScrollStateChanged(int i) {
////
////            }
////        });
//
//
//        /* Set toolbar as ActionBar
//
//            CANT DO BELOW TWO LINES OF CODE
//
//            setSupportActionBar() is only a valid method of Activity, not FragmentActivity
//
//            Plan:
//            - copy this class as is to new dev version of Home Activity that extends Activity, no FragmentActivity
//            - work on removing tabs and replacing them with navigation drawer
//            - will likely need to make dev variants of fragment classes as well due to interaction w/ viewpager
//
//
//
//         */
//
////        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
//
//
//
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_refresh) {
//            onRefresh();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    public void fetchRouteList(){
//
//        RestClientNextBus.get("routeList&a=chapel-hill", null, new RoutesResponseHandler(this));
//
//
//
//    }
//
//    public void fetchDirectionAndStops(String routeTag){
//
//
//        RestClientNextBus.get("routeConfig&a=chapel-hill&r=" + routeTag, null, new DirectionsStopsPathsResponseHandler(this));
//
//
//    }
//
//    public void fetchPredictions(String stopID){
//
//
//
//        RestClientNextBus.get("predictions&a=chapel-hill&stopId=" + stopID, null, new PredictionsResponseHandler(this));
//
//
//
//
//    }
//
//
//    public PagerAdapter getPagerAdapter() {
//        return mPagerAdapter;
//    }
//
//    @Override
//    public void onBackPressed()
//    {
//
//
//        int page = mPager.getCurrentItem();
//
//        if(page == 0){
//            ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) mPagerAdapter;
//            PredictionsFragment fragment = (PredictionsFragment) adapter.getRegisteredFragment(0);
//
//            int navLevel = fragment.getNavigationLevel();
//
//            if (navLevel == 0){
//                super.onBackPressed();
//
//            }
//            else if(navLevel == 1){
//                fragment.routeClick();
//            }
//            else if(navLevel == 2){
//                fragment.directionClick();
//            }
//            else if(navLevel == 3){
//                fragment.stopClick();
//            }
//
//        }
//        else{
//            super.onBackPressed();
//        }
//
//
//    }
//
//
//    public void onRefresh() {
//
//        new Handler().postDelayed(new Runnable() {
//            @Override public void run() {
//
//                int page = mPager.getCurrentItem();
//
//                if(page == 0){
//                    ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) mPagerAdapter;
//                    PredictionsFragment fragment = (PredictionsFragment) adapter.getRegisteredFragment(page);
//
//                    int navLevel = fragment.getNavigationLevel();
//
//                    if (navLevel == fragment.LEVEL_ROUTE){
//                        fragment.routeClick();
//
//                    }
//                    else if(navLevel == fragment.LEVEL_DIRECTION){
//                        fragment.directionClick();
//                    }
//                    else if(navLevel == fragment.LEVEL_STOP){
//                        fragment.stopClick();
//                    }
//                    else if(navLevel == fragment.LEVEL_PREDICTION){
//                        fragment.cancelRefresh();
//                        fragment.scheduleRefresh();
//                    }
//
//                }
//                else{
//
//
//                    ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) mPagerAdapter;
//                    FavoritesFragment fragment = (FavoritesFragment) adapter.getRegisteredFragment(page);
//
//                    fragment.refresh();
//
//                }
//
//
////                CustomSwipeToRefresh swipeLayout = (CustomSwipeToRefresh) findViewById(R.id.swipe_container);
////                swipeLayout.setRefreshing(false);
//            }
//        }, 1);
//    }
//
//
//    public void saveFavorite(View view){
//
//        int page = mPager.getCurrentItem();
//
//        if(page == 0) {
//            ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) mPagerAdapter;
//            PredictionsFragment fragment = (PredictionsFragment) adapter.getRegisteredFragment(0);
//            fragment.saveFavorite();
//        }
//
//
//
//    }
//
//
//
//}
