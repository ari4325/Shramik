package com.shramik.india;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class JobPagerAdapter extends FragmentPagerAdapter {
    Context context;
    int tabs;
    public JobPagerAdapter(Context context, FragmentManager fm, int tabs){
        super(fm);
        this.context = context;
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                DailyJob dailyJob = new DailyJob();
                return dailyJob;
            case 1:
                RegularJob regularJob = new RegularJob();
                return regularJob;
        }
        return null;
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return tabs;
    }
}
