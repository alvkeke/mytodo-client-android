package com.alvkeke.tools.todo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ProjectSettingActivity extends AppCompatActivity {

    private EditText etProjectName;
    private Button btnOk;
    private Button btnCancel;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_setting);

        etProjectName = findViewById(R.id.pro_setting_et_proName);
        btnOk = findViewById(R.id.pro_setting_btn_ok);
        btnCancel = findViewById(R.id.pro_setting_btn_cancel);

        intent = getIntent();

        String proName = intent.getStringExtra("proName");
        final Long proId = intent.getLongExtra("proId", -1);
        int proColor = intent.getIntExtra("proColor", Color.TRANSPARENT);

        etProjectName.setText(proName);




        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:完成所有设置的返回值
                intent.putExtra("proId", proId);
                intent.putExtra("proName", etProjectName.getText().toString());
//                intent.putExtra("proColor", )
                setResult(Constants.RESULT_CODE_SETTING_PROJECT, intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
