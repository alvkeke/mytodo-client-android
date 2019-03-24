package com.alvkeke.tools.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProjectListAdapter extends BaseAdapter {

    LayoutInflater mInflater;

    ProjectListAdapter(Context context){
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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

        switch (position){
            case 0:
                holder.proName.setText("全部任务");
                break;
            case 1:
                holder.proName.setText("今日任务");

                break;
            case 2:
                holder.proName.setText("近七日任务");
                break;
        }

        return convertView;
    }
}
