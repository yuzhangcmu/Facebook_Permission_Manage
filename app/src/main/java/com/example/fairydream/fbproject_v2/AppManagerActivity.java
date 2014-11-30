package com.example.fairydream.fbproject_v2;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class AppManagerActivity extends Activity {

    private ActionBar actionbar;
    private AppListFragment appfrag = new AppListFragment();
    private SettingFragment setfrag = new SettingFragment();
    private SyncHandler syncHandler;

    private final int SYNC_SUCCESS = 1;
    private final int SYNC_FAILURE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);

        appfrag = new AppListFragment();
        setfrag = new SettingFragment();

        HashMap<String,Boolean> fbPermissionMap = new HashMap<String, Boolean>();
        fbPermissionMap.put("read_contact",true);
        fbPermissionMap.put("read_sms",false);
        fbPermissionMap.put("access_location",true);
        App fbApp = new App("23sdwe", "FB");
        fbApp.setPermissionMap(fbPermissionMap);
        fbApp.setDescription("Read Contactlist\nRead SMS\nGet Fine Location\nInternet");

        HashMap<String,Boolean> happyCloudPermissionMap = new HashMap<String, Boolean>();
        happyCloudPermissionMap.put("read_contact",false);
        happyCloudPermissionMap.put("read_sms",false);
        App happyCloudApp = new App("wewe222", "HappyCloud");
        happyCloudApp.setPermissionMap(happyCloudPermissionMap);
        happyCloudApp.setDescription("Eat less... Exercise more...");

        HashMap<String,Boolean> tfboysPermissionMap = new HashMap<String, Boolean>();
        happyCloudPermissionMap.put("read_contact",false);
        happyCloudPermissionMap.put("read_sms",false);
        App tfboysApp = new App("20230806", "TFBOYS");
        tfboysApp.setPermissionMap(tfboysPermissionMap);
        tfboysApp.setDescription("Ten-year Promise");

        DBManager dbManager = new DBManager(this);
        dbManager.addApp(fbApp);
        dbManager.addApp(happyCloudApp);
        dbManager.addApp(tfboysApp);

        Date curDate = new  Date(System.currentTimeMillis());

        RequestLog requestLog1 = new RequestLog("23sdwe", "FB", curDate, "read_contact", "Nothing");
        RequestLog requestLog2 = new RequestLog("wewe222", "HappyCloud", curDate, "read_sms", "=====");

        dbManager.addLog(requestLog1);
        dbManager.addLog(requestLog2);

        dbManager.close();


        syncHandler = new SyncHandler();
        SyncThread syncThread = new SyncThread();
        new Thread(syncThread).start();
    }


    class SyncHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case SYNC_SUCCESS:
                    break;
                case SYNC_FAILURE:
                    Toast.makeText(AppManagerActivity.this, "Sync Fail ", Toast.LENGTH_SHORT).show();
                    break;
            }
            ActionBar.Tab applist = actionbar.newTab().setText("App List").setTabListener(new AppManagerTabListener(appfrag));
            ActionBar.Tab setting = actionbar.newTab().setText("setting").setTabListener(new AppManagerTabListener(setfrag));
            actionbar.addTab(applist);
            actionbar.addTab(setting);
        }
    }

    class SyncThread implements Runnable
    {

        @Override
        public void run()
        {
            DBManager dbManager = new DBManager(AppManagerActivity.this);
            String token = dbManager.getToken();

            try
            {
                ArrayList<App> appArrayList = ServerConnection.getAppList(token,null);
                ArrayList<RequestLog> requestLogsArrayList = ServerConnection.getRequestLogs(token, null);
                dbManager.deleteAllApp();
                dbManager.addApp(appArrayList);
                dbManager.deleteAllLog();
                dbManager.addLog(requestLogsArrayList);
                Message.obtain(syncHandler,SYNC_SUCCESS).sendToTarget();
            }
            catch (Exception e)
            {
                Message.obtain(syncHandler,SYNC_FAILURE).sendToTarget();
            }
            dbManager.close();

        }
    }
}
