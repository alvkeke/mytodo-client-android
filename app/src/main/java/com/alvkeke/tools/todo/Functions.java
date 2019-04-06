package com.alvkeke.tools.todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class Functions {

    /*Project List Handler Functions*/

    static Project findProjectInProjectList(ArrayList<Project> list, long ProId){

        for (Project e : list){
            if(e.getId() == ProId){
                return e;
            }
        }

        return null;
    }

    /*Task List Handler Functions*/

    ArrayList<TaskItem> getTodayTaskList(ArrayList<Project> allProjects){
        ArrayList<TaskItem> taskItems = new ArrayList<>();

        for(Project p : allProjects){
            for(TaskItem t : p.getTaskList()){
                if(isToday(t.getTime())){
                    taskItems.add(t);
                }
            }
        }

        return taskItems;
    }

    /*Time Format Functions*/

    private static boolean isToday(long time){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return format.format(today).equals(formatDate(time));
    }

    private static boolean isRencent(long time){

        return false;
    }

    private static String formatDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return simpleDateFormat.format(new Date(time));
    }

    private static String formatDate_All(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return simpleDateFormat.format(new Date(time));
    }

    private static String formatTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return simpleDateFormat.format(new Date(time));
    }

    static String autoFormatDate(long time){

        if(isToday(time)){
            //这里判断等于“00:00”是判断有没有设置指定的提醒时间，如果没有，则只显示日期，如果有则连同时间一起显示
            //按照目前这样看，有一个bug
            //如果用户自己设置为00:00,会出现只显示日期的情况,
            //我打算将没有时间只有日期的任务在00:00进行提醒,这就能避免错过提醒的情况发生.
            //TODO:看上面的说明
            if(formatTime(time).equals("00:00")){
                return "今天";
            }else{
                return "今天 " + formatTime(time);
            }
        }

        if(formatTime(time).equals("00:00")){
            return formatDate(time);
        }

        return formatDate_All(time);
    }

}
