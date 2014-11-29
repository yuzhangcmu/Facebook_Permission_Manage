package com.example.fairydream.fbproject_v2;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by fairydream on 14-11-24.
 */
public class RequestSolver
{
    private int requestType;
    private Context context;

    private final static int READ_CONTACT = 1;
    private final static int READ_SMS = 2;
    private final static int ACCESS_LOCATION = 3;

    private String contentResult = null;
    private boolean contentReady = false;

    public RequestSolver(int requestType, Context context)
    {
        this.requestType = requestType;
        this.context = context;
    }


    public ArrayList<String> getPermissionRequestList()
    {
        ArrayList<String> permissionRequestList = new ArrayList<String>();
        switch (requestType)
        {
            //
            case READ_CONTACT:
                permissionRequestList.add("read_contact");
                break;
            case READ_SMS:
                permissionRequestList.add("read_sms");
                break;
            case ACCESS_LOCATION:
                permissionRequestList.add("access_location");
                break;
        }
        return permissionRequestList;
    }


    public String getContent()
    {
        switch (requestType)
        {
            //
            case READ_CONTACT:
                getContact();
                break;
            case READ_SMS:
                getSMS();
                break;
            case ACCESS_LOCATION:
                getLocation();
                break;
        }

        while (!contentReady);
        contentReady = false;
        return contentResult;
    }

    private void getContact()
    {
        StringBuilder contentResultBuiler = new StringBuilder();

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        ArrayList<Integer> colIndex = new ArrayList<Integer>();
        for (int i = 0;i < projection.length;i ++)
        {
            colIndex.add(cursor.getColumnIndex(projection[i]));
        }

        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                for(int i = 0;i < colIndex.size();i ++)
                {
                    contentResultBuiler.append(cursor.getString(colIndex.get(i))).append("\t");
                }
                contentResultBuiler.append("\n");
            }
        }
        contentResult = contentResultBuiler.toString();
        contentReady = true;

    }

    private void getSMS()
    {
        StringBuilder contentResultBuiler = new StringBuilder();

        Uri uri = Uri.parse("content://sms/");
        String[] projection = new String[]{"person","address","date","body"};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        ArrayList<Integer> colIndex = new ArrayList<Integer>();
        for (int i = 0;i < projection.length;i ++)
        {
            colIndex.add(cursor.getColumnIndex(projection[i]));
        }

        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                for(int i = 0;i < colIndex.size();i ++)
                {
                    contentResultBuiler.append(cursor.getString(colIndex.get(i))).append("\t");
                }
                contentResultBuiler.append("\n");
            }
        }
        contentResult = contentResultBuiler.toString();
        contentReady = true;
    }

    private void getLocation()
    {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                contentResult = String.valueOf(latitude) + ";" + String.valueOf(longitude);
                contentReady = true;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        contentResult = String.valueOf(latitude) + ";" + String.valueOf(longitude);
        contentReady = true;
    }
}
