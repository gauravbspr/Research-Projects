package com.example.assignmentapp.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.assignmentapp.R;
import com.example.assignmentapp.datamodel.CustomJSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Shashank on 12/15/2016.
 */

public class ObjectCustomAdapter extends RecyclerView.Adapter<ObjectCustomAdapter.ViewHolder>{

    private ArrayList<CustomJSONObject> object;

    public ObjectCustomAdapter(ArrayList<CustomJSONObject> object){
        this.object = object;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ObjectCustomAdapter.ViewHolder viewHolder = new ObjectCustomAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject tempObject = new JSONObject(object.get(position).getJsonString());
            Iterator<?> keys = tempObject.keys();
            if(keys.hasNext()) {
                holder.Name.setText(tempObject.getString((String) keys.next()));
            }
            if(keys.hasNext()) {
                holder.age.setText(tempObject.getString((String) keys.next()));
            }
        }catch (JSONException e){

        }
    }

    @Override
    public int getItemCount() {
        return object.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name,age;
        private ViewHolder(View view) {
            super(view);
            Name = (TextView) view.findViewById(R.id.name);
            age = (TextView) view.findViewById(R.id.age);
        }

    }
}
