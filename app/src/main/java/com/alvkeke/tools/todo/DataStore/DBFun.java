package com.alvkeke.tools.todo.DataStore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alvkeke.tools.todo.MainFeatures.Functions;
import com.alvkeke.tools.todo.MainFeatures.Project;
import com.alvkeke.tools.todo.MainFeatures.TaskItem;

import java.util.ArrayList;

public class DBFun {

    public static void initDBFile(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select name from sqlite_master", null);
        boolean hasProTable = false;
        boolean hasTaskTable = false;
        boolean hasTrashProTable = false;
        boolean hasTrashTaskTable = false;

        while (cursor.moveToNext()){
            if(!hasProTable && cursor.getString(0).equals("projects")){
                hasProTable = true;
            }
            if(!hasTaskTable && cursor.getString(0).equals("tasks")){
                hasTaskTable = true;
            }
            if(!hasTrashProTable && cursor.getString(0).equals("trash_projects")){
                hasTrashProTable = true;
            }
            if(!hasTrashTaskTable && cursor.getString(0).equals("trash_tasks")){
                hasTrashTaskTable = true;
            }

            if(hasProTable && hasTaskTable && hasTrashProTable && hasTrashTaskTable){
                break;
            }
        }
        cursor.close();
        if(!hasProTable) {
            db.execSQL("create table `projects` (id integer primary key, name text, color integer)");
        }
        if(!hasTaskTable) {
            db.execSQL("create table `tasks` (id integer primary key, proId integer, content text, time integer, level integer)");
        }
        if(!hasTrashProTable){
            db.execSQL("create table `trash_projects` (id integer primary key, name text, color integer)");
        }
        if(!hasTrashTaskTable){
            db.execSQL("create table `trash_tasks` (id integer primary key, proId integer, content text, time integer, level integer)");
        }
    }

    public static boolean createProject(SQLiteDatabase db, long proId, String name, int color){
        Cursor cursor = db.rawQuery("select name from sqlite_master", null);
        boolean canContinue = false;
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals("projects")){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        String dbcmd = "insert into projects values(" + proId +",'"+ name +"',"+ color + ")";
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean createProject(SQLiteDatabase db, Project project){
        return createProject(db, project.getId(), project.getName(), project.getColor());
    }

    public static boolean createTask(SQLiteDatabase db, long taskId, long proId, String content, long time, int level){
        Cursor cursor = db.rawQuery("select name from sqlite_master", null);
        boolean canContinue = false;
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals("tasks")){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        String dbcmd = "insert into tasks values(" + taskId +","+ proId +",'"+ content + "',"+ time +","+ level +")";
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean createTask(SQLiteDatabase db, TaskItem taskItem){
        return createTask(db, taskItem.getId(), taskItem.getProId(), taskItem.getTaskContent(), taskItem.getTime(), taskItem.getLevel());
    }

    public static boolean modifyProject(SQLiteDatabase db, long proId, String name, int color){
        Cursor cursor = db.rawQuery("select name from sqlite_master", null);
        boolean canContinue = false;
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals("projects")){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        canContinue = false;
        cursor = db.rawQuery("select id from `projects` order by id", null);
        while (cursor.moveToNext()){
            if(cursor.getLong(0) == proId){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        String dbcmd = "update projects set name='" + name + "', color=" + color +"where id=" + proId;
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean modifyTask(SQLiteDatabase db, long taskId, long proId, String content, long time, int level){
        Cursor cursor = db.rawQuery("select name from sqlite_master", null);
        boolean canContinue = false;
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals("tasks")){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        cursor = db.rawQuery("select id from tasks order by id", null);
        canContinue = false;
        while (cursor.moveToNext()){
            if(cursor.getLong(0) == taskId){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        String dbcmd = "update tasks set proId=" + proId + ",content='" + content
                +"',time=" + time + ",level=" + level + " where id=" + taskId;

        db.execSQL(dbcmd);

        return true;

    }

    public static boolean deleteProject(SQLiteDatabase db, long proId){
        Cursor cursor = db.rawQuery("select name from sqlite_master", null);
        boolean canContinue = false;
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals("projects")){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        canContinue = false;
        cursor = db.rawQuery("select id from `projects` order by id", null);
        while (cursor.moveToNext()){
            if(cursor.getLong(0) == proId){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        cursor = db.rawQuery("select name, color from projects where id=" + proId, null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        int color = cursor.getInt(1);
        cursor.close();

        String dbcmd = "insert into trash_projects values(" + proId +",'"+ name +"',"+ color + ")";
        db.execSQL(dbcmd);
        dbcmd = "delete from projects where id=" + proId;
        db.execSQL(dbcmd);

        cursor = db.rawQuery("select id from tasks where proId=" + proId, null);
        ArrayList<Long> tasks = new ArrayList<>();
        while (cursor.moveToNext()){
            tasks.add(cursor.getLong(0));
        }
        cursor.close();
        for(long e : tasks){
            deleteTask(db, e);
        }

        return true;
    }

    public static boolean deleteTask(SQLiteDatabase db, long taskId){
        Cursor cursor = db.rawQuery("select name from sqlite_master", null);
        boolean canContinue = false;
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals("tasks")){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        cursor = db.rawQuery("select id from tasks order by id", null);
        canContinue = false;
        while (cursor.moveToNext()){
            if(cursor.getLong(0) == taskId){
                canContinue = true;
                break;
            }
        }
        cursor.close();
        if(!canContinue){
            return false;
        }

        cursor = db.rawQuery("select proId, content, time, level from tasks where id=" + taskId, null);
        cursor.moveToFirst();
        long proId = cursor.getLong(0);
        String content = cursor.getString(1);
        long time = cursor.getLong(2);
        int level = cursor.getInt(3);
        cursor.close();

        String dbcmd = "insert into trash_tasks values("+taskId+","+proId+",'"+content+"',"+time+","+level+")";
        db.execSQL(dbcmd);
        dbcmd = "delete from tasks where id=" + taskId;
        db.execSQL(dbcmd);

        return true;
    }

    public static void restoreTasks(SQLiteDatabase db, ArrayList<Project> projects){
        Cursor cursor = db.rawQuery("select id, name, color from projects", null);
        while (cursor.moveToNext()){
            long proId = cursor.getLong(0);
            String name = cursor.getString(1);
            int color = cursor.getInt(2);
            projects.add(new Project(proId, name, color));
        }
        cursor.close();

        cursor = db.rawQuery("select id, proId, content, time, level from tasks", null);
        while (cursor.moveToNext()){
            long taskId = cursor.getLong(0);
            long proId = cursor.getLong(1);
            String content = cursor.getString(2);
            long time = cursor.getLong(3);
            int level = cursor.getInt(4);
            Project p = Functions.findProjectInProjectList(projects, proId);
            if (p != null) {
                p.addTask(new TaskItem(proId, taskId, content, time, level));
            }
        }
        cursor.close();
    }
}
