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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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
        projects.add(new Project("Project"));
        taskList_Show = projects.get(0).getTaskList();

        DefaultTaskListAdapter defaultProAdapter = new DefaultTaskListAdapter(this);
        lvTaskRank.setAdapter(defaultProAdapter);

        proAdapter = new ProjectListAdapter(this, projects);
        lvProject.setAdapter(proAdapter);

        taskAdapter = new TaskListAdapter(this, taskList_Show);
        lvTaskList.setAdapter(taskAdapter);


        //设置事件响应
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
                startActivityForResult(intent, Constants.REQUEST_CODE_ADD_TASK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_CODE_ADD_TASK){
            if(resultCode == Constants.RESULT_CODE_ADD_TASK){
                if(data != null) {

                    //TODO: change the defaultValue of the project Id.
                    long proId = data.getLongExtra("proId", projects.get(0).getId());

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
                        int month = data.getIntExtra("month", 1);
                        int dayOfMonth = data.getIntExtra("dayOfMonth", 0);
                        int hour = data.getIntExtra("hour", 0);
                        int minute = data.getIntExtra("minute", 0);

                        time = new Date(year-1900, month-1, dayOfMonth, hour, minute).getTime();
                    }

                    projects.get(0).addTask(new TaskItem(proId, task, time, level));
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
