package com.example.fairydream.fbproject_v2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//import net.sqlcipher.database.SQLiteDatabase;
//import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by fairydream on 14-11-11.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private final static String DB_NAME ="FB.db";
    private final static int VERSION = 1;
    public final static String[] permissionName= new String[]{"read_contact","read_sms","access_location"};
    public final static String SECRET_KEY="95279527";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
       // super(context, name, null, VERSION, new SQLCipherV3Helper(context));
        super(context, name, factory, version);
    }

    public DBHelper(Context context)
    {
        //super(context, DB_NAME, null, VERSION, new SQLCipherV3Helper(context));
        super(context, DB_NAME, null, VERSION);
    }

    public DBHelper(Context cxt,int version)
    {
        this(cxt,DB_NAME,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        // Create user info table
        StringBuilder sql = new StringBuilder();
        sql.append("create table UserInfoTable(")
                .append("userToken text,")
                .append("username text);");
       //         .append("password text);");
        sqLiteDatabase.execSQL(sql.toString());
        sql.setLength(0);

        // Create permission list table

        sql.append("create table AppPermissionTable(")
                .append("_id char(25),")
                .append("appName text,")
                .append("appDescription text,");
        int i;
        for (i = 0;i < permissionName.length-1;i ++)
        {
            sql.append(permissionName[i]).append(" int(1),");
        }
        sql.append(permissionName[i]).append(" int(1));");
        sqLiteDatabase.execSQL(sql.toString());
        sql.setLength(0);

        //Create log table
        sql = new StringBuilder();
        sql.append("create table RequestLogTable(")
                .append("_id char(25),")
                .append("appName text,")
                .append("requestTime text,")
                .append("requestPermission text,")
                .append("requestReason text);");
        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2)
    {

    }
}
