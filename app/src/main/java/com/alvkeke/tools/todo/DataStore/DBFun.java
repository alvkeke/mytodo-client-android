package com.alvkeke.tools.todo.DataStore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alvkeke.tools.todo.MainFeatures.Project;
import com.alvkeke.tools.todo.MainFeatures.TaskItem;

import java.util.ArrayList;

public class DBFun {

    /**
     * CREATE TABLE `projects` (id integer primary key, name text, color integer, lastModifyTime integer)
     * CREATE TABLE `tasks` (id integer primary key, proId integer, content text,
     *                      time integer, level integer, isfinished integer, lastModifyTime integer)
     * **/
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
            db.execSQL("create table `projects` (id integer primary key, name text, color integer, lastModifyTime integer)");
        }
        if(!hasTaskTable) {
            db.execSQL("create table `tasks` (id integer primary key, proId integer, content text, time integer, level integer, isfinished integer, lastModifyTime integer)");
        }
        if(!hasTrashProTable){
            db.execSQL("create table `trash_projects` (id integer primary key, name text, color integer, lastModifyTime integer)");
        }
        if(!hasTrashTaskTable){
            db.execSQL("create table `trash_tasks` (id integer primary key, proId integer, content text, time integer, level integer, isfinished integer, lastModifyTime integer)");
        }

        //在以前版本基础上更新数据库
        cursor = db.rawQuery("select sql from sqlite_master where tbl_name='projects' AND type='table'", null);
        cursor.moveToLast();
        String sql_projs = cursor.getString(0);

        if(!sql_projs.contains("lastModifyTime integer")){
            db.execSQL("alter table projects add lastModifyTime integer");
            Log.e("debug", "add lastModifyTime column to projects");
        }

        cursor = db.rawQuery("select sql from sqlite_master where tbl_name='trash_projects' AND type='table'", null);
        cursor.moveToFirst();
        String sql_projs_trash = cursor.getString(0);
        if(!sql_projs_trash.contains("lastModifyTime integer")){
            db.execSQL("alter table trash_projects add lastModifyTime integer");
            Log.e("debug", "add lastModifyTime column to trash_projects");
        }

        cursor = db.rawQuery("select sql from sqlite_master where tbl_name='tasks' AND type='table'", null);
        cursor.moveToFirst();
        String sql_tasks = cursor.getString(0);

        if(!sql_tasks.contains("isfinished integer")){
            db.execSQL("alter table tasks add isfinished integer");
            Log.e("debug", "add isfinished column to tasks");
        }
        if(!sql_tasks.contains("lastModifyTime integer")){
            db.execSQL("alter table tasks add lastModifyTime integer");
            Log.e("debug", "add lastModifyTime column to tasks");
        }

        cursor = db.rawQuery("select sql from sqlite_master where tbl_name='trash_tasks' AND type='table'", null);
        cursor.moveToFirst();
        String sql_tasks_trash = cursor.getString(0);
        if(!sql_tasks_trash.contains("isfinished integer")){
            db.execSQL("alter table trash_tasks add isfinished integer");
            Log.e("debug", "add isfinished column to trash_tasks");
        }
        if(!sql_tasks_trash.contains("lastModifyTime integer")){
            db.execSQL("alter table trash_tasks add lastModifyTime integer");
            Log.e("debug", "add lastModifyTime column to trash_tasks");
        }

        cursor.close();
    }

    public static boolean createProject(SQLiteDatabase db, long proId, String name, int color, long lastModifyTime){
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

        String dbcmd = "insert into projects values(" + proId +",'"+ name +"',"+ color + "," + lastModifyTime + ")";
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean createProject(SQLiteDatabase db, Project project){
        return createProject(db, project.getId(), project.getName(), project.getColor(), project.getLastModifyTime());
    }

    public static boolean createTask(SQLiteDatabase db, long taskId, long proId, String content, long time, int level, long lastModifyTime){
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

        String dbcmd = "insert into tasks values(" + taskId +","+ proId +",'"+ content + "',"+ time +","+ level +", 0, " + lastModifyTime + ")";
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean createTask(SQLiteDatabase db, TaskItem taskItem){
        return createTask(db, taskItem.getId(), taskItem.getProId(), taskItem.getTaskContent(),
                taskItem.getTime(), taskItem.getLevel(), taskItem.getLastModifyTime());
    }

    public static boolean modifyProject(SQLiteDatabase db, long proId, String name, int color, long lastModifyTime){
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

        String dbcmd = "update projects set name='" + name + "', color=" + color + ",lastModifyTime=" + lastModifyTime +" where id=" + proId;
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean modifyTask(SQLiteDatabase db, long taskId, long proId, String content, long time, int level, long lastModifyTime){
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
                +"',time=" + time + ",level=" + level  + ",lastModifyTime=" + lastModifyTime+ " where id=" + taskId;

        db.execSQL(dbcmd);

        return true;

    }

    public static boolean deleteProject(SQLiteDatabase db, long proId, long deleteTime){
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

        String dbcmd = "delete from trash_projects where id=" + proId;
        db.execSQL(dbcmd);
        dbcmd = "insert into trash_projects values(" + proId +",'"+ name +"',"+ color +","+deleteTime + ")";
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
            deleteTask(db, e, deleteTime);
        }

        return true;
    }

    public static boolean deleteTask(SQLiteDatabase db, long taskId, long deleteTime){
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

        cursor = db.rawQuery("select proId, content, time, level, isfinished from tasks where id=" + taskId, null);
        cursor.moveToFirst();
        long proId = cursor.getLong(0);
        String content = cursor.getString(1);
        long time = cursor.getLong(2);
        int level = cursor.getInt(3);
        int isfinished = cursor.getInt(4);
        cursor.close();

        String dbcmd = "delete from trash_tasks where id=" + taskId;
        db.execSQL(dbcmd);
        dbcmd = "insert into trash_tasks values("+taskId+","+proId+",'"+content+"',"+time+","+level+","+isfinished+","+deleteTime+")";
        db.execSQL(dbcmd);
        dbcmd = "delete from tasks where id=" + taskId;
        db.execSQL(dbcmd);

        return true;
    }

    public static boolean setFinishTask(SQLiteDatabase db, long taskId, boolean isFinished, long modifyTime){
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
            return true;
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
            return true;
        }

        int isfinished;
        if(isFinished){
            isfinished = 1;
        }else{
            isfinished = 0;
        }
        String dbcmd = "update tasks set isfinished="+ isfinished +",lastModifyTime=" + modifyTime + " where id="+taskId;
        db.execSQL(dbcmd);

        return false;
    }

    public static void restoreTasks(SQLiteDatabase db, ArrayList<Project> projects){
        Cursor cursor = db.rawQuery("select id, name, color, lastModifyTime from projects", null);
        while (cursor.moveToNext()){
            long proId = cursor.getLong(0);
            String name = cursor.getString(1);
            int color = cursor.getInt(2);
            long lastModifyTime = cursor.getLong(3);
            Project project = new Project(proId, name, color);
            project.setLastModifyTime(lastModifyTime);
            projects.add(project);
        }
        cursor.close();

        for(Project p : projects){
            cursor = db.rawQuery("select proId, id, content, time, level, isfinished, lastModifyTime from tasks", null);
            while(cursor.moveToNext()){
                long proId = cursor.getLong(0);

                if (p.getId() == proId) {

                    long taskId = cursor.getLong(1);
                    String content = cursor.getString(2);
                    long time = cursor.getLong(3);
                    int level = cursor.getInt(4);
                    int isfinished = cursor.getInt(5);
                    long lastModifyTime = cursor.getLong(6);

                    TaskItem taskItem = new TaskItem(proId, taskId, content, time, level);
                    taskItem.setLastModifyTime(lastModifyTime);
                    if(isfinished==1){
                        taskItem.finish();
                    }else {
                        taskItem.unFinish();
                    }
                    p.addTask(taskItem);
                }
            }
        }
        cursor.close();
    }

    public static void mergeDataToDatabase(SQLiteDatabase db, ArrayList<Project> projects){
        for(Project p : projects) {

            Cursor cursor = db.rawQuery("select id, lastModifyTime from 'projects'", null);
            boolean existDataInDatabase = false;

            while (cursor.moveToNext()) {
                long proId = cursor.getLong(0);
                long lastModifyTime = cursor.getLong(1);
                if(p.getId() == proId){
                    existDataInDatabase = true;
                    if(p.getLastModifyTime() > lastModifyTime){
                        modifyProject(db, p.getId(), p.getName(), p.getColor(), p.getLastModifyTime());
                    }
                    break;
                }
            }
            if(!existDataInDatabase){
                createProject(db, p);
            }
            cursor.close();
        }

        for(Project p: projects){
            for(TaskItem  t: p.getTaskList()){
                Cursor cursor = db.rawQuery("select id, lastModifyTime from 'tasks'", null);
                boolean existDataInDatabase = false;

                while (cursor.moveToNext()){
                    long id = cursor.getLong(0);
                    long lastModifyTime = cursor.getLong(1);
                    if(t.getId() == id){
                        existDataInDatabase = true;
                        if(p.getLastModifyTime() > lastModifyTime){
                            modifyTask(db, t.getId(), t.getProId(), t.getTaskContent(), t.getTime(), t.getLevel(), t.getLastModifyTime());
                            break;
                        }
                    }
                }
                if(!existDataInDatabase){
                    createTask(db, t);
                }
                cursor.close();
            }
        }

    }
}
