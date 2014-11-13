//package com.jforce.chapelhillnextbus;
//
//import android.app.Activity;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.util.SparseArray;
//import android.view.ViewGroup;
//
///**
// * Created by justinforsyth on 10/4/14.
// /**
// * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
// * sequence.
// */
//public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
//
//    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
//    Activity activity;
//
//    public ScreenSlidePagerAdapter(FragmentManager fm, Activity activity) {
//        super(fm);
//        this.activity = activity;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//
//        if (position == 0){
//            return new PredictionsFragment();
//        }
//        if (position == 1){
//            return new FavoritesFragment();
//        }
////        if (position == 2){
////            return new SchedulesFragment();
////        }
//
//        return null;
//
//
//    }
//
//    @Override
//    public int getCount() {
//        return OldHomeActivity.NUM_PAGES;
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//
//        if (position == 0){
//
//            return activity.getResources().getString(R.string.predictions);
//
//        }
//        if (position == 1){
//
//            return activity.getResources().getString(R.string.favorites);
//        }
//
////        if (position == 2){
////            return activity.getResources().getString(R.string.schedules);
////        }
//
//        return null;
//
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        registeredFragments.put(position, fragment);
//        return fragment;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        registeredFragments.remove(position);
//        super.destroyItem(container, position, object);
//    }
//
//    public Fragment getRegisteredFragment(int position) {
//        return registeredFragments.get(position);
//    }
//
//}