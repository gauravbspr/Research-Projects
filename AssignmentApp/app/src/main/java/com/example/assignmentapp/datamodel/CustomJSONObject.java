package com.example.assignmentapp.datamodel;

import java.io.Serializable;

/**
 * Created by Shashank on 12/20/2016.
 */

public class CustomJSONObject implements Serializable {

    private String jsonString;

    public void setJsonString(String json){this.jsonString = json;}
    public String getJsonString(){return this.jsonString;}
}
