package com.devtechnician.paledj.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 8/21/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaleDJPagerAdapter extends FragmentPagerAdapter  {

      List<Fragment> fragments;
      FragmentManager fragmentManager;
      private final static String[] TABS = new String[]{"Server","Device","PlayList"};

    public PaleDJPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        this.fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int i) {
        return this.fragments.get(i);
    }


    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TABS[position].toUpperCase();
    }


}
