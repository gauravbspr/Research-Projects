package com.example.assignmentapp.main;

import android.content.Intent;
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
import com.example.assignmentapp.database.DatabaseHelper;
import com.example.assignmentapp.datamodel.ObjectModel;
import com.example.assignmentapp.database.ObjectTable;
import com.example.assignmentapp.views.ObjectCustomAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mList;
    private DatabaseHelper db;
    private Cursor mCursor;
    private ArrayList<ObjectModel> mObjectList;
    private TextView mCount;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = DatabaseHelper.getInstance(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AdditionActivity.class));
            }
        });

        mList = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(true);
        mList.setSaveEnabled(true);
        mCount = (TextView) findViewById(R.id.count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCursor = db.getObjects();
        mCount.setText("No. of Entries-"+mCursor.getCount());
        if(mObjectList!=null)
            mObjectList.clear();
        mObjectList = new ArrayList<>();
        while (mCursor.moveToNext()){
            ObjectModel object = new ObjectModel();
            object.setName(mCursor.getString(mCursor.getColumnIndex(ObjectTable.attribute_name)));
            object.setAge(mCursor.getString(mCursor.getColumnIndex(ObjectTable.attribute_age)));
            mObjectList.add(object);
        }
        adapter = new ObjectCustomAdapter(mObjectList);
        mList.setAdapter(adapter);
    }
}
