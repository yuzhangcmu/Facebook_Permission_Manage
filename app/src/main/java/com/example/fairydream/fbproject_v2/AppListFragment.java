package com.example.fairydream.fbproject_v2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppListFragment extends Fragment {

    private Activity activity;
    private GetAppListHandler getAppListHandler;
    private  ListView list;

    public final static String APP_ID = "APP_ID";
    private final int SUCCESS = 1;
    private final int FAILURE = 0;



    public AppListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        list = (ListView) getActivity().findViewById(R.id.AppListView);



     //   getAppListHandler = new GetAppListHandler();
     //   GetAppListThread getAppListThread = new GetAppListThread();
     //   new Thread(getAppListThread).start();
        ArrayList<HashMap<String, String>> appMapList = getAppMapList();

        AppListAdapter listItemAdapter = new AppListAdapter(appMapList);
        list.setAdapter(listItemAdapter);
    }

    private ArrayList<HashMap<String, String>> getAppMapList() {

        DBManager dbManager = new DBManager(getActivity());

        ArrayList<HashMap<String, String>> appMapList = new ArrayList<HashMap<String, String>>();
        ArrayList<App> appArrayList = dbManager.queryApp(null);

        for (int i = 0; i < appArrayList.size(); i++) {
            HashMap<String, String> appDescriptionMap = new HashMap<String, String>();
            appDescriptionMap.put("appId",String.valueOf(appArrayList.get(i).getId()));
            appDescriptionMap.put("appName",appArrayList.get(i).getAppName());
            appDescriptionMap.put("appDescription",appArrayList.get(i).getDescription());
            appMapList.add(appDescriptionMap);
        }

        dbManager.close();
        return appMapList;

    }

    private class AppListAdapter extends BaseAdapter
    {

        List<String> appIdList;
        List<String> appNameList;
        List<String> appDescriptionList;

        public AppListAdapter(ArrayList<HashMap<String, String>> appMapList)
        {
            this.appIdList = new ArrayList<String>();
            this.appNameList = new ArrayList<String>();
            this.appDescriptionList = new ArrayList<String>();

            for(int i = 0;i < appMapList.size();i ++)
            {
                appIdList.add(appMapList.get(i).get("appId"));
                appNameList.add(appMapList.get(i).get("appName"));
                appDescriptionList.add(appMapList.get(i).get("appDescription"));
            }
        }

        @Override
        public int getCount() {
            return appNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return appNameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            String appName = (String) getItem(position);
            String appDescription = appDescriptionList.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.app_item, null);
                convertView.setClickable(true);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent permissionSetIntent = new Intent();
                        permissionSetIntent.putExtra(APP_ID, appIdList.get(position));
                        permissionSetIntent.setClass(activity, PermissionSettingActivity.class);
                        startActivity(permissionSetIntent);
                    }
                });

                viewHolder = new ViewHolder();
                viewHolder.appName = (TextView) convertView.findViewById(R.id.AppName);
                viewHolder.appDescription = (TextView) convertView.findViewById(R.id.AppDescription);

                /*  Added by Yu Zhang . */
                viewHolder.app_pic = (ImageView) convertView.findViewById(R.id.app_pic);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.appName.setText(appName);
            viewHolder.appDescription.setText(appDescription);

            if (appName.equals("FB")) {
                viewHolder.app_pic.setImageResource(R.drawable.facebook2);
            } else if (appName.equals("HappyCloud")) {
                viewHolder.app_pic.setImageResource(R.drawable.cloud);
            } else {
                viewHolder.app_pic.setImageResource(R.drawable.tfboy3);
            }


            //ImageView head_portrait = (ImageView) layout.findViewById(R.id.head_portrait);

            return convertView;
        }


        private class ViewHolder {
            TextView appName;
            TextView appDescription;
            ImageView app_pic;
        }
    }

    class GetAppListHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SUCCESS:

                    ArrayList<HashMap<String, String>> appMapList = new ArrayList<HashMap<String, String>>();
                    ArrayList<App> appArrayList = (ArrayList<App>) msg.obj;

                    for (int i = 0; i < appArrayList.size(); i++) {
                        HashMap<String, String> appDescriptionMap = new HashMap<String, String>();
                        appDescriptionMap.put("appId",String.valueOf(appArrayList.get(i).getId()));
                        appDescriptionMap.put("appName",appArrayList.get(i).getAppName());
                        appDescriptionMap.put("appDescription",appArrayList.get(i).getDescription());
                        appMapList.add(appDescriptionMap);
                    }
                    AppListAdapter listItemAdapter = new AppListAdapter(appMapList);
                    list.setAdapter(listItemAdapter);
                    break;
                case FAILURE:
                    break;
            }
        }
    }

    class GetAppListThread implements Runnable
    {

        @Override
        public void run()
        {
            try
            {
                DBManager dbManager = new DBManager(activity);
                String token = dbManager.getToken();
                dbManager.close();
                ArrayList<App> appArrayList = ServerConnection.getAppList(token, null);
                Message.obtain(getAppListHandler, SUCCESS,appArrayList).sendToTarget();
            }
            catch(Exception e)
            {
                Message.obtain(getAppListHandler, FAILURE).sendToTarget();
            }
        }
    }

}
