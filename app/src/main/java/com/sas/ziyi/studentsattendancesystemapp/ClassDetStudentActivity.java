package com.sas.ziyi.studentsattendancesystemapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sas.ziyi.studentsattendancesystemapp.entity.AttendanceEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.CheckEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.ClassEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassDetStudentActivity extends AppCompatActivity {
    private TextView titleClass;
    private TextView attendance_checkNum;
    private TextView attendance_checkNumText;
    private TextView listTitle;
    private LinearLayout listLayout;

    private DrawerLayout mDrawerLayout;
    private LinearLayout contentLayout;
    private ScrollView scrollView;



    private String classEntity;
    private String studentInfor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_det_student);

        /**
         * 绑定
         */
        titleClass = (TextView)findViewById(R.id.title_main) ;
        attendance_checkNum = (TextView)findViewById(R.id.text_up) ;
        attendance_checkNumText = (TextView)findViewById(R.id.text_down) ;
        listTitle = (TextView)findViewById(R.id.text_list_title) ;
        listLayout = (LinearLayout) findViewById(R.id.list_layout) ;

        scrollView = (ScrollView)findViewById(R.id.attendance_layout);
        contentLayout = (LinearLayout)findViewById(R.id.content_layout);

        /**
         * toolbar
         */
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * 滑动菜单
         */
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
        }

        /**
         * 获取课程及学生信息
         */
        Intent intent = getIntent();
        classEntity = intent.getStringExtra("classEntity");
        studentInfor = intent.getStringExtra("studentInfor");

        /**
         * 调用方法，发送请求
         */
        Gson gson = new Gson();
        ClassEntity classEnt = gson.fromJson(classEntity,ClassEntity.class);
        getCheckAttendance(classEnt.getClassId(),classEnt.getClassFounderId());
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu_student,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.delete_class:
                Gson gson = new Gson();
                ClassEntity classEnt = gson.fromJson(classEntity,ClassEntity.class);
                deleteClass(classEnt.getClassId());
                break;
        }
        return true;
    }

    /**
     * 退出课程
     */
    public void deleteClass(String classId){

        Map<String,String> tempMap = new HashMap<String,String>();
        tempMap.put("classId",classId);
        tempMap.put("studentInfor",studentInfor);

        Gson gson = new Gson();
        String classId_studentInfor = gson.toJson(tempMap);

        String url = getString(R.string.url_head) + "";
        HttpUtil.sendOKHttpPost(url, "classId_studentInfor", classId_studentInfor, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassDetStudentActivity.this,"服务器抽风了呢！",
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
                             * 返回上一个活动
                             *
                             */
                            Intent intent = new Intent();
                            intent.putExtra("userId",studentInfor);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                        else{
                            Toast.makeText(ClassDetStudentActivity.this,"退出失败！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    /**
     * 向服务器发送请求，查询课程点名信息
     */
    public void getCheckAttendance(String classInfor,String teacherInfor){

        CheckEntity tempCheck = new CheckEntity();
        tempCheck.setClassId(classInfor);
        tempCheck.setTeacherId(teacherInfor);

        Gson gson = new Gson();
        String tempString = gson.toJson(tempCheck);

        Map<String,String> tempMap = new HashMap<String,String>();
        tempMap.put("checkEntity",tempString);
        tempMap.put("studentInfor",studentInfor);

        String url = getString(R.string.url_head) + "/checkcontrol/getcheckstudent";
        HttpUtil.sendOKHttpPost(url, "class_teacher_studentInfor", gson.toJson(tempMap), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassDetStudentActivity.this,"服务器抽风了呢！",
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
                            showInfor(responseText,classEntity);
                        }else {
                            Toast.makeText(ClassDetStudentActivity.this,"服务器抽风了呢！",
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
    public void showInfor(String checkAttendanceInforListStr,String classEntityStr){

        Gson gson = new Gson();
        ClassEntity classEntity = gson.fromJson(classEntityStr,ClassEntity.class);

        List<Map<String,String>> listResult = new ArrayList<Map<String,String>>();
        listResult = gson.fromJson(checkAttendanceInforListStr,new TypeToken<List<Map<String,String>>>(){}.getType());

        titleClass.setText(classEntity.getClassName());



        listTitle.setText("考勤概况");

        /**
         * 清除页面信息
         */
        if(listLayout != null){
            listLayout.removeAllViews();
        }

        int counter = 0;
        for(Map<String,String> tMap : listResult){
            View view = LayoutInflater.from(this).inflate(R.layout.infor_list_item,
                    listLayout,false);

            TextView textView_check_time = (TextView)view.findViewById(R.id.text_time);
            TextView textView_check_kind = (TextView)view.findViewById(R.id.text_kind);
            TextView textView_check_stage = (TextView)view.findViewById(R.id.text_stage);


            CheckEntity checkEntity = new CheckEntity();
            checkEntity = gson.fromJson(tMap.get("check"),CheckEntity.class);
            AttendanceEntity attendanceEntity = new AttendanceEntity();
            attendanceEntity = gson.fromJson(tMap.get("attendance"),AttendanceEntity.class);

            final boolean tempCheckOver = checkEntity.isCheckIsOver();
            final AttendanceEntity tAttendanceEntity = attendanceEntity;
            final CheckEntity tCheckEntity = checkEntity;

            textView_check_time.setText(checkEntity.getCheckTime().toString());
            switch (checkEntity.getCheckKind()){
                case 0:
                    textView_check_kind.setText("随机数字");
                    break;
                case 1:
                    textView_check_kind.setText("二维码");
                    break;
            }
            if(checkEntity.isCheckIsOver()){
                if(attendanceEntity == null){
                    textView_check_stage.setText("未签到");
                }else {
                    textView_check_stage.setText("已签到");
                    counter++;
                }

            }else {
                if(attendanceEntity == null){
                    textView_check_stage.setText("正在进行");
                }else {
                    textView_check_stage.setText("已签到");
                    counter++;
                }
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 判断是否可以进行签到
                     * 可以签到：进入签到界面
                     * 不能签到：进行提示
                     */
                    if(tempCheckOver){
                        Toast.makeText(ClassDetStudentActivity.this,"考勤已经结束！",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        /**
                         * 判断是否已经签到成功
                         */
                        if(tAttendanceEntity == null){
                            /**
                             * 判断考勤种类，进入不同界面
                             * （未实现）
                             */
                            showDialog(studentInfor,tCheckEntity);
                        }else {
                            Toast.makeText(ClassDetStudentActivity.this,"考勤已经完成！",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });

            listLayout.addView(view);

        }

        scrollView.setVisibility(View.VISIBLE);


        attendance_checkNum.setText(counter + "/" + listResult.size());
        attendance_checkNumText.setText("签到次数/考勤次数");

    }


    /**
     * 显示对话框
     */
    public void showDialog(String studentInfor,CheckEntity checkEntity){
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_student_add_attendance,mDrawerLayout,
                false);
        final String tempStudentInfor = studentInfor;
        final CheckEntity tCheckEntity = checkEntity;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("签到");
        builder.setView(view);
        builder.setPositiveButton("签到", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText textView_check_num = (EditText)view.findViewById(R.id.check_num);

                String tempString = textView_check_num.getText().toString();
                if(tempString != null && !tempString.equals("")){
                    AttendanceEntity attendanceEntity = new AttendanceEntity();
                    attendanceEntity.setCheckId(tCheckEntity.getCheckId());
                    attendanceEntity.setAttendanceKind(tCheckEntity.getCheckKind());
                    attendanceEntity.setStudentId(tempStudentInfor);

                    addAttendance(tempString,attendanceEntity);
                }else {
                    Toast.makeText(ClassDetStudentActivity.this,"签到码不能为空！",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * 添加签到
     */
    public void addAttendance(String attendanceNum,AttendanceEntity attendanceEntity){

        Gson gson = new Gson();
        String tempString = gson.toJson(attendanceEntity);

        Map<String,String> tempMap = new HashMap<String,String>();
        tempMap.put("attendanceNum",attendanceNum);
        tempMap.put("attendanceEntity",tempString);

        String url = getString(R.string.url_head) + "/checkcontrol/addAttendance";
        HttpUtil.sendOKHttpPost(url, "attendanceInfor", gson.toJson(tempMap), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassDetStudentActivity.this,"服务器抽风了呢！",
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
                             * 刷新显示
                             *
                             */
                            Gson gson = new Gson();
                            ClassEntity classEnt = gson.fromJson(classEntity,ClassEntity.class);
                            getCheckAttendance(classEnt.getClassId(),classEnt.getClassFounderId());
                        }else {
                            Toast.makeText(ClassDetStudentActivity.this,"服务器抽风了呢！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
