package com.example.fairydream.fbproject_v2;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by fairydream on 14-11-27.
 */
public class ServerConnection
{
    private static final String serverAddress = "";


    /*
     * Sigh up a new account
     * return SUCCESS   token
     *        FAILURE   reason
     */
    public static String signUp(String username, String password)
    {
        HttpPost httpPost = new HttpPost(serverAddress);
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("action", "signUp"));
        pairs.add(new BasicNameValuePair("username", username));
        pairs.add(new BasicNameValuePair("password", password));

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        String httpResult = "FAIL";
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                httpResult = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpResult;
    }

    /*
     * Sign in
     * Return
     */
    public static String signIn(String token, String username, String password)
    {
        HttpPost httpPost = new HttpPost(serverAddress);
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("action", "signIn"));
        pairs.add(new BasicNameValuePair("token", token));
        pairs.add(new BasicNameValuePair("username", username));
        pairs.add(new BasicNameValuePair("password", password));

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        String httpResult = null;
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                httpResult = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpResult;
    }

    /*
     *
     */
    public static ArrayList<App> getAppList(String token, String appId)
    {
        HttpPost httpPost = new HttpPost(serverAddress);
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("action", "getAppList"));
        pairs.add(new BasicNameValuePair("token", token));
        pairs.add(new BasicNameValuePair("appId", appId));

        UrlEncodedFormEntity urlEncodedFormEntity = null;

        ArrayList<App> appArrayList = new ArrayList<App>();
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(httpResponse.getEntity().getContent());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while((line = bufferedReader.readLine())!=null)
                {
                    String[] appInfo = line.split("\t");
                    if(appInfo.length==3)
                    {
                        App app = new App();
                        app.setId(appInfo[0]);
                        app.setAppName(appInfo[1]);
                        app.setDescription(appInfo[2]);
                        appArrayList.add(app);
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appArrayList;
    }

    /*
     * Get the request history log of a certain app, if appId = null, return all the request logs
     */
    public static ArrayList<RequestLog> getRequestLogs(String token, String appId)
    {
        HttpPost httpPost = new HttpPost(serverAddress);
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("action", "getRequestLogs"));
        pairs.add(new BasicNameValuePair("token", token));
        pairs.add(new BasicNameValuePair("appId", appId));

        UrlEncodedFormEntity urlEncodedFormEntity = null;

        ArrayList<RequestLog> requestLogsList = new ArrayList<RequestLog>();
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(httpResponse.getEntity().getContent());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while((line = bufferedReader.readLine())!=null)
                {
                    String[] logInfo = line.split("\t");
                    if(logInfo.length>=4)
                    {
                        RequestLog requestLog = new RequestLog();
                        requestLog.setId(logInfo[0]);
                        requestLog.setAppName(logInfo[1]);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = format.parse(logInfo[2]);
                            requestLog.setRequestTime(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        requestLog.setRequestPermission(logInfo[3]);
                        if(logInfo.length==5)
                        {
                            requestLog.setRequestReason(logInfo[4]);
                        }
                        requestLogsList.add(requestLog);
                    }
                }
                inputStreamReader.close();
                bufferedReader.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestLogsList;
    }

    /*
     * add/update an app's permission
     * return true if succeed
     *        false if failed
     */
    public static Boolean addApp(String token, App app)
    {
        HttpPost httpPost = new HttpPost(serverAddress);
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("action", "addApp"));
        pairs.add(new BasicNameValuePair("token", token));
        pairs.add(new BasicNameValuePair("appId", app.getId()));
        pairs.add(new BasicNameValuePair("appName",app.getAppName()));
        pairs.add(new BasicNameValuePair("appDescription",app.getDescription()));

        StringBuilder permissionList = new StringBuilder();
        HashMap<String,Boolean> permissionMap = app.getPermissionMap();
        Set<Map.Entry<String, Boolean>> entries = permissionMap.entrySet();
        for(Map.Entry entry : entries)
        {
            permissionList.append(entry.getKey())
                    .append("\t")
                    .append(String.valueOf(entry.getValue()))
                    .append("\n");
        }
        pairs.add(new BasicNameValuePair("permissionList",permissionList.toString()));

        UrlEncodedFormEntity urlEncodedFormEntity = null;

        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                return true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * Find whether a request can be allowed
     * return 0 - not allowed
     *        1 - allowed (server should record log)
     *        2 - need authorization
     */
    public static int appAuthen(String token, Request request)
    {
        HttpPost httpPost = new HttpPost(serverAddress);
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("action", "appAuth"));
        pairs.add(new BasicNameValuePair("token", token));
        pairs.add(new BasicNameValuePair("appId", request.getAppId()));
        pairs.add(new BasicNameValuePair("appName",request.getAppName()));
        pairs.add(new BasicNameValuePair("appDescription",request.getAppDescription()));
        pairs.add(new BasicNameValuePair("requestType",String.valueOf(request.getRequestType())));
        pairs.add(new BasicNameValuePair("requestReason",request.getRequestReason()));

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        String httpResult = "";
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                httpResult = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.valueOf(httpResult);
    }

    /*
     * Authorize some permission to a app in order to allow a request
     * return 0 - incorrect password
     *        1 - succeed (server should record log)
     */
    public static int appAuthorize(String token, String password, Request request)
    {
        HttpPost httpPost = new HttpPost(serverAddress);
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("action", "appAuth"));
        pairs.add(new BasicNameValuePair("token", token));
        pairs.add(new BasicNameValuePair("password",password));
        pairs.add(new BasicNameValuePair("appId", request.getAppId()));
        pairs.add(new BasicNameValuePair("appName",request.getAppName()));
        pairs.add(new BasicNameValuePair("appDescription",request.getAppDescription()));
        pairs.add(new BasicNameValuePair("requestType",String.valueOf(request.getRequestType())));
        pairs.add(new BasicNameValuePair("requestReason",request.getRequestReason()));

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        String httpResult = "";
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                httpResult = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.valueOf(httpResult);
    }
}
