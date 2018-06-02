package com.sas.ziyi.studentsattendancesystemapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sas.ziyi.studentsattendancesystemapp.entity.AttendanceEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.CheckEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.StudentEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.TeacherEntity;
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

    private String selectedItem ;

    private TextView attendanceNum;
    private TextView attendanceNumText;
    private TextView listTitle;
    private LinearLayout listLayout;

    private DrawerLayout mDrawerLayout;
    private LinearLayout contentLayout;
    private ScrollView scrollView;
    private NavigationView navigationView;

    private Spinner spinner;

    private String userNameHeader;
    private String userBasicInfo;

    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_check_teacher);

        /**
         * 获取点名信息
         */
        Intent intent = getIntent();
        checkId = intent.getStringExtra("checkInfor");
        userNameHeader = intent.getStringExtra("userNameHeader");
        userBasicInfo = intent.getStringExtra("userBasicInfo");

        /**
         * 绑定
         */
        attendanceNum = (TextView)findViewById(R.id.text_up) ;
        attendanceNumText = (TextView)findViewById(R.id.text_down) ;
        listTitle = (TextView)findViewById(R.id.text_list_title) ;
        listLayout = (LinearLayout) findViewById(R.id.list_layout) ;

        scrollView = (ScrollView)findViewById(R.id.attendance_layout);
        contentLayout = (LinearLayout)findViewById(R.id.content_layout);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

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
         * 设置滑动菜单中的用户名
         */
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        View viewHead = navigationView.getHeaderView(0);
        TextView userName = (TextView)viewHead.findViewById(R.id.user_name_nav_head) ;
        userName.setText(userNameHeader);

        Gson gson = new Gson();

        TeacherEntity teacherEntity = new TeacherEntity();
        teacherEntity = gson.fromJson(userBasicInfo,TeacherEntity.class);

        Menu viewMenu = navigationView.getMenu();
        MenuItem item_name = (MenuItem)viewMenu.findItem(R.id.nav_name);
        MenuItem item_gender = (MenuItem)viewMenu.findItem(R.id.nav_gender);
        MenuItem item_school = (MenuItem)viewMenu.findItem(R.id.nav_school);
        MenuItem item_email = (MenuItem)viewMenu.findItem(R.id.nav_email);

        item_name.setTitle(item_name.getTitle() + "  " + teacherEntity.getTeacherName());
        if(teacherEntity.isTeacherSex()){
            item_gender.setTitle(item_gender.getTitle() + "  " + "男");
        }else {
            item_gender.setTitle(item_gender.getTitle() + "  " + "女");
        }
        item_school.setTitle(item_school.getTitle() + "  " + teacherEntity.getTeacherSchool());
        item_email.setTitle(item_email.getTitle() + "  " + teacherEntity.getTeacherEmail());

        /**
         * 下拉刷新监听器
         */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAttendance(checkId);
            }
        });

        /**
         * 调用方法，发送请求
         */
        getAttendance(checkId);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu_class_check_teacher,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.stop_check:
                stopCheck(checkId);
                break;
        }
        return true;
    }


    /**
     * 向服务器发送请求，停止考勤
     */
    public void stopCheck(String checkId){
        String url = getString(R.string.url_head) + "/checkcontrol/stopcheck";
        HttpUtil.sendOKHttpPost(url, "checkId", checkId, new Callback() {
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
                            Toast.makeText(ClassCheckTeacherActivity.this,"考勤已停止",
                                    Toast.LENGTH_LONG).show();
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

                            //刷新事件结束
                            swipeRefreshLayout.setRefreshing(false);

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

            final String tempAttendanceId = tempAttendance.getAttendanceId();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangeAttDialog(tempAttendanceId);
                }
            });

            listLayout.addView(view);
        }
        scrollView.setVisibility(View.VISIBLE);
    }


    /**
     * 修改签到状态的对话框
     * @param attendanceId
     */
    public void showChangeAttDialog(String attendanceId){
        final String temmpAttId = attendanceId;

        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_attendance,mDrawerLayout,
                false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改签到状态");
        builder.setView(view);

        /**
         * 设置下拉菜单
         */
        spinner = (Spinner)view.findViewById(R.id.choose_change_atten_kind);
        String[] strings = {"旷课","到课"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ClassCheckTeacherActivity.this,
                R.layout.support_simple_spinner_dropdown_item,strings);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((String)spinner.getSelectedItem()){
                    case "旷课":
                        selectedItem = "旷课";
                        break;
                    case "到课":
                        selectedItem = "到课";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                changeAtten(temmpAttId);

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
     * 向服务器发送数据，修改签到状态
     * @param attendanceId
     */
    public void changeAtten(String attendanceId){
        AttendanceEntity attendanceEntity = new AttendanceEntity();
        switch (selectedItem){
            case "旷课":
                attendanceEntity.setAttendanceValid(false);
                break;
            case "到课":
                attendanceEntity.setAttendanceValid(true);
                break;
        }
        attendanceEntity.setAttendanceId(attendanceId);

        Gson gson = new Gson();

        String url = getString(R.string.url_head) + "/attendancecontrol/changeattendance";
        HttpUtil.sendOKHttpPost(url, "attendanceChanged", gson.toJson(attendanceEntity), new Callback() {
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
                            getAttendance(checkId);
                        }
                        else{
                            Toast.makeText(ClassCheckTeacherActivity.this,"服务器抽风了呢！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}



