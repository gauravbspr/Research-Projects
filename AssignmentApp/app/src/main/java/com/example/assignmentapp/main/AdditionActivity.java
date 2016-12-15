package com.example.assignmentapp.main;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignmentapp.R;
import com.example.assignmentapp.database.DatabaseHelper;
import com.example.assignmentapp.database.ObjectTable;
import com.example.assignmentapp.datamodel.FieldObjectModel;
import com.example.assignmentapp.datamodel.ObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shashank on 12/15/2016.
 */

public class AdditionActivity extends AppCompatActivity {

    private LinearLayout parentLayout;
    private FloatingActionButton fab_done;
    private ObjectModel object;
    private DatabaseHelper db;
    private AssetManager assetManager;
    private FieldObjectModel[] fieldObjects;
    private ArrayList<View> fieldViews;
    private HashMap<Spinner, ArrayList<String>> SpinnerItemsMap;
    private JSONArray rootJSONArray;
    private int standardTextSize;
    private int margin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_addition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        db = DatabaseHelper.getInstance(getApplicationContext());
        assetManager = getAssets();

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        fab_done = (FloatingActionButton) findViewById(R.id.fab_done);
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    db.InsertObjectEntry(object);
                    try{
                        JSONObject jsonObject = new JSONObject();
                        if(!object.getName().equalsIgnoreCase(""))
                            jsonObject.put(ObjectTable.attribute_name,object.getName());
                        if(!object.getAge().equalsIgnoreCase(""))
                            jsonObject.put(ObjectTable.attribute_age,object.getAge());
                        if(!object.getGender().equalsIgnoreCase("Select"))
                            jsonObject.put(ObjectTable.attribute_gender,object.getGender());
                        if(!object.getAddress().equalsIgnoreCase(""))
                            jsonObject.put(ObjectTable.attribute_address,object.getAddress());
                        Log.e("JSON-->",jsonObject.toString());
                    }catch (JSONException e){

                    }
                    finish();
                }
            }
        });
        object = new ObjectModel();
        createView();
    }


    private void createView() {
        try {
            standardTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics());
            margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            InputStream inputStream = assetManager.open("sample_json.json");
            int size = inputStream.available();
            byte[] byte_data = new byte[size];
            inputStream.read(byte_data);
            rootJSONArray = new JSONArray(new String(byte_data));
            fieldObjects = new FieldObjectModel[rootJSONArray.length()];
            fieldViews = new ArrayList<>();
            SpinnerItemsMap = new HashMap<>();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < rootJSONArray.length(); i++) {
                fieldObjects[i] = new FieldObjectModel();
                JSONObject object = rootJSONArray.getJSONObject(i);

                fieldObjects[i].setFieldName(object.getString("field-name"));
                fieldObjects[i].setFieldType(object.getString("type"));
                if (object.has("required"))
                    fieldObjects[i].setFieldRequired(object.getBoolean("required"));
                else
                    fieldObjects[i].setFieldRequired(false);

                if (object.has("min"))
                    fieldObjects[i].setMin(object.getInt("min"));
                else
                    fieldObjects[i].setMin(-1);

                if (object.has("max"))
                    fieldObjects[i].setMax(object.getInt("max"));
                else
                    fieldObjects[i].setMax(-1);

                if (object.getString("type").equalsIgnoreCase("text") | object.getString("type").equalsIgnoreCase("number")
                        | object.getString("type").equalsIgnoreCase("multiline")) {
                    EditText editText = new EditText(AdditionActivity.this);
                    editText.setTextSize(standardTextSize);
                    editText.setTextColor(Color.parseColor("#FF4081"));
                    editText.setHint(object.getString("field-name"));
                    if (object.getString("type").equalsIgnoreCase("number")){
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                    if (object.getString("type").equalsIgnoreCase("multiline")) {
                        editText.setSingleLine(false);
                        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        editText.setLines(2);
                        editText.setMaxLines(3);
                        editText.setVerticalScrollBarEnabled(true);
                        editText.setMovementMethod(ScrollingMovementMethod.getInstance());
                        editText.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    } else {
                        editText.setSingleLine(true);
                        editText.setMaxLines(1);
                        editText.setLines(1);
                    }
                    params.setMargins(margin, margin, margin, 0);
                    editText.setLayoutParams(params);
                    parentLayout.addView(editText);
                    fieldViews.add(editText);
                } else if (object.getString("type").equalsIgnoreCase("dropdown")) {
                    TextView textView = new TextView(AdditionActivity.this);
                    textView.setTextSize(standardTextSize);
                    textView.setTextColor(Color.parseColor("#3F51B5"));
                    textView.setText(object.getString("field-name"));
                    params.setMargins(margin, margin, margin, 0);
                    textView.setLayoutParams(params);
                    parentLayout.addView(textView);
                    JSONArray optionArray = object.getJSONArray("options");
                    ArrayList<String> SpinnerOptions = new ArrayList<String>();
                    SpinnerOptions.add("Select");
                    for (int j = 0; j < optionArray.length(); j++) {
                        String optionString = optionArray.getString(j);
                        SpinnerOptions.add(optionString);
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = null;
                    spinnerArrayAdapter = new ArrayAdapter<String>(AdditionActivity.this, R.layout.spinner_item, SpinnerOptions);
                    Spinner spinner = new Spinner(AdditionActivity.this);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setSelection(0, false);
                    SpinnerItemsMap.put(spinner, SpinnerOptions);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }

                    });
                    spinner.setLayoutParams(params);
                    parentLayout.addView(spinner);
                    fieldViews.add(spinner);
                }
            }
        } catch (IOException e) {

        } catch (JSONException e) {

        }
    }

    private boolean isValid() {
        for (int i = 0; i < fieldObjects.length; i++) {
            switch (fieldObjects[i].getFieldType()){
               case "text":
               case "number":
               case "multiline":
                   EditText editText = (EditText) fieldViews.get(i);
                   if(fieldObjects[i].getFieldRequired()) {
                       if (editText.getText().toString().trim().equalsIgnoreCase("")&&!fieldObjects[i].getFieldType().equalsIgnoreCase("number")) {
                           Toast.makeText(getApplicationContext(), "This field is mandatory", Toast.LENGTH_SHORT).show();
                           editText.requestFocus();
                            return false;
                       }else if(fieldObjects[i].getFieldType().equalsIgnoreCase("number")){
                               if (isValidValue(fieldObjects[i],editText))
                                   setValues(fieldObjects[i].getFieldName(),editText.getText().toString().trim());
                                else
                                   return false;
                       }else
                           setValues(fieldObjects[i].getFieldName(),editText.getText().toString().trim());
                   }else if(fieldObjects[i].getFieldType().equalsIgnoreCase("number")){
                       if (isValidValue(fieldObjects[i],editText))
                           setValues(fieldObjects[i].getFieldName(),editText.getText().toString().trim());
                       else
                           return false;
                   }else
                       setValues(fieldObjects[i].getFieldName(),editText.getText().toString().trim());
                   break;
               case "dropdown":
                    Spinner spinner = (Spinner) fieldViews.get(i);
                   if(fieldObjects[i].getFieldRequired()) {
                       if(SpinnerItemsMap.get(fieldViews.get(i)).get(spinner.getSelectedItemPosition()).equalsIgnoreCase("Select")){
                           Toast.makeText(getApplicationContext(), "This field is mandatory", Toast.LENGTH_SHORT).show();
                           spinner.performClick();
                           return false;
                       }else
                           setValues(fieldObjects[i].getFieldName(),SpinnerItemsMap.get(fieldViews.get(i)).get(spinner.getSelectedItemPosition()));
                   }else
                       setValues(fieldObjects[i].getFieldName(),SpinnerItemsMap.get(fieldViews.get(i)).get(spinner.getSelectedItemPosition()));
                   break;
           }
        }
        return true;
    }

    private void setValues(String field,String value){
        switch (field){
            case ObjectTable.attribute_name:
                object.setName(value);
                break;
            case ObjectTable.attribute_age:
                object.setAge(value);
                break;
            case ObjectTable.attribute_address:
                object.setAddress(value);
                break;
            case ObjectTable.attribute_gender:
                object.setGender(value);
                break;
        }
    }

    private boolean isValidValue(FieldObjectModel object,EditText editText){
        if(object.getMax() !=-1|object.getMin() !=-1)
            try{
                if (Integer.parseInt(editText.getText().toString().trim()) < object.getMax()
                        &&Integer.parseInt(editText.getText().toString().trim())>object.getMin()) {
                    setValues(object.getFieldName(),editText.getText().toString().trim());
                }else{
                    Toast.makeText(getApplicationContext(), "Value out of bound", Toast.LENGTH_SHORT).show();
                    editText.requestFocus();
                    return false;
                }
            }catch (NumberFormatException e){
                if(editText.getText().toString().trim().equalsIgnoreCase(""))
                    Toast.makeText(getApplicationContext(), "Field is mandatory", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Invalid value", Toast.LENGTH_SHORT).show();
                editText.requestFocus();
               return false;
            }
        return true;
    }
}
