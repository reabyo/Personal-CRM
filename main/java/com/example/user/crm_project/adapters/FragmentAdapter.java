package com.example.user.crm_project.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.user.crm_project.fragments.AdvanceSearch_Fr;
import com.example.user.crm_project.fragments.QuickSearch_Fr;


/**
 * Created by user on 6/3/2016.
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Fragment quick = new QuickSearch_Fr();
                return quick;
            case 1:
                Fragment advanced = new AdvanceSearch_Fr();
                return advanced;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position){
            case 0:
                title = "Quick Search";
                return title;
            case 1:
                title = "Advance Search";
                return title;
        }
        return null;
    }
}
