package com.example.fairydream.fbproject_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;

public class PermissionAPIActivity extends Activity {

    private String appId;
    private String appName;
    private String appDescription;
    private String requestReason;
    private int requestType;
    private String password;
    private Request request;
    private boolean startAccessResource = false;

    private RequestSolver requestSolver;
    private ArrayList<String> permissionRequestList;
    private ReturnContentHandler returnContentHandler;
    private AppAuthenHandler appAuthenHandler;
    private AppAuthorizeHandler appAuthorizeHandler;

    private TextView textView;
    private View dialogLayout;

    private final int INTERNET_NOT_CONNECT = -1;

    private final int GET_RESOURCE_SUCCESS = 1;
    private final int GET_RESOURCE_FAILURE = 0;

    private final int PERMISSION_NOT_ALLOWED = 0;
    private final int PERMISSION_ALLOWED = 1;
    private final int NEED_NEW_PERMISSION = 2;

    private final int APP_AUTHORIZE_SUCCESS = 1;
    private final int APP_AUTHORIZE_FAILURE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the signature of calling app, and generate its key hash
        String callingPackage = getCallingPackage();
        String keyHash = "";
        try
        {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(callingPackage, PackageManager.GET_SIGNATURES);
            android.content.pm.Signature signature = packageInfo.signatures[0];
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(signature.toByteArray());
            keyHash = Base64.encodeToString(md.digest(),Base64.DEFAULT);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Intent sourceIntent = getIntent();
        appId = keyHash;
        appName = sourceIntent.getStringExtra("AppName");
        appDescription = sourceIntent.getStringExtra("AppDescription");
        requestType = sourceIntent.getIntExtra("RequestType",0);
        requestReason = sourceIntent.getStringExtra("RequestReason");

        request = new Request(appId,appName,appDescription,requestType,requestReason);


        requestSolver = new RequestSolver(requestType,this);

        // Find out whether the app has the permission
        appAuthenHandler = new AppAuthenHandler();
        AppAuthenThread appAuthenThread = new AppAuthenThread();
        new Thread(appAuthenThread).start();
    }

    private void showPermissionRequestDialog()
    {
        StringBuilder message = new StringBuilder();
        message.append(appName).append(" is Requesting Permission: ");
        int i;
        permissionRequestList = requestSolver.getPermissionRequestList();
        for (i = 0;i < permissionRequestList.size()-1;i ++ )
        {
            message.append(permissionRequestList.get(i)).append(", ");
        }
        message.append(permissionRequestList.get(i));

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(message);

        LayoutInflater inflater = getLayoutInflater();
        dialogLayout = inflater.inflate(R.layout.dialog_permission_grant,(ViewGroup) findViewById(R.id.dialog_permission_api));
        builder.setView(dialogLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Check the password
                EditText passwordEditText = (EditText) dialogLayout.findViewById(R.id.password_permission_api);
                password = passwordEditText.getText().toString();
                appAuthorizeHandler = new AppAuthorizeHandler();
                startAccessResource = true;
                AppAuthorizeThread appAuthorizeThread = new AppAuthorizeThread();
                new Thread(appAuthorizeThread).start();
                try {
                    Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialogInterface, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            // User refuse to grant permission
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
         //       if(!startAccessResource)
         //       {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
         //       }
            }
        });
        builder.create().show();
    }

    private void recordRequestLog()
    {
        StringBuilder permissionsRequest = new StringBuilder();
        RequestLog requestLog = new RequestLog();
        requestLog.setId(appId);
        requestLog.setAppName(appName);
        requestLog.setRequestTime(new Date(System.currentTimeMillis()));
        for (String permission : permissionRequestList)
        {
            if(permissionsRequest.length()!=0)
            {
                permissionsRequest.append(", ");
            }
            permissionsRequest.append(permission);
        }
        requestLog.setRequestPermission(permissionsRequest.toString());
        requestLog.setRequestReason(requestReason);

        DBManager dbManager = new DBManager(this);
        dbManager.addLog(requestLog);
    }

    class ReturnContentHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            Intent resultIntent = new Intent();
            switch (msg.what)
            {
                case GET_RESOURCE_SUCCESS:

                    resultIntent.putExtra("contentResult", msg.obj.toString());
                    setResult(Activity.RESULT_OK, resultIntent);
                    break;

                case GET_RESOURCE_FAILURE:
                    break;
            }
            finish();
        }
    }

    class GetResourceThread implements Runnable
    {

        @Override
        public void run()
        {
            Looper.prepare();
            try
            {
                String contentResult = requestSolver.getContent();
                Message.obtain(returnContentHandler, GET_RESOURCE_SUCCESS, contentResult).sendToTarget();
            }
            catch (Exception e)
            {
                Message.obtain(returnContentHandler, GET_RESOURCE_FAILURE).sendToTarget();
            }
        }
    }

    class AppAuthenHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case PERMISSION_NOT_ALLOWED:
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                    break;
                case PERMISSION_ALLOWED:
                    returnContentHandler = new ReturnContentHandler();
                    GetResourceThread getResource = new GetResourceThread();
                    new Thread(getResource).start();
                    break;
                case NEED_NEW_PERMISSION:
                    showPermissionRequestDialog();
                    break;
                case INTERNET_NOT_CONNECT:
                    Toast.makeText(PermissionAPIActivity.this, "Please Check Your Internet Connection.",Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                    break;
            }
        }
    }



    class AppAuthenThread implements Runnable
    {

        @Override
        public void run()
        {
            DBManager dbManager = new DBManager(PermissionAPIActivity.this);
            String token = dbManager.getToken();
            dbManager.close();

        //    Message.obtain(appAuthenHandler, NEED_NEW_PERMISSION).sendToTarget();

            int appAuthenResult = 0;
            try
            {
                appAuthenResult = ServerConnection.appAuthen(token,request);
                Message.obtain(appAuthenHandler,appAuthenResult).sendToTarget();
            }
            catch (Exception e)
            {
                Message.obtain(appAuthenHandler, INTERNET_NOT_CONNECT).sendToTarget();
            }
        }
    }

    class AppAuthorizeHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what) {
                case APP_AUTHORIZE_SUCCESS:
                    // Give data back to app
                    returnContentHandler = new ReturnContentHandler();
                    GetResourceThread getResource = new GetResourceThread();
                    new Thread(getResource).start();
                    break;
                case APP_AUTHORIZE_FAILURE:
                    // Incorrect password
                    TextView textView = (TextView) dialogLayout.findViewById(R.id.message);
                    textView.setText("Incorrect Password!");
                    break;
                case INTERNET_NOT_CONNECT:
                    Toast.makeText(PermissionAPIActivity.this, "Please Check Your Internet Connection.",Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                    break;
            }
        }

    }

    class AppAuthorizeThread implements Runnable {
        @Override
        public void run()
        {

         //   Message.obtain(appAuthorizeHandler, APP_AUTHORIZE_SUCCESS).sendToTarget();

            // Send request to server
            DBManager dbManager = new DBManager(PermissionAPIActivity.this);
            String token = dbManager.getToken();
            int appAuthorizeResult = 0;
            dbManager.close();
            try
            {
                appAuthorizeResult = ServerConnection.appAuthorize(token, password, request);
                Message.obtain(appAuthorizeHandler, appAuthorizeResult).sendToTarget();
            }
            catch(Exception e)
            {
                Message.obtain(appAuthorizeHandler,INTERNET_NOT_CONNECT).sendToTarget();
            }

        }
    }

}
