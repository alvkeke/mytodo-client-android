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
    static String formatTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-DD", Locale.CHINA);
        String retDate = simpleDateFormat.format(new Date(time));
        return retDate;
    }


}
