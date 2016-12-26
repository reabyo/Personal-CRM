package com.example.user.crm_project.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.user.crm_project.Activity_Add_Customer;
import com.example.user.crm_project.Activity_Calendar;
import com.example.user.crm_project.R;
import com.example.user.crm_project.adapters.FragmentAdapter;

/**
 * Created by user on 12/6/2016.
 */

public class ViewPager_Fr extends Fragment implements View.OnClickListener {

    private ViewPager viewPager;
    private FragmentAdapter adapter;
    private TabLayout tabLayout;

    public ViewPager_Fr() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_fr, container, false);
        adapter = new FragmentAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.search_pager);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) view.findViewById(R.id.search_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Button moveToAdd = (Button) view.findViewById(R.id.moveToAdd);
        Button moveToCalendar = (Button) view.findViewById(R.id.moveToCalendar2);
        moveToAdd.setOnClickListener(this);
        moveToCalendar.setOnClickListener(this);

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                    }
                });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.moveToAdd:
                Intent toAddIntent = new Intent(getActivity(), Activity_Add_Customer.class);
                startActivity(toAddIntent);
                break;
            case R.id.moveToCalendar2:
                Intent toCalendarIntent = new Intent(getActivity(), Activity_Calendar.class);
                startActivity(toCalendarIntent);
                break;
        }
    }
}
