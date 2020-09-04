package com.shramik.india;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class SearchEmployer extends Fragment {


    public SearchEmployer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_employer, container, false);
        TabLayout tabLayout = v.findViewById(R.id.tab);
        final ViewPager viewPager = v.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Daily Job"));
        tabLayout.addTab(tabLayout.newTab().setText("Regular Job"));

        JobPagerAdapter jobPagerAdapter = new JobPagerAdapter(getActivity().getApplicationContext(), getActivity().getSupportFragmentManager(), 2);
        viewPager.setAdapter(jobPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }
}