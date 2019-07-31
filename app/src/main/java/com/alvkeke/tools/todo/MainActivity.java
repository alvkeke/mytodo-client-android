package com.alvkeke.tools.todo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
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
import com.alvkeke.tools.todo.Network.SyncCallback;
import com.alvkeke.tools.todo.Network.Synchronizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import static com.alvkeke.tools.todo.Common.Constants.*;
import static com.alvkeke.tools.todo.Network.Constants.*;

public class MainActivity extends AppCompatActivity implements SyncCallback {

    DrawerLayout drawerLayout;
    ImageView mainStatusBar;
    ImageView drawerStatusBar;
    RelativeLayout toolbarArea;
    Toolbar toolbar;
    ImageView btnAddTask;
    public ListView lvTaskList;
    SwipeRefreshLayout refreshLayout;

    ImageView ivUserIcon;
    TextView tvUsername;
    ListView lvTaskRank;
    ListView lvProject;
    RelativeLayout btnProjectSetting;
    ImageView btnAddProject;

    ArrayList<Project> projects;
    ProjectListAdapter proAdapter;
    DefaultTaskListAdapter defaultProAdapter;
    TaskListAdapter taskAdapter;

    ArrayList<TaskItem> taskList_Show;

    File dbfile;
    SQLiteDatabase db;

    SharedPreferences usersetting;

    long currentProjectId;
    int currentTaskList;
    int sortTaskListWay;
    boolean proSettingMode;
    boolean showFinishedTasks;

    Synchronizer synchronizer;
    String username;
    int netkey;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.main_toolbar);
        toolbarArea = findViewById(R.id.main_toolbar_area);
        mainStatusBar = findViewById(R.id.main_replace_to_status_bar);
        drawerStatusBar = findViewById(R.id.drawer_replace_to_status);
        btnAddTask = findViewById(R.id.main_btn_add_task);
        lvTaskList = findViewById(R.id.main_task_list);
        refreshLayout = findViewById(R.id.swipeRefreshLayout);

        ivUserIcon = findViewById(R.id.drawer_user_icon);
        tvUsername = findViewById(R.id.drawer_tx_user_name);
        lvTaskRank = findViewById(R.id.drawer_lv_auto_rank);
        lvProject = findViewById(R.id.drawer_lv_projects);
        btnProjectSetting = findViewById(R.id.drawer_bottom_area);
        btnAddProject = findViewById(R.id.drawer_btn_add_project);

        Objects.requireNonNull(getSupportActionBar()).hide();

        //设置系统标题栏透明,并不是隐藏
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setStatusBarHeight();

        toolbar.inflateMenu(R.menu.task_list_menu);
        hideTaskMenu();

        //界面初始化设置
        exitProjectSettingMode();
        lvTaskList.setDivider(null);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);

        //为列表框添加适配器
        defaultProAdapter = new DefaultTaskListAdapter(this);
        lvTaskRank.setAdapter(defaultProAdapter);

        projects = new ArrayList<>();
        proAdapter = new ProjectListAdapter(this, projects);
        lvProject.setAdapter(proAdapter);

        taskAdapter = new TaskListAdapter(this, taskList_Show);
        lvTaskList.setAdapter(taskAdapter);

        username = getIntent().getStringExtra("username");
        netkey = getIntent().getIntExtra("netkey", -1);

        usersetting = getSharedPreferences(username, 0);
        currentProjectId = usersetting.getLong("currentProjectId", -1);
        currentTaskList = usersetting.getInt("currentTaskList", TASK_LIST_ALL_TASK);
        sortTaskListWay = usersetting.getInt("sortTaskListWay", SORT_LIST_LEVEL_FIRST);
        showFinishedTasks = usersetting.getBoolean("showFinishedTasks", false);

        //二次修改界面设置
        tvUsername.setText(username);
        toolbar.getMenu().getItem(2).setChecked(showFinishedTasks);
        taskAdapter.showFinishedTasks(showFinishedTasks);

        //加载本地存储的项目已经任务.todo:根据存储的用户信息判断加载的为local文件还是用户个人文件夹
        File dir;
        dir = getExternalFilesDir(username);
        if(!Objects.requireNonNull(dir).exists()){
            if(!dir.mkdir()){
                Log.e("debug", "mkdir failed.");
            }
        }
        dbfile = new File(dir, "database.db");
        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        DBFun.initDBFile(db);
        DBFun.restoreTasks(db, projects);
        db.close();

        //刷新列表界面
        flashCurrentTaskList();

        //设置事件响应

        //抽屉页面
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                deselectItem();
                hideTaskMenu();
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                defaultProAdapter.notifyDataSetChanged();
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

        lvProject.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intentProSetting = new Intent(MainActivity.this, ProjectSettingActivity.class);
                intentProSetting.putExtra("proId", id);
                intentProSetting.putExtra("proName", proAdapter.getItem(position).getName());
                intentProSetting.putExtra("proColor", proAdapter.getItem(position).getColor());

                startActivityForResult(intentProSetting, Constants.REQUEST_CODE_SETTING_PROJECT);
                return true
                        ;
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

                AlertDialog.Builder builder;

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
                        final long taskId = taskList_Show.get(pos).getId();
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

                        builder = new AlertDialog.Builder(MainActivity.this);
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
                                        ArrayList<TaskItem> delArray = new ArrayList<>();
                                        for (int i = 0; i<lvTaskList.getCount(); i++){
                                            if(array.get(i)){
                                                TaskItem taskItem = taskList_Show.get(i);
                                                //deleteTask(taskItem.getId(), taskItem.getProId());
                                                delArray.add(taskItem);
                                            }
                                        }

                                        for (TaskItem e: delArray){
                                            deleteTask(e.getId(), e.getProId());
                                        }

                                        deselectItem();
                                        flashCurrentTaskList();
                                        hideTaskMenu();
                                    }
                                });
                        builder.create().show();

                        break;
                    case R.id.menu_show_all_task:
                        showFinishedTasks = !showFinishedTasks;
                        menuItem.setChecked(showFinishedTasks);
                        SharedPreferences.Editor editor = usersetting.edit();
                        editor.putBoolean("showFinishedTasks", showFinishedTasks);
                        editor.apply();
                        taskAdapter.showFinishedTasks(showFinishedTasks);
                        break;
                    case R.id.menu_task_rank:
                        //排列任务
                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setItems(new String[]{"等级优先", "时间优先"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sortTaskListWay = which;
                                flashCurrentTaskList();
                                SharedPreferences.Editor editor = usersetting.edit();
                                editor.putInt("sortTaskListWay", sortTaskListWay);
                                editor.apply();
                            }
                        });
                        builder.create().show();
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
                flashTaskMenuItem();
                taskAdapter.notifyDataSetChanged();
            }
        });

        lvTaskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TaskItem task = taskList_Show.get(position);

                toggleTaskFinishState(task);
                flashCurrentTaskList();
                return true;
            }
        });


//        synchronizer = new Synchronizer(MainActivity.this, netkey);
//        synchronizer.setAddress(SERVER_IP, SERVER_PORT);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(netkey == -1) {

                    //如果为离线模式,则清空当前项目列表,并从数据库中重新加载项目与任务
                    for(Project p :projects){
                        p.getTaskList().clear();
                    }
                    projects.clear();
                    flashCurrentTaskList();

                    db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
                    DBFun.restoreTasks(db, projects);
                    db.close();
                    flashCurrentTaskList();
                    refreshLayout.setRefreshing(false);
                }else if(netkey > 0){
                    synchronizer = new Synchronizer(MainActivity.this, netkey);
                    synchronizer.setAddress(SERVER_IP, SERVER_PORT);
//                    synchronizer.pushData(projects);
//                    synchronizer.pullData();
                    synchronizer.sync(projects);
                }
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                ArrayList<String> projectsInfo = Functions.stringListFromProjectList(projects);
                intent.putStringArrayListExtra("projectsInfo", projectsInfo);
                intent.putExtra("currentProjectId", currentProjectId);
                startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            Log.e("debug", "return data is null");
            return;
        }
        if(requestCode == REQUEST_CODE_ADD_TASK){
            if(resultCode == RESULT_CODE_ADD_TASK){

                long proId = data.getLongExtra("projectId", -1);
                final String task = data.getStringExtra("task");
                final int level = data.getIntExtra("level", 0);
                boolean isRemind = data.getBooleanExtra("isRemind", false);
                long tmpTime = -1;
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

                    tmpTime = calendar.getTimeInMillis();
                }
                final long time = tmpTime;

                Project project = Functions.findProjectInProjectList(projects, proId);
                if(project == null){
                    if(projects.isEmpty()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("当前无可用项目\n是否自动创建一个项目？")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long proId = Functions.generateId();
                                        createProject(proId, "自动创建", Color.BLACK);
                                        addTask(Functions.generateId(), proId, task, time, level);
                                        flashCurrentTaskList();
                                    }
                                });
                        builder.create().show();
                        return;
                    }else {
                        Toast.makeText(this, "信息出错：找不到项目", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //添加任务
                addTask(Functions.generateId(), proId, task, tmpTime, level);

                //新建任务时,刷新当前显示的列表
                flashCurrentTaskList();

            }

        }else if(requestCode == REQUEST_CODE_SETTING_PROJECT){
            long proId;
            switch (resultCode){
                case RESULT_CANCEL:
                    break;
                case RESULT_DELETE_PROJECT:
                    proId = data.getLongExtra("proId", -1);
                    deleteProject(proId);

//                    exitProjectSettingMode();
                    break;

                default:
                    proId = data.getLongExtra("proId", -1);
                    int proColor = data.getIntExtra("proColor", 0);
                    String proName = data.getStringExtra("proName");

                    modifyProject(proId, proName, proColor);

            }
            flashCurrentTaskList();

        }else if (requestCode == REQUEST_CODE_ADD_PROJECT){
            if(resultCode != RESULT_CANCEL) {
                long id = data.getLongExtra("proId", -1);
                String proName = data.getStringExtra("proName");
                int color = data.getIntExtra("proColor", 0);
                createProject(id, proName, color);

                exitProjectSettingMode();
            }
        }else if (requestCode == REQUEST_CODE_SETTING_TASK){
            if(resultCode == RESULT_CANCEL){
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

            modifyTask(taskId, oldProId, newProId, task, time, level);

            flashCurrentTaskList();


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

    void sortTaskList(){
        if(sortTaskListWay == SORT_LIST_LEVEL_FIRST){
            ArrayList<TaskItem> high = new ArrayList<>();
            ArrayList<TaskItem> mid = new ArrayList<>();
            ArrayList<TaskItem> low = new ArrayList<>();
            ArrayList<TaskItem> none = new ArrayList<>();
            for(TaskItem e : taskList_Show){
                switch (e.getLevel()){
                    case 3:
                        high.add(e);
                        break;
                    case 2:
                        mid.add(e);
                        break;
                    case 1:
                        low.add(e);
                        break;
                    case 0:
                        none.add(e);
                        break;
                }
            }
            selectSort_time(high);
            selectSort_time(mid);
            selectSort_time(low);
            selectSort_time(none);
            taskList_Show.clear();
            taskList_Show.addAll(high);
            taskList_Show.addAll(mid);
            taskList_Show.addAll(low);
            taskList_Show.addAll(none);
        }else if(sortTaskListWay == SORT_LIST_TIME_FIRST){
            selectSort_time(taskList_Show);
        }
    }

    void selectSort_level(ArrayList<TaskItem> arrayList){
        if (arrayList == null || arrayList.isEmpty()){
            return;
        }
        for (int i = 0; i<arrayList.size(); i++){
            int p = i;
            for (int j = i; j<arrayList.size(); j++){
                if(arrayList.get(j).getLevel() > arrayList.get(p).getLevel()){
                    p=j;
                }
            }
            Collections.swap(arrayList, i, p);
        }
    }

    void selectSort_time(ArrayList<TaskItem> arrayList){
        if(arrayList == null || arrayList.isEmpty()){
            return;
        }

        ArrayList<TaskItem> hastime = new ArrayList<>();
        ArrayList<TaskItem> notime = new ArrayList<>();

        for(TaskItem e: arrayList){
            if(e.getTime()<0){
                notime.add(e);
            }else{
                hastime.add(e);
            }
        }

        for(int i = 0; i<hastime.size(); i++){
            int p = i;
            for(int j = i; j<hastime.size(); j++){
                if(hastime.get(j).getTime() < hastime.get(p).getTime()){
                    p = j;
                }
            }
            Collections.swap(hastime, i, p);
        }
        selectSort_level(notime);
        arrayList.clear();
        arrayList.addAll(hastime);
        arrayList.addAll(notime);
    }

    void flashMenuItem(){
        if(currentTaskList == TASK_LIST_USER_PROJECT){
            toolbar.getMenu().getItem(4).setVisible(true);
        }else{
            toolbar.getMenu().getItem(4).setVisible(false);
        }
    }
    void flashTaskMenuItem(){
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
        //taskAdapter.notifyDataSetChanged();
    }
    void hideTaskMenu(){
        toolbar.getMenu().getItem(0).setVisible(false);
        toolbar.getMenu().getItem(1).setVisible(false);
    }
    void deselectItem(){
        for(int i = 0; i<lvTaskList.getCount(); i++){
            lvTaskList.setItemChecked(i, false);
        }
        lvTaskList.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        lvTaskList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
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

    public void addTask(long taskId, long proId, String content, long time, int level) {

        Project project = Functions.findProjectInProjectList(projects, proId);
        if(project == null){
            Toast.makeText(this, "信息出错：找不到项目", Toast.LENGTH_SHORT).show();
            return;
        }

        //保存到本地，建立一个函数专门储存任务
        TaskItem taskItem = new TaskItem(proId, taskId, content, time, level);
//        taskItem.setLastModifyTime(new Date().getTime());
        taskItem.updataLastModifyTime();
        project.addTask(taskItem);

        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        if(!DBFun.createTask(db, taskItem)){
            Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void modifyTask(long taskId, long oldProId, long newProId, String content, long time, int level) {
        Project oldProject = Functions.findProjectInProjectList(projects, oldProId);
        if(oldProject == null){
            Toast.makeText(this, "信息出错:找不到项目", Toast.LENGTH_LONG).show();
            return;
        }

        TaskItem taskItem = oldProject.findTask(taskId);
        taskItem.setContent(content);
        taskItem.setLevel(level);
        taskItem.setTime(time);
//        taskItem.setLastModifyTime(new Date().getTime());
        taskItem.updataLastModifyTime();

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

        //存储到本地

        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        if(!DBFun.modifyTask(db, taskId, newProId, content, time, level, taskItem.getLastModifyTime())) {
            Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void toggleTaskFinishState(TaskItem taskItem) {
        if(!taskItem.isFinished()){
            taskItem.finish();
        }else {
            taskItem.unFinish();
        }
        taskItem.updataLastModifyTime();

        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        if (!DBFun.setFinishTask(db, taskItem.getId(), taskItem.isFinished(), new Date().getTime())){
            Toast.makeText(MainActivity.this, "数据库修改失败", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void deleteTask(long taskId, long proId) {
        Project p = Functions.findProjectInProjectList(projects, proId);
        if (p != null) {
            TaskItem taskItem = p.findTask(taskId);
            if(taskItem != null) {
                p.getTaskList().remove(taskItem);

                db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
                if(!DBFun.deleteTask(db, taskItem.getId(), new Date().getTime())){
                    Toast.makeText(MainActivity.this, "数据库修改失败", Toast.LENGTH_LONG).show();
                }
                db.close();
            }
        }
    }

    public void createProject(long proId, String name, int color) {
        Project project = new Project(proId, name, color);
        project.updataLastModifyTime();
        projects.add(project);

        //保存到本地，建立一个函数专门储存项目
        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        if(!DBFun.createProject(db, project)) {
            Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
        }
        db.close();
        proAdapter.notifyDataSetChanged();
    }

    public void modifyProject(long proId, String name, int color) {
        Project project = proAdapter.findItem(proId);

        if (project != null) {
            project.changeName(name);

            if (color!= 0) {
                project.changeColor(color);
            }else{
                color = project.getColor();
            }
            project.updataLastModifyTime();

            //建立一个函数专门修改项目信息，并保存到本地
            db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            if(!DBFun.modifyProject(db, proId, name, color, new Date().getTime())){
                Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
            }
            db.close();
        }
        proAdapter.notifyDataSetChanged();
    }

    public void deleteProject(long proId) {
        Project p = proAdapter.findItem(proId);
        projects.remove(p);

        //从数据库中删除
        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        if(!DBFun.deleteProject(db, proId, new Date().getTime())){
            Toast.makeText(this, "数据库修改失败", Toast.LENGTH_LONG).show();
        }
        db.close();
        proAdapter.notifyDataSetChanged();
    }

    public void flashCurrentTaskList(){

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
                    return;
                }
                taskList_Show = project.getTaskList();
                toolbar.setTitle(project.getName());
                break;
        }
        SharedPreferences.Editor editor = usersetting.edit();
        editor.putInt("currentTaskList", currentTaskList);
        editor.putLong("currentProjectId", currentProjectId);

        editor.apply();
        flashMenuItem();
        sortTaskList();

        taskAdapter.changeTaskList(taskList_Show);
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public void syncDataFailed(int FailedType) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "同步失败", Toast.LENGTH_SHORT).show();
            }
        });
        refreshLayout.setRefreshing(false);

    }

    @Override
    public void pushDataSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "同步完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void syncDataSuccess(final ArrayList<Project> projects) {
        //todo:完成此方法

        Functions.mergeProjectList(this.projects, projects);

        proAdapter.changeProjectList(this.projects);
        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        DBFun.mergeDataToDatabase(db, projects);
        db.close();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                proAdapter.changeProjectList(projects);
                proAdapter.notifyDataSetChanged();
                flashCurrentTaskList();
            }
        });

        refreshLayout.setRefreshing(false);
    }

}
