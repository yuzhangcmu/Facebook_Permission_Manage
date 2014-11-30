package com.example.fairydream.fbproject_v2;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by fairydream on 14-11-11.
 */
public class DBManager
{
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context)
    {
        SQLiteDatabase.loadLibs(context);
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase(DBHelper.SECRET_KEY);
    }


    /*
     * check whether the user has registered
     */
    public boolean isUserRegister()
    {
        Cursor cursor;
        cursor = db.rawQuery("select * from UserInfoTable", new String[]{});
        if(cursor.moveToNext())
        {
            cursor.close();
            return true;
        }
        else
        {
            cursor.close();
            return false;
        }
    }

    /*
     * add the user info
     */

    public void addUserToken(String username, String token)
    {
        db.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            values.put("userToken",token);
            values.put("username",username);
            db.insert("UserInfoTable",null,values);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    /*
    * get the user's token
    */
    public String getToken()
    {
        String token = null;
        Cursor cursor;
        cursor = db.rawQuery("select * from UserInfoTable", new String[]{});
        if(cursor.moveToNext())
        {
            token = cursor.getString(cursor.getColumnIndex("userToken"));
        }
        cursor.close();
        return token;
    }

   /*
    * get the username
    */
    public String getUsername()
    {
        String username = null;
        Cursor cursor;
        cursor = db.rawQuery("select * from UserInfoTable", new String[]{});
        if(cursor.moveToNext())
        {
            username = cursor.getString(cursor.getColumnIndex("username"));
        }
        cursor.close();
        return username;
    }

    /*
     * add App
     */
    public void addApp(App app)
    {

        Cursor cursor = db.rawQuery("select * from AppPermissionTable where _id = ?", new String[]{app.getId()});
        if(cursor.moveToNext())
        {
            updateApp(app);
            return;
        }

        db.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            values.put("_id",app.getId());
            values.put("appName", app.getAppName());
            values.put("appDescription",app.getDescription());
            values = appPermissionMap2dbValues(app.getPermissionMap(), values);
            db.insert("AppPermissionTable",null,values);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public void addApp(ArrayList<App> appArrayList)
    {
        for (App app : appArrayList)
        {
            addApp(app);
        }
    }


    /*
     * query app
     */
    public ArrayList<App> queryApp(String appId)
    {
        Cursor cursor;
        if (appId == null)
        {
            cursor = db.rawQuery("select * from AppPermissionTable",null);
        }
        else
        {
            cursor = db.rawQuery("select * from AppPermissionTable where _id = ?", new String[]{appId});
        }

        ArrayList<App> appList = new ArrayList<App>();

        while (cursor.moveToNext())
        {
            App app = new App();
            HashMap<String, Boolean> permissionMap = dbCursor2appPermissionMap(cursor);
            app.setId(cursor.getString(cursor.getColumnIndex("_id")));
            app.setAppName(cursor.getString(cursor.getColumnIndex("appName")));
            app.setDescription(cursor.getString(cursor.getColumnIndex("appDescription")));
            app.setPermissionMap(permissionMap);
            appList.add(app);
        }

        cursor.close();
        return appList;
    }


    public void updateApp(App app)
    {
        db.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            values.put("_id",app.getId());
            values.put("appName", app.getAppName());
            values.put("appDescription",app.getDescription());
            values = addPermissions2dbValues(app.getPermissionMap(),values);
            db.update("AppPermissionTable",values,"_id = ?", new String[]{String.valueOf(app.getId())});
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }

    }

    /*
     * delete all app
     */
    public void deleteAllApp()
    {
        db.beginTransaction();
        try
        {
            db.delete("AppPermissionTable",null,null);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

     /*
        permissionMap:
            Key: read_sms, read_contact, get_location
            Value: 0 - not grant
            Value: 1 - grant
            Value: 2 - to be decide
     */
    private HashMap<String,Boolean> dbCursor2appPermissionMap(Cursor cursor)
    {
        HashMap<String, Boolean> appPermissionMap = new HashMap<String, Boolean>();
        String[] permissionName = DBHelper.permissionName;
        for (int i = 0;i < permissionName.length;i ++) {
            int isGrant = cursor.getInt(cursor.getColumnIndex(permissionName[i]));
            if (isGrant == 2) {
                // Permission not request
                continue;
            }
            appPermissionMap.put(permissionName[i], isGrant==1);
        }
        return appPermissionMap;
    }

    private ContentValues appPermissionMap2dbValues(HashMap<String,Boolean> appPermissionMap, ContentValues values)
    {
        String[] permissionName = DBHelper.permissionName;
        for (int i = 0;i < permissionName.length;i ++)
        {
            if(appPermissionMap.get(permissionName[i])==null)
            {
                // Permission not request
                values.put(permissionName[i],2);
            }
            else
            {
                values.put(permissionName[i],appPermissionMap.get(permissionName[i])?1:0);
            }
        }
        return values;
    }

    private ContentValues addPermissions2dbValues(HashMap<String,Boolean> appPermissionMap, ContentValues values)
    {

        Set<Map.Entry<String, Boolean>> sets = appPermissionMap.entrySet();
        for(Map.Entry<String, Boolean> entry : sets)
        {
            values.put(entry.getKey(),entry.getValue()?1:0);
        }
        return  values;
    }

    public void addLog(RequestLog requestLog)
    {

        db.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            values.put("_id",requestLog.getId());
            values.put("appName", requestLog.getAppName());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = format.format(requestLog.getRequestTime());
            values.put("requestTime",dateStr);
            values.put("requestPermission",requestLog.getRequestPermission());
            values.put("requestReason",requestLog.getRequestReason());
            db.insert("RequestLogTable",null,values);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public void addLog(ArrayList<RequestLog> requestLogArrayList)
    {
        for (RequestLog requestLog : requestLogArrayList)
        {
            addLog(requestLog);
        }
    }


    public ArrayList<RequestLog> queryLog(String appId)
    {
        Cursor cursor;
        if (appId == null)
        {
            cursor = db.rawQuery("select * from RequestLogTable",null);
        }
        else
        {
            cursor = db.rawQuery("select * from RequestLogTable where _id = ?", new String[]{appId});
        }

        ArrayList<RequestLog> requestLogsList = new ArrayList<RequestLog>();

        while (cursor.moveToNext())
        {
            RequestLog requestLog = new RequestLog();
            requestLog.setId(cursor.getString(cursor.getColumnIndex("_id")));
            requestLog.setAppName(cursor.getString(cursor.getColumnIndex("appName")));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(cursor.getString(cursor.getColumnIndex("requestTime")));
                requestLog.setRequestTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            requestLog.setRequestPermission(cursor.getString(cursor.getColumnIndex("requestPermission")));
            requestLog.setRequestReason(cursor.getString(cursor.getColumnIndex("requestReason")));
            requestLogsList.add(requestLog);
        }

        cursor.close();
        return requestLogsList;
    }

    /*
     * delete all log
     */
    public void deleteAllLog()
    {
        db.beginTransaction();
        try
        {
            db.delete("RequestLogTable",null,null);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public void close()
    {
        db.close();
    }


}
