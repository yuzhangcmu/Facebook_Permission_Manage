package com.example.fairydream.fbproject_v2;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class AppManagerTabListener implements TabListener
{
    Fragment myfragment;

    public AppManagerTabListener(Fragment fragment)
    {
        myfragment = fragment;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
        ft.replace(R.id.AppManagerLayout, myfragment);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
        ft.remove(myfragment);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

}
