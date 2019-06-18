package com.alvkeke.tools.todo.MainFeatures;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvkeke.tools.todo.R;

import java.util.Calendar;

public class DefaultTaskListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;

    public DefaultTaskListAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        this.context = context;

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

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        switch (position){
            case 0:
                holder.proName.setText("全部任务");
                holder.icon.setImageResource(R.mipmap.ic_all_tasks);
                break;
            case 1:
                holder.proName.setText("今日任务");

                String resIdStr = "ic_rili_" + day;
                int resId = context.getResources().getIdentifier(resIdStr, "mipmap", context.getPackageName());
                holder.icon.setImageResource(resId);

                break;
            case 2:
                holder.proName.setText("近七日任务");
                holder.icon.setImageResource(R.mipmap.ic_rili_next7day);
                break;
        }



        return convertView;
    }
}
