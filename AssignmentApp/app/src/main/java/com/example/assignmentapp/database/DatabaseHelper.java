package com.example.assignmentapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.assignmentapp.datamodel.ObjectModel;

/**
 * Created by Shashank on 12/15/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String databaseName = "TestDB";
    private static DatabaseHelper mInstance = null;
    private static int VERSION = 1;
    private Context context;


    private DatabaseHelper(Context context) {
        super(context, databaseName, null, VERSION);
        this.context = context;
    }

    public static DatabaseHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx);
        }
        return mInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String object_table = "CREATE TABLE IF NOT EXISTS   " + ObjectTable.table_name + " ("+ ObjectTable.attribute_name+" TEXT , "+ ObjectTable.attribute_age+" TEXT, "+
                ObjectTable.attribute_gender+" TEXT, " +ObjectTable.attribute_address+" TEXT)";
        db.execSQL(object_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void InsertObjectEntry(ObjectModel object){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ObjectTable.attribute_name,object.getName());
        values.put(ObjectTable.attribute_age,object.getAge());
        values.put(ObjectTable.attribute_gender,object.getGender());
        values.put(ObjectTable.attribute_address,object.getAddress());
        db.insert(ObjectTable.table_name, null, values);
    }

    public Cursor getObjects(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ ObjectTable.table_name,null);
        return cursor;
    }
}
