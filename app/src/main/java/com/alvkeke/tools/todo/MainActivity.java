package com.alvkeke.tools.todo;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static com.alvkeke.tools.todo.Constants.*;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView mainStatusBar;
    ImageView drawerStatusBar;
    RelativeLayout toolbarArea;
    Toolbar toolbar;
    TextView tvToolbarTitle;
    ImageView btnAddTask;
    ListView lvTaskList;

    ImageView ivUserIcon;
    TextView tvUsername;
    ListView lvTaskRank;
    ListView lvProject;

    ArrayList<Project> projects;
    ProjectListAdapter proAdapter;
    TaskListAdapter taskAdapter;

    ArrayList<TaskItem> taskList_Show;

    int currentTaskList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.main_toolbar);
        toolbarArea = findViewById(R.id.main_toolbar_area);
        mainStatusBar = findViewById(R.id.main_replace_to_status_bar);
        drawerStatusBar = findViewById(R.id.drawer_replace_to_status);
        tvToolbarTitle = findViewById(R.id.main_toolbar_title);
        btnAddTask = findViewById(R.id.main_btn_add_task);
        lvTaskList = findViewById(R.id.main_task_list);

        ivUserIcon = findViewById(R.id.drawer_user_icon);
        tvUsername = findViewById(R.id.drawer_tx_user_name);
        lvTaskRank = findViewById(R.id.drawer_lv_auto_rank);
        lvProject = findViewById(R.id.drawer_lv_projects);

        Objects.requireNonNull(getSupportActionBar()).hide();

        //设置系统标题栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setStatusBarHeight();

        projects = new ArrayList<>();
        //TODO:delete the code below, these code are for test
        projects.add(new Project(1,"Project1", Color.BLACK));
        projects.add(new Project(2,"Project2", Color.BLACK));

        projects.get(0).addTask(new TaskItem(1, "todo1.1", -1, 0));
        projects.get(0).addTask(new TaskItem(1, "todo1.2", -1, 0));
        projects.get(0).addTask(new TaskItem(1, "todo1.3", -1, 0));
        projects.get(0).addTask(new TaskItem(1, "todo1.4", -1, 0));
        projects.get(0).addTask(new TaskItem(1, "todo1.5", -1, 0));
        projects.get(0).addTask(new TaskItem(1, "todo1.6", -1, 0));

        projects.get(1).addTask(new TaskItem(2, "todo2.1", -1, 0));
        projects.get(1).addTask(new TaskItem(2, "todo2.2", -1, 0));
        projects.get(1).addTask(new TaskItem(2, "todo2.3", -1, 0));
        projects.get(1).addTask(new TaskItem(2, "todo2.4", -1, 0));
        projects.get(1).addTask(new TaskItem(2, "todo2.5", -1, 0));
        projects.get(1).addTask(new TaskItem(2, "todo2.6", -1, 0));


        taskList_Show = Functions.getAllTaskList(projects);
        currentTaskList = TASK_LIST_ALL_TASK;

        DefaultTaskListAdapter defaultProAdapter = new DefaultTaskListAdapter(this);
        lvTaskRank.setAdapter(defaultProAdapter);

        proAdapter = new ProjectListAdapter(this, projects);
        lvProject.setAdapter(proAdapter);

        taskAdapter = new TaskListAdapter(this, taskList_Show);
        lvTaskList.setAdapter(taskAdapter);


        //设置事件响应

        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                taskAdapter.changeTaskList(taskList_Show);
                taskAdapter.notifyDataSetChanged();

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        lvProject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taskList_Show = projects.get(position).getTaskList();
                taskAdapter.changeTaskList(taskList_Show);
                taskAdapter.notifyDataSetChanged();

                currentTaskList = TASK_LIST_USER_PROJECT;
                drawerLayout.closeDrawer(GravityCompat.START);
            }

        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_TASK){
            if(resultCode == RESULT_CODE_ADD_TASK){
                if(data != null) {

                    //TODO: change the defaultValue of the project Id.
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

                    project.addTask(new TaskItem(proId, task, time, level));

                    //新建任务时,刷新当前显示的列表
                    switch (currentTaskList){
                        case TASK_LIST_ALL_TASK:
                            taskList_Show = Functions.getAllTaskList(projects);
                            break;
                        case TASK_LIST_TODAY_TASK:
                            taskList_Show = Functions.getTodayTaskList(projects);
                            break;
                        case TASK_LIST_RECENT_TASK:
                            taskList_Show = Functions.getRecentTaskList(projects);
                            break;
                    }

                    taskAdapter.changeTaskList(taskList_Show);

                    taskAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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

}
