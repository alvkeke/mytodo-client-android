package com.alvkeke.tools.todo.MainFeatures;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Functions {

    /*Project List Handler Functions*/

    public static long generateId(){return new Date().getTime();}

    public static Project findProjectInProjectList(ArrayList<Project> list, long ProId){

        for (Project e : list){
            if(e.getId() == ProId){
                return e;
            }
        }

        return null;
    }

    public static ArrayList<String> stringListFromProjectList(ArrayList<Project> list){
        ArrayList<String> strings = new ArrayList<>();

        for(Project e : list){
            String projectInfo = e.getId() + ":" + e.getName() + ":" + e.getColor();
            strings.add(projectInfo);
        }

        return strings;
    }

    public static ArrayList<Project> projectListFromStringList(final ArrayList<String> list){
        ArrayList<Project> projects = new ArrayList<>();

        if(list != null) {
            for (String s : list) {
                String[] proInfo = s.split(":");
                projects.add(new Project(Long.valueOf(proInfo[0]), proInfo[1], Integer.valueOf(proInfo[2])));
            }
        }

        return projects;
    }

    public static void mergeProjectList(ArrayList<Project> projListTo, ArrayList<Project> projListFrom) {

        for(Project p : projListFrom){
            Project pOld = findProjectInProjectList(projListTo, p.getId());
            if(pOld == null){
                projListTo.add(p);
            }else{
                if(p.getLastModifyTime() > pOld.getLastModifyTime()) {
                    pOld.changeName(p.getName());
                    pOld.changeColor(p.getColor());
                    pOld.setLastModifyTime(p.getLastModifyTime());
                }
            }
        }

    }

    public static void autoMoveTaskToProject(ArrayList<Project> projects, ArrayList<TaskItem> taskItems){
        for(TaskItem t : taskItems){
            for(Project p : projects){
                if(p.getId() == t.getProId()){
                    p.addTask(t);
                    break;
                }
            }
        }
        taskItems.clear();
    }

    /*Task List Handler Functions*/

    public static ArrayList<TaskItem> getAllTaskList(ArrayList<Project> projects){
        ArrayList<TaskItem> taskItems = new ArrayList<>();

        for (Project p : projects){
            taskItems.addAll(p.getTaskList());
        }

        return taskItems;
    }

    public static ArrayList<TaskItem> getTodayTaskList(ArrayList<Project> allProjects){
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

    public static ArrayList<TaskItem> getRecentTaskList(ArrayList<Project> projects){
        ArrayList<TaskItem> taskItems = new ArrayList<>();
        for (Project p: projects){
            for (TaskItem t : p.getTaskList()){
                if(isRecent(t.getTime())){
                    taskItems.add(t);
                }
            }
        }

        return taskItems;
    }

    private static TaskItem findTaskItemInTaskList(ArrayList<TaskItem> taskItems, long taskId){
        for(TaskItem e : taskItems){
            if(e.getId() == taskId){
                return e;
            }
        }
        return null;
    }

    public static void mergeTaskList(ArrayList<TaskItem> tasklistTo, ArrayList<TaskItem> tasklistFrom){
        for(TaskItem t : tasklistFrom){

            TaskItem tOld = findTaskItemInTaskList(tasklistTo, t.getId());
            if(tOld == null){
                tasklistTo.add(t);
            }else{
                if(t.getLastModifyTime() > tOld.getLastModifyTime()){
                    tOld.setProId(t.getProId());
                    tOld.setTime(t.getTime());
                    tOld.setLevel(t.getLevel());
                    tOld.setContent(t.getTaskContent());
                    tOld.setFinished(t.isFinished());
                    tOld.setLastModifyTime(t.getLastModifyTime());
                }
            }

        }
    }

    /*Time Format Functions*/

    public static boolean isToday(long time){
//        Date today = new Date();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//        return format.format(today).equals(formatDate(time));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //清空hour时,必须使用set来进行,否则因为AM/PM的缘故,会出现一些奇怪的问题
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        long today = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long tomorrow = calendar.getTimeInMillis();
        return time >= today && time < tomorrow;
    }

    private static boolean isRecent(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //清空hour时,必须使用set来进行,否则因为AM/PM的缘故,会出现一些奇怪的问题
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        long today = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, 7);
        long lastDay = calendar.getTimeInMillis();

        return time >= today && time < lastDay;

    }

    private static int next_day(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);

        if(time < calendar.getTimeInMillis()) return -1; //out of time

        long beginTime, endTime;
        for(int i = 0; i<7; i++){
            beginTime = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            endTime = calendar.getTimeInMillis();
            if(time >= beginTime && time < endTime){
                return i;
            }
        }

        return 8;   //高于7则说明不在最近（7天）范围内
    }

    public static String formatDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return simpleDateFormat.format(new Date(time));
    }

    private static String formatDate_All(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return simpleDateFormat.format(new Date(time));
    }

    public static String formatTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return simpleDateFormat.format(new Date(time));
    }

    static String autoFormatDate(long time){

        int nextDays = next_day(time);

        if(nextDays == -1){
            return "已过期";
        }else if (nextDays == 0){
            String day = "今天";
            String sTime = formatTime(time);

            if(sTime.equals("00:00")){
                return day;
            }else{
                return day +" "+ sTime;
            }
        }else if (nextDays == 1){
            String day = "明天";
            String sTime = formatTime(time);

            if(sTime.equals("00:00")){
                return day;
            }else{
                return day +" "+ sTime;
            }
        }else if (nextDays == 2){
            String day = "后天";
            String sTime = formatTime(time);

            if(sTime.equals("00:00")){
                return day;
            }else{
                return day +" "+ sTime;
            }
        }else if (nextDays >2 && nextDays<7){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE", Locale.CHINA);
            String day = simpleDateFormat.format(new Date(time));
            String sTime = formatTime(time);

            if(sTime.equals("00:00")){
                return day;
            }else{
                return day +" "+ sTime;
            }

        }else {
            if(formatTime(time).equals("00:00")){
                return formatDate(time);
            }else {
                return formatDate_All(time);
            }
        }

    }

}
