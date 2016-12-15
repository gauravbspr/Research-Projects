package com.example.assignmentapp.datamodel;

/**
 * Created by Shashank on 12/15/2016.
 */

public class FieldObjectModel {

    private String fieldName;
    private boolean fieldRequired;
    private String fieldType;
    private int min, max;

    public void setFieldName(String fieldName){this.fieldName = fieldName;}
    public String getFieldName(){return this.fieldName;}

    public void setFieldRequired(boolean status){this.fieldRequired = status;}
    public boolean getFieldRequired(){return this.fieldRequired;}

    public void setFieldType(String type){this.fieldType = type;}
    public String getFieldType(){return this.fieldType;}

    public void setMin(int min){this.min = min;}
    public int getMin(){return this.min;}

    public void setMax(int max){this.max = max;}
    public int getMax(){return this.max;}
}
