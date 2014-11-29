package com.example.fairydream.fbproject_v2;


/**
 * Created by fairydream on 14-11-27.
 */
public class Request
{
    private String appId;
    private String appName;
    private String appDescription;
    private int requestType;
    private String requestReason;

    public Request(String appId, String appName, String appDescription, int requestType, String requestReason)
    {
        this.appId = appId;
        this.appName = appName;
        this.appDescription = appDescription;
        this.requestType = requestType;
        this.requestReason = requestReason;
    }


    public String getAppId(){ return appId; }

    public String getAppName() { return  appName; }

    public String getAppDescription() { return appDescription; }

    public int getRequestType() { return requestType; }

    public String getRequestReason() { return requestReason; }

    public void setAppId(String appId) { this.appId = appId; }

    public void setAppName(String appName) { this.appName = appName; }

    public void setAppDescription(String appDescription) { this.appDescription = appDescription; }

    public void setRequestType(int requestType) { this.requestType = requestType; }

    public void setRequestReason(String requestReason) { this.requestReason = requestReason; }
}
