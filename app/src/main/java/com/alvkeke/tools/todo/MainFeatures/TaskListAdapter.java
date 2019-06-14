package com.alvkeke.tools.todo.MainFeatures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvkeke.tools.todo.R;

import java.util.ArrayList;


public class TaskListAdapter extends BaseAdapter {

    private ArrayList<TaskItem> tasks;
    private LayoutInflater mInflater;
    private Context context;

    public TaskListAdapter(Context context, ArrayList<TaskItem> e){
        tasks = e;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void changeTaskList(ArrayList<TaskItem> e){
        tasks = e;
    }

    @Override
    public int getCount() {
        if(tasks == null){
            return 0;
        }
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

    static class ViewHolder{
        ImageView ivImportance;
        TextView tvTaskContent;
        TextView tvTaskTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.task_item_layout, null);

            holder.ivImportance = convertView.findViewById(R.id.taskItem_importance);
            holder.tvTaskContent = convertView.findViewById(R.id.taskItem_taskContent);
            holder.tvTaskTime = convertView.findViewById(R.id.taskItem_taskTime);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTaskContent.setText(tasks.get(position).getTaskContent());

        int level = tasks.get(position).getLevel();
        switch (level){
            case 1:
                holder.ivImportance.setBackgroundColor(context.getResources().getColor(R.color.level_low));
                break;
            case 2:
                holder.ivImportance.setBackgroundColor(context.getResources().getColor(R.color.level_mid));
                break;
            case 3:
                holder.ivImportance.setBackgroundColor(convertView.getResources().getColor(R.color.level_high));
                break;
                default:
                    holder.ivImportance.setBackgroundColor(context.getResources().getColor(R.color.level_none));
                    break;
        }

        long time = tasks.get(position).getTime();
        if(time > 0) {
            String timeStr = Functions.autoFormatDate(time);
            holder.tvTaskTime.setText(timeStr);
        }else{
            holder.tvTaskTime.setText("");
        }

        return convertView;
    }
}
