package com.alvkeke.tools.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    Switch reminderSwitch;
    EditText etTaskContent;
    EditText etRemindTime;
    EditText etRemindDate;
    Button btnOk;
    Button btnCancel;
    RelativeLayout reminderSettingArea;
    Spinner projectSelector;
    Spinner levelSelector;

    Boolean isRemind;
    int year;
    int month;
    int dayOfMonth;

    int hour;
    int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        reminderSwitch = findViewById(R.id.addTask_remind_me);
        etTaskContent = findViewById(R.id.addTask_task_content);
        etRemindDate = findViewById(R.id.addTask_et_remind_date);
        etRemindTime = findViewById(R.id.addTask_et_remind_time);
        btnOk = findViewById(R.id.addTask_btn_ok);
        btnCancel = findViewById(R.id.addTask_btn_cancel);
        reminderSettingArea = findViewById(R.id.addTask_reminder_setting_area);
        projectSelector = findViewById(R.id.addTask_project_select);
        levelSelector = findViewById(R.id.addTask_level_select);

        final Calendar calender = Calendar.getInstance();

        isRemind = false;
        year = -1;
        month = -1;
        dayOfMonth = -1;
        hour = 0;
        minute = 0;

        etRemindTime.setFocusable(false);
        etRemindDate.setFocusable(false);


        //TODO: Delete the codes below;
        ArrayList<Project> list = new ArrayList<>();
        Intent intent = getIntent();
        final ArrayList<String> proList = intent.getStringArrayListExtra("projectsInfo");
        for(String s : proList){
            String[] proInfo = s.split(":");
            list.add(new Project(Long.valueOf(proInfo[0]), proInfo[1], Integer.valueOf(proInfo[2])));
        }
        //list.add(new Project("123", this.getResources().getColor(R.color.color_black)));

        final ProjectListAdapter projectListAdapter = new ProjectListAdapter(this, list);
        projectSelector.setAdapter(projectListAdapter);

        //这里用项目列表来代替任务等级的列表,通过参数上的不同来区分这任务等级和项目列表的不同
        ArrayList<Project> levelList = new ArrayList<>();
        levelList.add(new Project("普通", this.getResources().getColor(R.color.level_none)));
        levelList.add(new Project("优先", this.getResources().getColor(R.color.level_low)));
        levelList.add(new Project("重要", this.getResources().getColor(R.color.level_mid)));
        levelList.add(new Project("紧急", this.getResources().getColor(R.color.level_high)));

        ProjectListAdapter levelListAdapter = new ProjectListAdapter(this, levelList);
        levelSelector.setAdapter(levelListAdapter);


        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    reminderSettingArea.setVisibility(View.VISIBLE);
                    isRemind = true;
                }else{
                    reminderSettingArea.setVisibility(View.INVISIBLE);
                    isRemind = false;
                }
            }
        });

        etRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                if(hour == -1){ hour = calender.get(Calendar.HOUR_OF_DAY); }
                if(minute == -1){ minute = calender.get(Calendar.MINUTE); }
                */
                if(etRemindTime.getText().toString().isEmpty()){
                    hour = calender.get(Calendar.HOUR_OF_DAY);
                    minute = calender.get(Calendar.MINUTE);
                }

                TimePickerDialog timeDialog = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        AddTaskActivity.this.hour = hourOfDay;
                        AddTaskActivity.this.minute = minute;

                        String timeShow = AddTaskActivity.this.hour + ":" + AddTaskActivity.this.minute;
                        etRemindTime.setText(timeShow);
                    }
                },
                        hour, minute, true);

                timeDialog.show();
            }
        });

        etRemindDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(year==-1){ year = calender.get(Calendar.YEAR); }
                if( month == -1){ month = calender.get(Calendar.MONTH) +1; }
                if( dayOfMonth == -1){ dayOfMonth = calender.get(Calendar.DAY_OF_MONTH); }

                DatePickerDialog dataDialog = new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        AddTaskActivity.this.year = year;
                        AddTaskActivity.this.month = month+1;
                        AddTaskActivity.this.dayOfMonth = dayOfMonth;

                        String dateShow = AddTaskActivity.this.year + "-" +
                                AddTaskActivity.this.month + "-" + AddTaskActivity.this.dayOfMonth;
                        etRemindDate.setText(dateShow);
                    }
                }, year, month-1, dayOfMonth);
                dataDialog.show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etTaskContent.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "任务内容不能为空", Toast.LENGTH_SHORT).show();
                }else if(etRemindDate.getText().toString().isEmpty() && isRemind){
                    Toast.makeText(getApplicationContext(), "日期不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = getIntent();

                    intent.putExtra("task", etTaskContent.getText().toString());
                    intent.putExtra("isRemind", isRemind);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    intent.putExtra("dayOfMonth", dayOfMonth);
                    intent.putExtra("hour", hour);
                    intent.putExtra("minute", minute);
                    intent.putExtra("level", levelSelector.getSelectedItemPosition());
                    intent.putExtra("projectId", projectSelector.getSelectedItemId());

                    setResult(Constants.RESULT_CODE_ADD_TASK, intent);
                    AddTaskActivity.this.finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTaskActivity.this.finish();
            }
        });

    }
}
