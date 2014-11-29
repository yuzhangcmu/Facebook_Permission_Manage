package com.example.fairydream.fbproject_v2;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PermissionSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_setting);

        Intent intent =getIntent();
        String appId = intent.getStringExtra(AppListFragment.APP_ID);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

        PermissionListFragment permissionListTab = new PermissionListFragment();
        Bundle permissionBundle = new Bundle();
        permissionBundle.putString("appID",appId);
        permissionListTab.setArguments(permissionBundle);

        LogFragment logTab = new LogFragment();
        Bundle logBundle = new Bundle();
        logBundle.putString("appID",appId);
        logTab.setArguments(logBundle);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener1 = new PermissionTabListener(permissionListTab);
        ActionBar.TabListener tabListener2 = new PermissionTabListener(logTab);

        // Add 2 tabs, specifying the tab's text and TabListener
        actionBar.addTab(actionBar.newTab().setText("Permission Settings ").setTabListener(tabListener1));
        actionBar.addTab(actionBar.newTab().setText("View Logs").setTabListener(tabListener2));

    }

}
