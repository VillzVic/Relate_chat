package com.example.appzonepc2.relate.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appzonepc2 on 15/02/2018.
 */

public class tabPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> FragmentList = new ArrayList<>();
    List<String> FragmentTitleList = new ArrayList<>();


    public tabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);
    }

    @Override
    public int getCount() {
        return FragmentTitleList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment,String title){
        FragmentList.add(fragment);
        FragmentTitleList.add(title);
    }
}
