package com.example.assignmentapp.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignmentapp.R;
import com.example.assignmentapp.datamodel.CustomJSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Shashank on 12/15/2016.
 */

public class AdditionActivity extends AppCompatActivity {

    private LinearLayout parentLayout;
    private FloatingActionButton fab_done;
    private ArrayList<View> fieldViews;
    private HashMap<Spinner, ArrayList<String>> SpinnerItemsMap;
    private JSONArray rootJSONArray,priorArray;
    private JSONObject finalJSON;
    private int standardTextSize;
    private int margin;
    private boolean updateStatus;
    private JSONObject priorObject;
    private int position;
    private String extraField;
    private TextView previewText;
    private HashMap<String,String> tempFieldValue;
    private HashMap<String, String> compositeKeyValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_addition);
        if(getIntent().getStringExtra("viewType").equalsIgnoreCase("update")) {
            updateStatus = true;
            try {
                priorObject = new JSONObject(getIntent().getStringExtra("object"));
                Log.e("priorJSON-->",priorObject.toString());
                position = getIntent().getIntExtra("position",-1);
            }catch (JSONException e){
                try {
                    priorArray = new JSONArray(getIntent().getStringExtra("object"));
                    Log.e("priorArray-->",priorArray.toString());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }else {
            updateStatus = false;
            priorObject=null;
            priorArray =null;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        try {
            rootJSONArray = new JSONArray(getIntent().getStringExtra("JSON"));
            Log.e("rootJSON-->",rootJSONArray.toString());
        }catch (JSONException e){

        }

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        fab_done = (FloatingActionButton) findViewById(R.id.fab_done);
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    Intent i = new Intent();
                    i.putExtra("MyObject",finalJSON.toString());
                    if(updateStatus)
                        i.putExtra("position",position);
                    setResult(1,i);
                    Log.e("JSON-->",finalJSON.toString());
                    finish();
                }
            }
        });
        tempFieldValue = new HashMap<>();
        createView();
    }


    private void createView() {
        try {
            standardTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics());
            margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 310, getResources().getDisplayMetrics());
            fieldViews = new ArrayList<>();
            SpinnerItemsMap = new HashMap<>();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            if(updateStatus)
            if(priorObject==null) {
                compositeKeyValue = new HashMap<>();
                for (int i = 0; i < priorArray.length(); i++) {
                    JSONObject feedingObject = priorArray.getJSONObject(i);
                    Iterator x = feedingObject.keys();
                    String key = (String) x.next();
                    compositeKeyValue.put(key, feedingObject.getString(key));
                    Log.e(key,feedingObject.getString(key));
                }
            }
            for (int i = 0; i < rootJSONArray.length(); i++) {
                JSONObject object = rootJSONArray.getJSONObject(i);
                LinearLayout rowLayout = getNewRowLayout();
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
                    params.setMargins(margin, margin, 0, 0);
                    editText.setLayoutParams(params);
                    if(updateStatus){
                        if(priorObject!=null)
                            editText.setText(priorObject.getString(object.getString("field-name")));
                        else
                            editText.setText(compositeKeyValue.get(object.getString("field-name")));
                    }

                    rowLayout.addView(editText);
                    LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ImageView delete = new ImageView(AdditionActivity.this);
                    delete.setImageResource(R.drawable.ic_clear);
                    param3.setMargins(margin, margin, 0, 0);
                    delete.setLayoutParams(param3);
                    delete.setClickable(true);
                    delete.setOnClickListener(handleClearOnClick(editText,object.getString("type")));
                    delete.setBackgroundColor(Color.TRANSPARENT);
                    rowLayout.addView(delete);
                    fieldViews.add(editText);
                    parentLayout.addView(rowLayout);
                } else if (object.getString("type").equalsIgnoreCase("dropdown")) {
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                    if(updateStatus) {
                        Iterator<String> iter = SpinnerOptions.iterator();
                        Log.e("Length-->",""+SpinnerOptions.size());
                        int k=0;
                        for (;iter.hasNext();k++){
                            String temp = iter.next();
                            if(priorObject!=null) {
                                if (temp.equalsIgnoreCase(priorObject.getString(object.getString("field-name")))) {
                                    spinner.setSelection(k);
                                    break;
                                }
                            }else
                                if(temp.equalsIgnoreCase(compositeKeyValue.get(object.getString("field-name")))) {
                                    spinner.setSelection(k);
                                    break;
                                }
                        }
                    }
                    spinner.setLayoutParams(params2);
                    parentLayout.addView(spinner);
                    fieldViews.add(spinner);
                } else if(object.getString("type").equalsIgnoreCase("composite")){
                    TextView textView = new TextView(AdditionActivity.this);
                    textView.setTextSize(standardTextSize);
                    textView.setTextColor(Color.parseColor("#3F51B5"));
                    textView.setText(object.getString("field-name"));
                    params.setMargins(margin, margin, margin, 0);
                    textView.setLayoutParams(params);
                    if(updateStatus){
                        textView.setText("");
                        String tempValue;
                        if(priorObject!=null)
                            tempValue = priorObject.getString(object.getString("field-name"));
                        else
                            tempValue = compositeKeyValue.get(object.getString("field-name"));
                        JSONArray temp = new JSONArray(tempValue);
                        tempFieldValue.put(object.getString("field-name"),temp.toString());
                        JSONArray tempArray = object.getJSONArray("fields");
                        Log.e("TAG-->",""+temp.length());
                        for (int k=0;k<2&&k<temp.length();k++){
                            JSONObject tempObject = temp.getJSONObject(k);
                            JSONObject tempFieldObject = tempArray.getJSONObject(k);
                            textView.setText(textView.getText().toString()+" "+tempObject.getString(tempFieldObject.getString("field-name")));
                        }
                    }
                    rowLayout.addView(textView);
                    textView.setClickable(true);
                    textView.setOnClickListener(handleMoreFieldsOnClick(textView,object.getJSONArray("fields"),object.getString("field-name")));
                    fieldViews.add(textView);
                    LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ImageView delete = new ImageView(AdditionActivity.this);
                    delete.setImageResource(R.drawable.ic_clear);
                    param3.setMargins(margin, margin, 0, 0);
                    delete.setLayoutParams(param3);
                    delete.setClickable(true);
                    delete.setLayoutParams(params);
                    delete.setOnClickListener(handleClearOnClick(textView,object.getString("type")));
                    delete.setBackgroundColor(Color.TRANSPARENT);
                    rowLayout.addView(delete);
                    parentLayout.addView(rowLayout);
                }

            }
        } catch (JSONException e) {
            Log.e("TAGError-->",Log.getStackTraceString(e));
        }
    }

    private static int REQUEST_CODE = 1;
    View.OnClickListener handleMoreFieldsOnClick(final TextView textView, final JSONArray fields, final String fieldName) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdditionActivity.this,AdditionActivity.class);
                intent.putExtra("JSON",fields.toString());
                if(tempFieldValue.containsKey(fieldName)) {
                    Log.e(fieldName,tempFieldValue.get(fieldName));
                    intent.putExtra("viewType","update");
                    intent.putExtra("object",tempFieldValue.get(fieldName));
                }else
                    intent.putExtra("viewType","new");
                extraField = fieldName;
                previewText = textView;
                startActivityForResult(intent,REQUEST_CODE);
            }
        };
    }

    View.OnClickListener handleClearOnClick(final View view,final String type) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if(type.equalsIgnoreCase("composite")){
                    TextView textView = (TextView) view;
                    textView.setText("");
                }else if(type.equalsIgnoreCase("text")|type.equalsIgnoreCase("number")|type.equalsIgnoreCase("multiline")){
                    EditText editText = (EditText) view;
                    editText.setText("");
                }
            }
        };
    }
    private boolean isValid() {
        try {
            for (int i = 0; i < rootJSONArray.length(); i++) {
                JSONObject object = rootJSONArray.getJSONObject(i);
                switch (object.getString("type")) {
                    case "text":
                    case "number":
                    case "multiline":
                        EditText editText = (EditText) fieldViews.get(i);
                        if (object.has("required")) {
                            if (editText.getText().toString().trim().equalsIgnoreCase("") && !object.getString("type").equalsIgnoreCase("number")) {
                                Toast.makeText(getApplicationContext(), "This field is mandatory", Toast.LENGTH_SHORT).show();
                                editText.requestFocus();
                                return false;
                            } else if (object.getString("type").equalsIgnoreCase("number")) {
                                if (isValidValue(object.getInt("min"),object.getInt("max"), editText))
                                    setValues(object.getString("field-name"),editText.getText().toString().trim());
                                else
                                    return false;
                            } else
                                setValues(object.getString("field-name"), editText.getText().toString().trim());
                        } else if (object.getString("type").equalsIgnoreCase("number")) {
                            if (isValidValue(object.getInt("min"),object.getInt("max"), editText))
                                setValues(object.getString("field-name"),editText.getText().toString().trim());
                            else
                                return false;
                        } else
                            setValues(object.getString("field-name"), editText.getText().toString().trim());
                        break;
                    case "dropdown":
                        Spinner spinner = (Spinner) fieldViews.get(i);
                        if (object.has("required")) {
                            if (SpinnerItemsMap.get(fieldViews.get(i)).get(spinner.getSelectedItemPosition()).equalsIgnoreCase("Select")) {
                                Toast.makeText(getApplicationContext(), "This field is mandatory", Toast.LENGTH_SHORT).show();
                                spinner.performClick();
                                return false;
                            } else
                                setValues(object.getString("field-name"), SpinnerItemsMap.get(fieldViews.get(i)).get(spinner.getSelectedItemPosition()));
                        } else
                            setValues(object.getString("field-name"), SpinnerItemsMap.get(fieldViews.get(i)).get(spinner.getSelectedItemPosition()));
                        break;
                    case "composite":
                        TextView textView = (TextView) fieldViews.get(i);
                        if (object.has("required")) {
                            if(textView.getText().toString().trim().equalsIgnoreCase(object.getString("field-name"))){
                                Toast.makeText(getApplicationContext(), "This field is mandatory", Toast.LENGTH_SHORT).show();
                                textView.requestFocus();
                                textView.setTextColor(Color.parseColor("#FF4081"));
                                return false;
                            }else
                                setValues(object.getString("field-name"),tempFieldValue.get(object.getString("field-name")));
                        }else
                            setValues(object.getString("field-name"),tempFieldValue.get(object.getString("field-name")));
                        break;
                }
            }

        }catch (JSONException e){

        }
        return true;
    }

    private void setValues(String field,String value){
        try{
            if(finalJSON == null)
                finalJSON = new JSONObject();
            finalJSON.put(field,value);
        }catch (JSONException e){
            Log.e("JSONerror2-->",Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==1){
            try{
            JSONObject temp= new JSONObject(data.getStringExtra("MyObject"));
            Iterator x = temp.keys();
            JSONArray jsonArray = new JSONArray();
            int count=0;
                previewText.setText("");
                while (x.hasNext()){
                String key = (String) x.next();
                    JSONObject tempObject = new JSONObject();
                    tempObject.put(key,temp.get(key));
                jsonArray.put(tempObject);
                if(count<2){
                    previewText.setText(previewText.getText().toString()+" "+temp.get(key));
                    count++;
                }
            }
                tempFieldValue.put(extraField,jsonArray.toString());
        }catch (JSONException e){

            }
    }}

    private LinearLayout getNewRowLayout(){
        LinearLayout layout = new LinearLayout(AdditionActivity.this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setWeightSum(6f);
        layout.setLayoutParams(params);
        return layout;
    }

    private boolean isValidValue(int min,int max,EditText editText){
        if(max !=-1|min !=-1)
            try{
                if (Integer.parseInt(editText.getText().toString().trim()) < max
                        &&Integer.parseInt(editText.getText().toString().trim())>min) {
                    return true;
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
