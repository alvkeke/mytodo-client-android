package com.alvkeke.tools.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class TaskListAdapter extends BaseAdapter {

    ArrayList<TaskItem> tasks;
    LayoutInflater mInflater;

    TaskListAdapter(ArrayList<TaskItem> e, Context context){
        tasks = e;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).getId();
    }

    public final class ViewHolder{
        ImageView ivImportance;
        TextView tvTaskContent;
        TextView tvTaskTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.task_item_layout, null);

            holder.ivImportance = convertView.findViewById(R.id.taskItem_importance);
            holder.tvTaskContent = convertView.findViewById(R.id.taskItem_taskContent);
            holder.tvTaskTime = convertView.findViewById(R.id.taskItem_taskTime);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTaskContent.setText(tasks.get(position).getTaskContent());

        return convertView;
    }
}
