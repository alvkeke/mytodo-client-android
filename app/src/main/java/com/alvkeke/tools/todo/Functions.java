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


    /*Time Format Functions*/
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
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        if(format.format(today).equals(formatDate(time))){
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
