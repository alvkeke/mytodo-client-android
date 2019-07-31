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

public class ProjectListAdapter extends BaseAdapter {

    private ArrayList<Project> projects;
    private LayoutInflater mInflater;

    public ProjectListAdapter(Context context, ArrayList<Project> projects){
        mInflater = LayoutInflater.from(context);
        this.projects = projects;
    }

    public void changeProjectList(ArrayList<Project> p){
        this.projects = p;
    }

    @Override
    public int getCount() {
        return projects == null?0: projects.size();
    }

    @Override
    public Project getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return projects.get(position).getId();
    }

    public Project findItem(Long id){
        for(Project e : projects){
            if(e.getId() == id){
                return e;
            }
        }
        return null;
    }

    public int getItemPosition(Long id){
        int i = 0;
        for(;i<getCount(); i++){
            if(getItemId(i) == id){
                return i;
            }
        }
        return -1;
    }

    static class ViewHolder{
        ImageView icon;
        TextView proName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.pro_item_layout, null);

            holder.icon = convertView.findViewById(R.id.iv_pro_icon);
            holder.proName = convertView.findViewById(R.id.tv_pro_name);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.proName.setText(projects.get(position).getName());
        holder.icon.setBackgroundColor(projects.get(position).getColor());

        return convertView;
    }
}
