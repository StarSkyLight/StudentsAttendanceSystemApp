package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ClassCheckTeacherActivity extends AppCompatActivity {

    private String checkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_check_teacher);

        /**
         * 获取点名信息
         */
        Intent intent = getIntent();
        checkId = intent.getStringExtra("checkInfor");

        Toast.makeText(ClassCheckTeacherActivity.this,checkId,
                Toast.LENGTH_SHORT).show();
    }
}
