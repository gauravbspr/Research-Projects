package com.example.assignmentapp.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.assignmentapp.R;
import com.example.assignmentapp.datamodel.ObjectModel;

import java.util.ArrayList;

/**
 * Created by Shashank on 12/15/2016.
 */

public class ObjectCustomAdapter extends RecyclerView.Adapter<ObjectCustomAdapter.ViewHolder>{

    private ArrayList<ObjectModel> object;

    public ObjectCustomAdapter(ArrayList<ObjectModel> object){
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
        holder.Name.setText(object.get(position).getName());
        holder.age.setText(object.get(position).getAge());
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
