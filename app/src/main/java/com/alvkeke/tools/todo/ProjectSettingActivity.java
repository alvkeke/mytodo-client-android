package com.alvkeke.tools.todo;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alvkeke.tools.todo.Constants.*;
import com.alvkeke.tools.todo.components.ColorSelector;

public class ProjectSettingActivity extends AppCompatActivity {

    private EditText etProjectName;
    private ColorSelector colorSelector;
    private Button btnOk;
    private Button btnCancel;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_setting);

        etProjectName = findViewById(R.id.pro_setting_et_proName);
        colorSelector = findViewById(R.id.pro_setting_color_selector);
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
                if(!etProjectName.getText().toString().isEmpty()) {
                    intent.putExtra("proId", proId);
                    intent.putExtra("proName", etProjectName.getText().toString());
                    intent.putExtra("proColor", colorSelector.getColor());

                    setResult(0, intent);

                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "项目名称不能为空", Toast.LENGTH_LONG).show();
                }
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
