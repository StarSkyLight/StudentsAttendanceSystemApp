package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ClassDetTeacherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_det_teacher);

        /**
         * 获取课程及点名信息
         */
        Intent intent = getIntent();
        final String classesCheckInforList = intent.getStringExtra("classesSimpInforList");
        final String classEntity = intent.getStringExtra("classEntity");
        final String studentsNum = intent.getStringExtra("studentsNum");


    }
}
