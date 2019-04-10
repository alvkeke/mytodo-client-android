package com.alvkeke.tools.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class ProjectSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_setting);

        Intent intent = getIntent();
        ArrayList<String> strings = intent.getStringArrayListExtra("projectsInfo");
        if(!strings.isEmpty()) {
            ArrayList<Project> projects = Functions.projectListFromStringList(strings);
        }


    }
}
