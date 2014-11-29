package com.example.fairydream.fbproject_v2;

import java.util.Date;

/**
 * Created by fairydream on 14-11-24.
 */
public class RequestLog
{
    private String _id = null;
    private String appName = null;
    private Date requestTime = null;
    private String requestPermission = null;
    private String requestReason = null;

    public RequestLog(){}

    public RequestLog(String _id, String appName, Date requestTime, String requestPermission, String requestReason)
    {
        this._id = _id;
        this.appName = appName;
        this.requestTime = requestTime;
        this.requestPermission = requestPermission;
        this.requestReason = requestReason;
    }

    public String getId(){ return  _id; }

    public void setId(String _id) { this._id = _id; }

    public String getAppName() { return  appName; }

    public void setAppName(String appName) { this.appName = appName; }

    public Date getRequestTime() { return  requestTime; }

    public void setRequestTime(Date requestTime) { this.requestTime = requestTime; }

    public String getRequestPermission() { return  requestPermission; }

    public void setRequestPermission(String requestPermission) { this.requestPermission = requestPermission; }

    public String getRequestReason() { return requestReason; }

    public void setRequestReason(String requestReason) { this.requestReason = requestReason; }

}
