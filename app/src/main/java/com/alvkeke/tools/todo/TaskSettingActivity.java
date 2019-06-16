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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alvkeke.tools.todo.Common.Constants;
import com.alvkeke.tools.todo.MainFeatures.Functions;
import com.alvkeke.tools.todo.MainFeatures.Project;
import com.alvkeke.tools.todo.MainFeatures.ProjectListAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskSettingActivity extends AppCompatActivity {

    Switch reminderSwitch;
    TextView labelReminder;
    EditText etTaskContent;
    EditText etRemindTime;
    EditText etRemindDate;
    Button btnOk;
    Button btnCancel;
    RelativeLayout reminderSettingArea;
    Spinner projectSelector;
    Spinner levelSelector;

    long proId;
    long taskId;

    Boolean isRemind;
    int year;
    int month;
    int dayOfMonth;

    int hour;
    int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);

        setTitle("任务设置");

        reminderSwitch = findViewById(R.id.addTask_remind_me);
        labelReminder = findViewById(R.id.addTask_label_remind_me);
        etTaskContent = findViewById(R.id.addTask_task_content);
        etRemindDate = findViewById(R.id.addTask_et_remind_date);
        etRemindTime = findViewById(R.id.addTask_et_remind_time);
        btnOk = findViewById(R.id.addTask_btn_ok);
        btnCancel = findViewById(R.id.addTask_btn_cancel);
        reminderSettingArea = findViewById(R.id.addTask_reminder_setting_area);
        projectSelector = findViewById(R.id.addTask_project_select);
        levelSelector = findViewById(R.id.addTask_level_select);

        etRemindTime.setFocusable(false);
        etRemindDate.setFocusable(false);

        final Calendar calender = Calendar.getInstance();

        Intent intent = getIntent();

        proId = intent.getLongExtra("proId", -1);
        taskId = intent.getLongExtra("taskId", -1);
        String content = intent.getStringExtra("content");
        long time = intent.getLongExtra("time", -1);
        int level = intent.getIntExtra("level", 0);
        ArrayList<String> projectsInfo = intent.getStringArrayListExtra("projectsInfo");
        ArrayList<Project> list = Functions.projectListFromStringList(projectsInfo);
        ProjectListAdapter projectListAdapter = new ProjectListAdapter(this, list);
        projectSelector.setAdapter(projectListAdapter);

        //这里用项目列表来代替任务等级的列表,通过参数上的不同来区分这任务等级和项目列表的不同
        ArrayList<Project> levelList = new ArrayList<>();
        levelList.add(new Project(-1,"普通", this.getResources().getColor(R.color.level_none)));
        levelList.add(new Project(-1,"优先", this.getResources().getColor(R.color.level_low)));
        levelList.add(new Project(-1,"重要", this.getResources().getColor(R.color.level_mid)));
        levelList.add(new Project(-1,"紧急", this.getResources().getColor(R.color.level_high)));

        ProjectListAdapter levelListAdapter = new ProjectListAdapter(this, levelList);
        levelSelector.setAdapter(levelListAdapter);

        //设置传入的任务参数
        etTaskContent.setText(content);
        levelSelector.setSelection(level);
        projectSelector.setSelection(projectListAdapter.getItemPosition(proId));

        isRemind = time != -1;
        reminderSwitch.setChecked(isRemind);
        if(isRemind){
            reminderSettingArea.setVisibility(View.VISIBLE);
            etRemindDate.setText(Functions.formatDate(time));
            etRemindTime.setText(Functions.formatTime(time));
        }else{
            reminderSettingArea.setVisibility(View.INVISIBLE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);





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

                if(etRemindTime.getText().toString().isEmpty()){
                    hour = calender.get(Calendar.HOUR_OF_DAY);
                    minute = calender.get(Calendar.MINUTE);
                }

                TimePickerDialog timeDialog = new TimePickerDialog(TaskSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TaskSettingActivity.this.hour = hourOfDay;
                        TaskSettingActivity.this.minute = minute;

                        String timeShow = TaskSettingActivity.this.hour + ":" + TaskSettingActivity.this.minute;
                        etRemindTime.setText(timeShow);
                    }
                }, hour, minute, true);

                timeDialog.show();
            }
        });

        etRemindDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(year==-1){ year = calender.get(Calendar.YEAR); }
                if( month == -1){ month = calender.get(Calendar.MONTH); }
                if( dayOfMonth == -1){ dayOfMonth = calender.get(Calendar.DAY_OF_MONTH); }

                DatePickerDialog dataDialog = new DatePickerDialog(TaskSettingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        TaskSettingActivity.this.year = year;
                        TaskSettingActivity.this.month = month;
                        TaskSettingActivity.this.dayOfMonth = dayOfMonth;

                        String dateShow =
                                TaskSettingActivity.this.year + "-" +
                                        (TaskSettingActivity.this.month+1) + "-" +
                                        TaskSettingActivity.this.dayOfMonth;
                        etRemindDate.setText(dateShow);
                    }
                }, year, month, dayOfMonth);
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

                    intent.putExtra("oldProId", proId);
                    intent.putExtra("newProId", projectSelector.getSelectedItemId());
                    intent.putExtra("taskId", taskId);
                    intent.putExtra("content", etTaskContent.getText().toString());
                    intent.putExtra("isRemind", isRemind);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    intent.putExtra("dayOfMonth", dayOfMonth);
                    intent.putExtra("hour", hour);
                    intent.putExtra("minute", minute);
                    intent.putExtra("level", levelSelector.getSelectedItemPosition());

                    setResult(0, intent);
                    TaskSettingActivity.this.finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Constants.RESULT_CANCEL);
                TaskSettingActivity.this.finish();
            }
        });

    }
}
