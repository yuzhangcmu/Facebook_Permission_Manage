package com.example.fairydream.fbproject_v2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogFragment extends Fragment {

    private String appId;

    private Activity activity;

    public LogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        appId = getArguments().getString("appID");
        return inflater.inflate(R.layout.fragment_log, container, false);

    }


    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        ListView list = (ListView) getActivity().findViewById(R.id.LogListView);

        ArrayList<HashMap<String, String>> requestLogMapList = getLogList();

        RequestLogAdapter listItemAdapter = new RequestLogAdapter(requestLogMapList);
        list.setAdapter(listItemAdapter);

    }

    private ArrayList<HashMap<String,String>> getLogList()
    {
        DBManager dbManager = new DBManager(getActivity());

        ArrayList<HashMap<String, String>> requestLogMapList = new ArrayList<HashMap<String, String>>();
        ArrayList<RequestLog> requestLogArrayList = dbManager.queryLog(appId);

        for (int i = 0; i < requestLogArrayList.size(); i++) {
            HashMap<String, String> requestLogMap = new HashMap<String, String>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = format.format(requestLogArrayList.get(i).getRequestTime());
            requestLogMap.put("requestTime",dateStr);
            requestLogMap.put("requestPermission", requestLogArrayList.get(i).getRequestPermission());
            requestLogMap.put("requestReason", requestLogArrayList.get(i).getRequestReason());
            requestLogMapList.add(requestLogMap);
        }

        dbManager.close();
        return requestLogMapList;
    }

    private class RequestLogAdapter extends BaseAdapter
    {
        List<String> requestTimeList;
        List<String> requestPermissionList;
        List<String> requestReasonList;

        public RequestLogAdapter(ArrayList<HashMap<String, String>> requestLogMapList)
        {
            this.requestTimeList = new ArrayList<String>();
            this.requestPermissionList = new ArrayList<String>();
            this.requestReasonList = new ArrayList<String>();

            for(int i = 0;i < requestLogMapList.size();i ++)
            {
                requestTimeList.add(requestLogMapList.get(i).get("requestTime"));
                requestPermissionList.add(requestLogMapList.get(i).get("requestPermission"));
                requestReasonList.add(requestLogMapList.get(i).get("requestReason"));
            }
        }

        @Override
        public int getCount() {
            return requestTimeList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            String requestTime = requestTimeList.get(position);
            String requestPermission = requestPermissionList.get(position);
            String requestReason = requestReasonList.get(position);

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.log_item, null);

                viewHolder = new ViewHolder();
                viewHolder.requestTime = (TextView) convertView.findViewById(R.id.LogTimeText);
                viewHolder.requestPermission = (TextView) convertView.findViewById(R.id.LogPermissionText);
                viewHolder.requestReason = (TextView) convertView.findViewById(R.id.LogReasonText);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.requestTime.setText(requestTime);
            viewHolder.requestPermission.setText(requestPermission);
            viewHolder.requestReason.setText(requestReason);

            return convertView;
        }

        private class ViewHolder {
            TextView requestTime;
            TextView requestPermission;
            TextView requestReason;
        }
    }

}
