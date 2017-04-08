package com.reabyo.crme;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.reabyo.crme.fragments.ViewPager_Fr;

public class Activity_Search_Customer extends FragmentActivity {

    public ViewPager_Fr vp;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_customer);

        vp = new ViewPager_Fr();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainContainer, vp);
        transaction.commit();
    }
}
