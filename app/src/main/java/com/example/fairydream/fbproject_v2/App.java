package com.example.fairydream.fbproject_v2;

import java.util.HashMap;

/**
 * Created by fairydream on 14-11-11.
 */
public class App
{
    private String _id;
    private String name;
    private String description;
    private HashMap<String,Boolean> permissionMap;
    /*
        permissionMap:
            Key: read_sms, read_contact, get_location
            Value: 0 - not grant
            Value: 1 - grant
            Value: 2 - to be decide
     */

    public App()
    {

    }

    public App(String _id, String name)
    {
        this._id = _id;
        this.name = name;
    }

    public App(String _id, String name, HashMap<String, Boolean> permissionMap)
    {
        this._id = _id;
        this.name = name;
        this.permissionMap = permissionMap;
    }

    public String getId()
    {
        return _id;
    }

    public void setId(String _id)
    {
        this._id = _id;
    }

    public String getAppName()
    {
        return name;
    }

    public void setAppName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public HashMap<String,Boolean> getPermissionMap()
    {
        return permissionMap;
    }

    public void setPermissionMap( HashMap<String,Boolean> permissionMap)
    {

        this.permissionMap = permissionMap;
    }
}
