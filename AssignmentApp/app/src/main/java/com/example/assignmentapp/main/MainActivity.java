package com.example.assignmentapp.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.assignmentapp.R;
import com.example.assignmentapp.datamodel.CustomJSONObject;
import com.example.assignmentapp.views.ObjectCustomAdapter;
import com.example.assignmentapp.views.RecyclerItemClickListener;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mList;
    private ArrayList<CustomJSONObject> mObjectList;
    private TextView mCount;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AssetManager assetManager;
    private String JSONString;
    private int newRequestCode=1;
    private int updateRequestCode=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("sample_json.json");
            int size = inputStream.available();
            byte[] byte_data = new byte[size];
            inputStream.read(byte_data);
            JSONString = new String(byte_data);
        }catch (IOException e){

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(),AdditionActivity.class)
                        .putExtra("viewType","new")
                        .putExtra("JSON", JSONString),newRequestCode);
            }
        });
        mList = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(true);
        mList.setSaveEnabled(true);
        mCount = (TextView) findViewById(R.id.count);
        mList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),mList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    startActivityForResult(new Intent(getApplicationContext(), AdditionActivity.class).putExtra("viewType", "update")
                            .putExtra("object", mObjectList.get(position).getJsonString())
                            .putExtra("position", position).putExtra("JSON", JSONString),updateRequestCode);

            }
        }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mObjectList!=null)
            mCount.setText("No. of Entries-"+mObjectList.size());
        else
            mCount.setText("No. of Entries-0");
        if(mObjectList==null)
            mObjectList = new ArrayList<>();
        if(adapter==null) {
            adapter = new ObjectCustomAdapter(mObjectList);
            mList.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1) {
            int position = data.getIntExtra("position",-1);
            CustomJSONObject object = new CustomJSONObject() ;
            object.setJsonString(data.getStringExtra("MyObject"));
            if (requestCode == newRequestCode) {
                mObjectList.add(object);
            }else if(requestCode == updateRequestCode){
                mObjectList.get(position).setJsonString(object.getJsonString());
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
