package com.alvkeke.tools.todo.DataStore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.alvkeke.tools.todo.MainFeatures.Project;
import com.alvkeke.tools.todo.MainFeatures.TaskItem;

import java.util.ArrayList;

public class F {

    public static boolean createProject(SQLiteDatabase db, long proId, String name, int color){

        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals(String.valueOf(proId))){
                Log.e("create project", "project id exist");
                return false;
            }
        }
        cursor.close();

        String dbcmd = "create table `" + proId +
                "`(id integer primary key, content text, time integer, level integer)";
        db.execSQL(dbcmd);
        //第一条记录存放项目信息，排列为 0,名称,null,颜色
        dbcmd = "insert into `" + proId + "` values(0, '" + name + "', 0," + color + ")";
        db.execSQL(dbcmd);

        return true;
    }
    public static boolean createProject(SQLiteDatabase db, Project project){
        return createProject(db, project.getId(), project.getName(), project.getColor());
    }

    public static boolean createTask(SQLiteDatabase db, long proId, long taskId, String content, long time, int level){

        boolean isProjectExist = false;
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals(String.valueOf(proId))){
                isProjectExist = true;
                break;
            }
        }
        cursor.close();
        if(!isProjectExist){
            Log.e("create task", "project doesn't exist");
            return false;
        }

        cursor = db.rawQuery("select id from `" + proId + "` order by id", null);
        while (cursor.moveToNext()){
            if(cursor.getLong(0) == taskId){
                Log.e("create task", "task id exist");
                return false;
            }
        }
        cursor.close();

        String dbcmd = "insert into `" + proId + "` values(" + taskId + ", '" + content + "', " + time + "," + level +")";
        db.execSQL(dbcmd);

        return true;
    }
    public static boolean createTask(SQLiteDatabase db, TaskItem task){
        return createTask(db, task.getProId(), task.getId(), task.getTaskContent(), task.getTime(), task.getLevel());
    }

    public static boolean modifyProject(SQLiteDatabase db, long proId, String name, int color){

        boolean isProjectExist = false;
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals(String.valueOf(proId))){
                isProjectExist = true;
                break;
            }
        }
        cursor.close();
        if(!isProjectExist){
            Log.e("update project", "project doesn't exist");
            return false;
        }

        String dbcmd = "update `" + proId + "` set content='" + name + "', level=" + color + " where id=0";
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean modifyTask(SQLiteDatabase db, long proId, long taskId, String content, long time, int level){

        boolean isExist = false;
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals(String.valueOf(proId))){
                isExist = true;
                break;
            }
        }
        cursor.close();
        if(!isExist){
            Log.e("modify task", "project doesn't exist");
            return false;
        }

        isExist = false;
        cursor = db.rawQuery("select id from `" + proId + "` order by id", null);
        while(cursor.moveToNext()){
            if(cursor.getLong(0) == taskId){
                isExist = true;
                break;
            }
        }
        cursor.close();
        if(!isExist){
            Log.e("modify task", "task doesn't exist");
            return false;
        }

        String dbcmd = "update `" + proId + "` set content='" + content + "', time=" + time + ",level="+ level + " where id="+taskId;
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean restoreProject(SQLiteDatabase db, ArrayList<Project> projects){

        ArrayList<Long> proIds = new ArrayList<>();

        Cursor cursor = db.rawQuery("select name from sqlite_master order by name", null);
        while (cursor.moveToNext()){
            String sId = cursor.getString(0);
            if(sId.equals("android_metadata")){
                continue;
            }
            proIds.add(Long.valueOf(sId));
        }
        cursor.close();

        long taskId;
        String content;
        long time;
        int level;

        for(Long proId : proIds){
            cursor = db.rawQuery("select id, content, time, level from `" + proId + "` order by id", null);
            cursor.moveToFirst();

            content = cursor.getString(1);
            level = cursor.getInt(3);

            Project p = new Project(proId, content, level);

            while (cursor.moveToNext()){

                taskId = cursor.getLong(0);
                content = cursor.getString(1);
                time = cursor.getLong(2);
                level = cursor.getInt(3);

                p.addTask(new TaskItem(proId, taskId, content, time, level));
            }
            projects.add(p);
        }

        cursor.close();

        return true;
    }



}
