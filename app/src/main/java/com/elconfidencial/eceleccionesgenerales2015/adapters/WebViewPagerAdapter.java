package com.elconfidencial.eceleccionesgenerales2015.adapters;

import android.app.FragmentContainer;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afll on 30/05/2016.
 */
public class WebViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragmentArrayList;

    public WebViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> fragments){
        super(fm);
        fragmentArrayList=fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentArrayList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
}
