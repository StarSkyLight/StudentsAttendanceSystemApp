package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sas.ziyi.studentsattendancesystemapp.entity.AttendanceEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.CheckEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.StudentEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassCheckTeacherActivity extends AppCompatActivity {

    private String checkId;

    private TextView attendanceNum;
    private TextView attendanceNumText;
    private TextView listTitle;
    private LinearLayout listLayout;

    private DrawerLayout mDrawerLayout;
    private LinearLayout contentLayout;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_check_teacher);

        /**
         * 获取点名信息
         */
        Intent intent = getIntent();
        checkId = intent.getStringExtra("checkInfor");

        /**
         * 绑定
         */
        attendanceNum = (TextView)findViewById(R.id.text_up) ;
        attendanceNumText = (TextView)findViewById(R.id.text_down) ;
        listTitle = (TextView)findViewById(R.id.text_list_title) ;
        listLayout = (LinearLayout) findViewById(R.id.list_layout) ;

        scrollView = (ScrollView)findViewById(R.id.attendance_layout);
        contentLayout = (LinearLayout)findViewById(R.id.content_layout);

        /**
         * 滑动菜单
         */
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        /**
         * 调用方法，发送请求
         */
        getAttendance(checkId);
    }

    /**
     * 向服务器发送请求，查询课程点名信息
     */
    public void getAttendance(String checkInfor){
        String url = getString(R.string.url_head) + "/attendancecontrol/getallattendance";
        HttpUtil.sendOKHttpPost(url, "checkInfor", checkInfor, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassCheckTeacherActivity.this,"服务器抽风了呢！",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseText != null && !responseText.equals("")){
                            /**
                             * 以列表显示
                             *
                             */
                            showInfor(responseText);
                        }else {
                            Toast.makeText(ClassCheckTeacherActivity.this,"服务器抽风了呢！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 操作页面元素，列表显示课程信息和点名概况
     */
    public void showInfor(String attendanceStuInfo){
        Gson gson = new Gson();

        List<Map<String,String>> templist = new ArrayList<Map<String,String>>();
        templist = gson.fromJson(attendanceStuInfo,new TypeToken<List<Map<String,String>>>(){}.getType());

        attendanceNum.setText(templist.size()+"");
        attendanceNumText.setText("学生人数");

        listTitle.setText("考勤详情");


        /**
         * 清除页面信息
         */
        if(listLayout != null){
            listLayout.removeAllViews();
        }

        for(Map<String,String> tempMap : templist){
            View view = LayoutInflater.from(this).inflate(R.layout.infor_list_item,
                    listLayout,false);

            TextView textView_att_name = (TextView)view.findViewById(R.id.text_time);
            TextView textView_att_num = (TextView)view.findViewById(R.id.text_kind);
            TextView textView_att_time = (TextView)view.findViewById(R.id.text_stage);


            AttendanceEntity tempAttendance = new AttendanceEntity();
            StudentEntity tempStudent = new StudentEntity();

            tempAttendance = gson.fromJson(tempMap.get("attendanceEntity"),AttendanceEntity.class);
            tempStudent = gson.fromJson(tempMap.get("studentEntity"),StudentEntity.class);

            textView_att_name.setText(tempStudent.getStudentName());
            textView_att_num.setText(tempStudent.getStudentNumber());
            if(tempAttendance.isAttendanceValid()){
                textView_att_time.setText(tempAttendance.getAttendanceTime().toString());
            }
            else{
                textView_att_time.setText("未签到");
            }

            listLayout.addView(view);
            scrollView.setVisibility(View.VISIBLE);

        }
    }
}
