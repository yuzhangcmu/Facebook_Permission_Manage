package com.example.fairydream.fbproject_v2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PermissionListFragment extends Fragment
{

    private Activity activity;
    private String appId;
    private HashMap<String,Boolean> permissionMap;
    private ListView list;
    private SetPermissionListHandler setPermissionListHandler;

  //  private boolean syncChangeSwitch = false;

    private final int PERMISSION_CHANGE_SUCCESS = 1;
    private final int PERMISSION_CHANGE_FAILURE = 0;


    public PermissionListFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        appId = getArguments().getString("appID");
        return inflater.inflate(R.layout.fragment_permission_list, container, false);

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        list = (ListView) getActivity().findViewById(R.id.permissionListView);

        activity = getActivity();

        setPermissionListHandler = new SetPermissionListHandler();
        GetPermissionMap getPermissionMap = new GetPermissionMap();
        new Thread(getPermissionMap).start();
    }

    private class SetPermissionListHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            PermissionListAdapter listItemAdapter = new PermissionListAdapter(permissionMap);
            list.setAdapter(listItemAdapter);
        }
    }

    private class GetPermissionMap implements Runnable
    {

        @Override
        public void run()
        {
            DBManager dbManager = new DBManager(getActivity());
            String token = dbManager.getToken();
            dbManager.close();
            permissionMap = ServerConnection.getAppList(token,appId).get(0).getPermissionMap();
            Message.obtain(setPermissionListHandler,1).sendToTarget();
        }
    }

    private class PermissionListAdapter extends BaseAdapter
    {
        HashMap<String,Boolean> permissionMap;
        List<String> permissionList;

        public PermissionListAdapter(HashMap<String,Boolean>permissions)
        {
            permissionList = new ArrayList<String>();
            permissionMap = new HashMap<String, Boolean>(permissions);
            Iterator iter = permissionMap.entrySet().iterator();
            while(iter.hasNext())
            {
                Map.Entry entry = (Map.Entry)iter.next();
                String key = entry.getKey().toString();
                permissionList.add(key);
            }
        }

        @Override
        public int getCount() {
            return permissionList.size();
        }

        @Override
        public Object getItem(int position) {
            return permissionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String permission = (String)getItem(position);
            ViewHolder viewHolder = null;
            if(convertView==null)
            {
                convertView = LayoutInflater.from(activity).inflate(R.layout.permission_item,null);
                viewHolder = new ViewHolder();
                viewHolder.aSwitch = (Switch)convertView.findViewById(R.id.Switch);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    //change permission
                    PermissionChangeHandler permissionChangeHandler = new PermissionChangeHandler();
                    permissionChangeHandler.setButtonView(buttonView);
                    permissionChangeHandler.setChecked(isChecked);
                    PermissionChangeThread changePermissionThread = new PermissionChangeThread(appId,permission,isChecked,permissionChangeHandler);
                    new Thread(changePermissionThread).start();
                }
            });
            viewHolder.aSwitch.setText(permission);
            viewHolder.aSwitch.setChecked(permissionMap.get(permission));

            return convertView;
        }

        private class ViewHolder
        {
            Switch aSwitch;
        }
    }

    class PermissionChangeHandler extends Handler
    {
        private CompoundButton buttonView;
        private boolean isChecked;

        public void setButtonView(CompoundButton buttonView)
        {
            this.buttonView = buttonView;
        }
        public void setChecked(boolean isChecked)
        {
            this.isChecked = isChecked;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PERMISSION_CHANGE_SUCCESS:
                    Toast.makeText(activity, "Sync Successfully.", Toast.LENGTH_SHORT).show();
                    break;
                case PERMISSION_CHANGE_FAILURE:
                    Toast.makeText(activity, "Sync Failed. Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
    //                buttonView.setClickable(!isChecked);
                    break;
            }
        }
    }

    class PermissionChangeThread implements Runnable
    {
        private String appId = "";
        private String permission = "";
        private boolean isChecked;
        private PermissionChangeHandler permissionChangeHandler;

        public PermissionChangeThread(String appId, String permission, boolean isChecked, PermissionChangeHandler permissionChangeHandler)
        {
            this.appId = appId;
            this.permission = permission;
            this.isChecked = isChecked;
            this.permissionChangeHandler = permissionChangeHandler;
        }

        @Override
        public void run()
        {
            DBManager dbManager = new DBManager(activity);
            App app = dbManager.queryApp(appId).get(0);
            HashMap<String,Boolean> permissionMap = app.getPermissionMap();
            permissionMap.put(permission, isChecked);
            app.setPermissionMap(permissionMap);

            try
            {
                // sync to server
                String token = dbManager.getToken();
                ServerConnection.addApp(token,app);
                // update local database
                dbManager.updateApp(app);
                Message.obtain(permissionChangeHandler,PERMISSION_CHANGE_SUCCESS).sendToTarget();
            }
            catch (Exception e)
            {
                Message.obtain(permissionChangeHandler,PERMISSION_CHANGE_FAILURE).sendToTarget();
            }
            dbManager.close();

        }
    }


}
