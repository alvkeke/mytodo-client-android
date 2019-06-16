package com.alvkeke.tools.todo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alvkeke.tools.todo.Common.Constants;
import com.alvkeke.tools.todo.DataStore.DBFun;
import com.alvkeke.tools.todo.MainFeatures.Functions;
import com.alvkeke.tools.todo.MainFeatures.DefaultTaskListAdapter;
import com.alvkeke.tools.todo.MainFeatures.Project;
import com.alvkeke.tools.todo.MainFeatures.ProjectListAdapter;
import com.alvkeke.tools.todo.MainFeatures.TaskItem;
import com.alvkeke.tools.todo.MainFeatures.TaskListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static com.alvkeke.tools.todo.Common.Constants.*;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView mainStatusBar;
    ImageView drawerStatusBar;
    RelativeLayout toolbarArea;
    Toolbar toolbar;
    //TextView tvToolbarTitle;
    ImageView btnAddTask;
    public ListView lvTaskList;

    ImageView ivUserIcon;
    TextView tvUsername;
    ListView lvTaskRank;
    ListView lvProject;
    RelativeLayout btnProjectSetting;
    ImageView btnAddProject;

    ArrayList<Project> projects;
    ProjectListAdapter proAdapter;
    TaskListAdapter taskAdapter;

    ArrayList<TaskItem> taskList_Show;

    SQLiteDatabase db;

    long currentProjectId;
    int currentTaskList;
    boolean proSettingMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.main_toolbar);
        toolbarArea = findViewById(R.id.main_toolbar_area);
        mainStatusBar = findViewById(R.id.main_replace_to_status_bar);
        drawerStatusBar = findViewById(R.id.drawer_replace_to_status);
        //tvToolbarTitle = findViewById(R.id.main_toolbar_title);
        btnAddTask = findViewById(R.id.main_btn_add_task);
        lvTaskList = findViewById(R.id.main_task_list);

        ivUserIcon = findViewById(R.id.drawer_user_icon);
        tvUsername = findViewById(R.id.drawer_tx_user_name);
        lvTaskRank = findViewById(R.id.drawer_lv_auto_rank);
        lvProject = findViewById(R.id.drawer_lv_projects);
        btnProjectSetting = findViewById(R.id.drawer_bottom_area);
        btnAddProject = findViewById(R.id.drawer_btn_add_project);

        Objects.requireNonNull(getSupportActionBar()).hide();

        //设置系统标题栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setStatusBarHeight();

        toolbar.inflateMenu(R.menu.task_list_menu);
        hideTaskMenu();

        //设置初始化设置
        exitProjectSettingMode();

        projects = new ArrayList<>();

        //修改文件夹
        File dir = getExternalFilesDir("local");
        if(!Objects.requireNonNull(dir).exists()) {
            if(!dir.mkdir()){
                Log.e("debug", "mkdir failed.");
            }
        }

        //修改文件名称
        File dbfile = new File(dir, "database.db");
        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        DBFun.initDBFile(db);
/*
        DBFun.createProject(db, 1, "pro1", Color.BLUE);
        DBFun.createProject(db, 2, "pro2", Color.GREEN);
        DBFun.createProject(db, 3, "pro3", Color.YELLOW);

        DBFun.createTask(db, 11, 1, "todo1.1", -1, 0);
        DBFun.createTask(db, 12, 1, "todo1.2", -1, 0);
        DBFun.createTask(db, 13, 1, "todo1.3", -1, 0);
        DBFun.createTask(db, 14, 1, "todo1.4", -1, 0);
        DBFun.createTask(db, 21, 2, "todo2.1", -1, 0);
        DBFun.createTask(db, 22, 2, "todo2.2", -1, 0);
        DBFun.createTask(db, 23, 2, "todo2.3", -1, 0);
        DBFun.createTask(db, 24, 2, "todo2.4", -1, 0);
        DBFun.createTask(db, 31, 3, "todo3.1", -1, 0);
        DBFun.createTask(db, 32, 3, "todo3.2", -1, 0);
        DBFun.createTask(db, 33, 3, "todo3.3", -1, 0);
        DBFun.createTask(db, 34, 3, "todo3.4", -1, 0);
*/
        DBFun.restoreTasks(db, projects);


        //todo:修改为加载本地储存的用户设置
        DefaultTaskListAdapter defaultProAdapter = new DefaultTaskListAdapter(this);
        lvTaskRank.setAdapter(defaultProAdapter);

        proAdapter = new ProjectListAdapter(this, projects);
        lvProject.setAdapter(proAdapter);

        taskAdapter = new TaskListAdapter(this, taskList_Show);
        lvTaskList.setAdapter(taskAdapter);

        //taskList_Show = Functions.getAllTaskList(projects);
        currentTaskList = TASK_LIST_ALL_TASK;
        //tvToolbarTitle.setText("所有");
        flashCurrentTaskList();

        //设置事件响应

        //抽屉页面
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                exitProjectSettingMode();
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:添加用户登录的功能
            }
        });

        lvTaskRank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        taskList_Show = Functions.getAllTaskList(projects);
                        break;
                    case 1:
                        taskList_Show = Functions.getTodayTaskList(projects);
                        break;
                    case 2:
                        taskList_Show = Functions.getRecentTaskList(projects);
                        break;
                }

                currentTaskList = position + 1;
                flashCurrentTaskList();
                taskAdapter.changeTaskList(taskList_Show);
                taskAdapter.notifyDataSetChanged();

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        lvProject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!proSettingMode) {
                    currentProjectId = projects.get(position).getId();
                    taskList_Show = projects.get(position).getTaskList();
                    taskAdapter.changeTaskList(taskList_Show);
                    taskAdapter.notifyDataSetChanged();

                    currentTaskList = TASK_LIST_USER_PROJECT;
                    drawerLayout.closeDrawer(GravityCompat.START);
                    flashCurrentTaskList();
                }else{
                    Intent intentProSetting = new Intent(MainActivity.this, ProjectSettingActivity.class);
                    intentProSetting.putExtra("proId", id);
                    intentProSetting.putExtra("proName", proAdapter.getItem(position).getName());
                    intentProSetting.putExtra("proColor", proAdapter.getItem(position).getColor());

                    startActivityForResult(intentProSetting, Constants.REQUEST_CODE_SETTING_PROJECT);
                }
            }

        });

        btnProjectSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!proSettingMode) {
                    enterProjectSettingMode();
                }else{
                    exitProjectSettingMode();
                }

            }
        });

        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddProj = new Intent(MainActivity.this, ProjectSettingActivity.class);
                startActivityForResult(intentAddProj, REQUEST_CODE_ADD_PROJECT);
            }
        });

        //主界面
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_task_edit:
                        Log.e("debug", "edit");
                        SparseBooleanArray array = lvTaskList.getCheckedItemPositions();
                        int pos = 0;
                        for (; pos < lvTaskList.getCount(); pos++) {
                            if (array.get(pos)) {
                                Log.e("edit task", "position:" + pos);
                                break;
                            }
                        }
                        long proId = taskList_Show.get(pos).getProId();
                        long taskId = taskList_Show.get(pos).getId();
                        String content = taskList_Show.get(pos).getTaskContent();
                        long time = taskList_Show.get(pos).getTime();
                        int level = taskList_Show.get(pos).getLevel();
                        ArrayList<String> projectsInfo = Functions.stringListFromProjectList(projects);
                        Intent intent = new Intent(MainActivity.this, TaskSettingActivity.class);

                        intent.putExtra("proId", proId);
                        intent.putExtra("taskId", taskId);
                        intent.putExtra("content", content);
                        intent.putExtra("time", time);
                        intent.putExtra("level", level);
                        intent.putStringArrayListExtra("projectsInfo", projectsInfo);
                        deselectItem();
                        hideTaskMenu();
                        startActivityForResult(intent, REQUEST_CODE_SETTING_TASK);

                        break;
                    case R.id.menu_task_delete:

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("您确定要删除该任务吗？\n\n此操作不可以回退。")
                                .setNegativeButton(R.string.title_btn_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deselectItem();
                                        hideTaskMenu();
                                    }
                                })
                                .setPositiveButton(R.string.title_btn_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SparseBooleanArray array = lvTaskList.getCheckedItemPositions();
                                        for (int i = 0; i<lvTaskList.getCount(); i++){
                                            if(array.get(i)){
                                                TaskItem taskItem = taskList_Show.get(i);
                                                Project p = Functions.findProjectInProjectList(projects, taskItem.getProId());
                                                if (p != null) {
                                                    p.getTaskList().remove(taskItem);
                                                }

                                                DBFun.deleteTask(db, taskItem.getId());
                                            }
                                        }
                                        flashCurrentTaskList();
                                        deselectItem();
                                        hideTaskMenu();
                                    }
                                });
                        builder.create().show();

                        break;
                    case R.id.menu_task_rank:
                        //todo:完成排列任务的特性
                        break;
                    case R.id.menu_project_setting:
                        //完成打开项目设置的页面
                        Intent intentProSetting = new Intent(MainActivity.this, ProjectSettingActivity.class);
                        intentProSetting.putExtra("proId", currentProjectId);
                        Project currentProject = Functions.findProjectInProjectList(projects, currentProjectId);
                        if(currentProject == null){
                            break;
                        }
                        intentProSetting.putExtra("proName", currentProject.getName());
                        intentProSetting.putExtra("proColor", currentProject.getColor());

                        startActivityForResult(intentProSetting, Constants.REQUEST_CODE_SETTING_PROJECT);

                        break;
                }
                return false;
            }
        });

        lvTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("select", String.valueOf(lvTaskList.getCheckedItemCount()));
                flashMenuItem();
            }
        });

        lvTaskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("debug", "Long Click");
                taskList_Show.get(position).finish();
                flashCurrentTaskList();
                return true;
            }
        });


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                ArrayList<String> projectsInfo = Functions.stringListFromProjectList(projects);
                intent.putStringArrayListExtra("projectsInfo", projectsInfo);
                startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
            }
        });

    }

    void flashCurrentTaskList(){

        switch (currentTaskList){
            case TASK_LIST_ALL_TASK:
                taskList_Show = Functions.getAllTaskList(projects);
                toolbar.setTitle("所有");
                break;
            case TASK_LIST_TODAY_TASK:
                taskList_Show = Functions.getTodayTaskList(projects);
                toolbar.setTitle("今天");
                break;
            case TASK_LIST_RECENT_TASK:
                taskList_Show = Functions.getRecentTaskList(projects);
                toolbar.setTitle("近7天");
                break;
            case TASK_LIST_USER_PROJECT:
                Project project = Functions.findProjectInProjectList(projects, currentProjectId);
                if(project == null){
                    currentTaskList = TASK_LIST_ALL_TASK;
                    flashCurrentTaskList();
                    break;
                }
                taskList_Show = project.getTaskList();
                toolbar.setTitle(project.getName());
                break;
        }

        taskAdapter.changeTaskList(taskList_Show);

        taskAdapter.notifyDataSetChanged();
    }
    void flashMenuItem(){
        if(lvTaskList.getCheckedItemCount() == 0){
            toolbar.getMenu().getItem(0).setVisible(false);
            toolbar.getMenu().getItem(1).setVisible(false);
        }else if(lvTaskList.getCheckedItemCount() == 1){
            toolbar.getMenu().getItem(0).setVisible(true);
            toolbar.getMenu().getItem(1).setVisible(true);
        }else{
            toolbar.getMenu().getItem(0).setVisible(false);
            toolbar.getMenu().getItem(1).setVisible(true);
        }
        taskAdapter.notifyDataSetChanged();
    }
    void hideTaskMenu(){
        toolbar.getMenu().getItem(0).setVisible(false);
        toolbar.getMenu().getItem(1).setVisible(false);
    }
    void deselectItem(){
        for(int i = 0; i<lvTaskList.getCount(); i++){
            lvTaskList.setItemChecked(i, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_TASK){
            if(resultCode == RESULT_CODE_ADD_TASK){
                if(data != null) {

                    long proId = data.getLongExtra("projectId", -1);

                    Project project = Functions.findProjectInProjectList(projects, proId);
                    if(project == null){
                        Toast.makeText(this, "信息出错：找不到项目", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String task = data.getStringExtra("task");
                    int level = data.getIntExtra("level", 0);
                    boolean isRemind = data.getBooleanExtra("isRemind", false);
                    long time = -1;
                    if(isRemind) {
                        int year = data.getIntExtra("year", 1900);
                        int month = data.getIntExtra("month", 0);
                        int dayOfMonth = data.getIntExtra("dayOfMonth", 0);
                        int hour = data.getIntExtra("hour", 0);
                        int minute = data.getIntExtra("minute", 0);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);

                        time = calendar.getTimeInMillis();
                    }

                    //保存到本地，建立一个函数专门储存任务
                    TaskItem taskItem = new TaskItem(proId, Functions.generateId(), task, time, level);
                    project.addTask(taskItem);
                    if(!DBFun.createTask(db, taskItem)){
                        Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
                    }

                    //新建任务时,刷新当前显示的列表
                    flashCurrentTaskList();
                }

            }

        }else if(requestCode == REQUEST_CODE_SETTING_PROJECT){
            switch (resultCode){
                case RESULT_CANCEL:
                    break;
                case RESULT_DELETE_PROJECT:
                    if(data != null){
                        long proId = data.getLongExtra("proId", -1);
                        Project p = proAdapter.findItem(proId);
                        Log.e("delete project", String.valueOf(projects.remove(p)));
                        //todo:从数据库中删除
                        DBFun.deleteProject(db, proId);
                    }
                    exitProjectSettingMode();
                    break;

                    default:
                        if (data != null) {
                            long proId = data.getLongExtra("proId", -1);
                            int proColor = data.getIntExtra("proColor", 0);
                            String proName = data.getStringExtra("proName");

                            Project project = proAdapter.findItem(proId);
                            if (project != null) {
                                project.changeName(proName);
                                if (proColor != 0) {
                                    project.changeColor(proColor);
                                }else{
                                    proColor = project.getColor();
                                }
                                //建立一个函数专门修改项目信息，并保存到本地
                                if(!DBFun.modifyProject(db, proId, proName, proColor)){
                                    Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
            }
            proAdapter.notifyDataSetChanged();
            flashCurrentTaskList();

        }else if (requestCode == REQUEST_CODE_ADD_PROJECT){
            if(resultCode != RESULT_CANCEL) {
                if (data != null) {
                    long id = data.getLongExtra("proId", -1);
                    String proName = data.getStringExtra("proName");
                    int color = data.getIntExtra("proColor", 0);
                    //proAdapter.addProject(new Project(id, proName, color));
                    Project project = new Project(id, proName, color);
                    projects.add(project);
                    //:保存到本地，建立一个函数专门储存项目
                    if(!DBFun.createProject(db, project)) {
                        Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
                    }
                }
                proAdapter.notifyDataSetChanged();
                exitProjectSettingMode();
            }
        }else if (requestCode == REQUEST_CODE_SETTING_TASK){
            if(resultCode == RESULT_CANCEL || data == null){
                return;
            }

            long oldProId = data.getLongExtra("oldProId", -1);
            long newProId = data.getLongExtra("newProId", -1);
            long taskId = data.getLongExtra("taskId", -1);
            Project oldProject = Functions.findProjectInProjectList(projects, oldProId);
            if(oldProject == null){
                Toast.makeText(this, "信息出错:找不到项目", Toast.LENGTH_LONG).show();
                return;
            }
            String task = data.getStringExtra("content");
            int level = data.getIntExtra("level", 0);
            boolean isRemind = data.getBooleanExtra("isRemind", false);
            long time = -1;
            if(isRemind){
                int year = data.getIntExtra("year", 1900);
                int month = data.getIntExtra("month", 0);
                int dayOfMonth = data.getIntExtra("dayOfMonth", 0);
                int hour = data.getIntExtra("hour", 0);
                int minute = data.getIntExtra("minute", 0);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                time = calendar.getTimeInMillis();
            }

            TaskItem taskItem = oldProject.findTask(taskId);
            taskItem.setContent(task);
            taskItem.setLevel(level);
            taskItem.setTime(time);
            if(oldProId != newProId){
                Project newProject = Functions.findProjectInProjectList(projects, newProId);
                if(newProject == null){
                    Toast.makeText(this, "修改项目出错,找不到项目", Toast.LENGTH_LONG).show();
                    return;

                }
                newProject.addTask(taskItem);
                oldProject.getTaskList().remove(taskItem);
                taskItem.setProId(newProId);
            }

            flashCurrentTaskList();

            //存储到本地
            if(!DBFun.modifyTask(db, taskId, newProId, task, time, level)) {
                Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if(lvTaskList.getCheckedItemCount()>0){
            SparseBooleanArray array = lvTaskList.getCheckedItemPositions();
            for (int i = 0; i < array.size(); i++){
                lvTaskList.setItemChecked(i, false);
            }
            hideTaskMenu();
        }else{
            //super.onBackPressed();
            //以下代码模拟Home键按下。
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }

    }

    void setStatusBarHeight(){
        int systemStatusBarHeight = 0;
        int resId = getApplicationContext().getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if(resId > 0){
            systemStatusBarHeight = getApplicationContext().getResources().getDimensionPixelSize(resId);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mainStatusBar.getLayoutParams();
        params.height = systemStatusBarHeight;
        mainStatusBar.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams)drawerStatusBar.getLayoutParams();
        params.height = systemStatusBarHeight;
        drawerStatusBar.setLayoutParams(params);

    }

    void enterProjectSettingMode(){
        proSettingMode = true;
        TextView tvTitle = (TextView)btnProjectSetting.getChildAt(1);
        tvTitle.setText(R.string.label_project_setting_finish);
        btnProjectSetting.setBackgroundColor(0x44000000);
        btnAddProject.setVisibility(View.VISIBLE);
    }

    void exitProjectSettingMode(){
        proSettingMode = false;
        TextView tvTitle = (TextView)btnProjectSetting.getChildAt(1);
        tvTitle.setText(R.string.label_project_setting);
        btnProjectSetting.setBackgroundColor(0x00000000);
        btnAddProject.setVisibility(View.GONE);
    }

}
