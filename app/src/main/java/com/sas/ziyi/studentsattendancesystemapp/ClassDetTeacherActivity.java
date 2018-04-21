package com.sas.ziyi.studentsattendancesystemapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.sas.ziyi.studentsattendancesystemapp.entity.CheckEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.ClassEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ClassDetTeacherActivity extends AppCompatActivity {

    private TextView titleClass;
    private TextView titleFounder;
    private TextView checkNum;
    private TextView checkNumText;
    private TextView listTitle;
    private LinearLayout listLayout;

    private DrawerLayout mDrawerLayout;
    private LinearLayout contentLayout;
    private ScrollView scrollView;
    private FloatingActionButton floatingActionButton;

    private Spinner spinner;

    /**
     * 存储选择结果
     */
    private String selectedItem ;

    private String teacherInfor;
    private String classesCheckInforList;
    private String classEntity;
    private String studentsNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_det_teacher);

        /**
         * 绑定
         */
        titleClass = (TextView)findViewById(R.id.title_main) ;
        titleFounder = (TextView)findViewById(R.id.title_vice) ;
        checkNum = (TextView)findViewById(R.id.text_up) ;
        checkNumText = (TextView)findViewById(R.id.text_down) ;
        listTitle = (TextView)findViewById(R.id.text_list_title) ;
        listLayout = (LinearLayout) findViewById(R.id.list_layout) ;

        scrollView = (ScrollView)findViewById(R.id.check_layout);
        contentLayout = (LinearLayout)findViewById(R.id.content_layout);

        /**
         * 滑动菜单
         */
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        /**
         * 获取课程及点名信息
         */
        Intent intent = getIntent();
        classesCheckInforList = intent.getStringExtra("classesSimpInforList");
        classEntity = intent.getStringExtra("classEntity");
        studentsNum = intent.getStringExtra("studentsNum");
        teacherInfor = intent.getStringExtra("teacherInfor");

        /**
         * 悬浮按钮
         */
        floatingActionButton = (FloatingActionButton)findViewById(R.id.add_check);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                ClassEntity classEnt = gson.fromJson(classEntity,ClassEntity.class);
                /**
                 * 显示对话框
                 */
                showDialog(teacherInfor,classEnt.getClassId());

            }
        });

        /**
         * 调用方法，发送请求
         */
        Gson gson = new Gson();
        ClassEntity classEnt = gson.fromJson(classEntity,ClassEntity.class);
        getCheck(classEnt.getClassId(),teacherInfor);

    }

    /**
     * 显示对话框
     */
    public void showDialog(String teacherInfor,String classInfor){
        final String tempTeacherInfor = teacherInfor;
        final String tempClassInfor = classInfor;

        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_check,mDrawerLayout,
                false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("开始点名");
        builder.setView(view);


        /**
         * 设置下拉菜单
         */
        spinner = (Spinner)view.findViewById(R.id.choose_check_kind);
        String[] strings = {"随机数字","二维码"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ClassDetTeacherActivity.this,
                R.layout.support_simple_spinner_dropdown_item,strings);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((String)spinner.getSelectedItem()){
                    case "随机数字":
                        selectedItem = "随机数字";
                        break;
                    case "二维码":
                        selectedItem = "二维码";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton("开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addCheck("addCheckInfor",tempTeacherInfor,tempClassInfor,selectedItem);
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
     * 向服务器发送请求，添加点名信息
     */
    public void addCheck(String dataName,String teacherInfor,String classInfo,String checkKind){

        CheckEntity checkEntity = new CheckEntity();
        checkEntity.setTeacherId(teacherInfor);
        checkEntity.setClassId(classInfo);
        switch (checkKind){
            case "随机数字":
                checkEntity.setCheckKind(0);
                break;
            case "二维码":
                checkEntity.setCheckKind(1);
                break;
        }
        checkEntity.setCheckIsOver(false);

        Gson gson = new Gson();
        String tempStr = gson.toJson(checkEntity);

        String url = getString(R.string.url_head) + "/checkcontrol/addcheck";
        HttpUtil.sendOKHttpPost(url, dataName, tempStr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassDetTeacherActivity.this,"服务器抽风了呢！",
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
                        }
                        else{
                            Toast.makeText(ClassDetTeacherActivity.this,"服务器抽风了呢！",
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
    public void getCheck(String classInfor,String teacherInfor){

        CheckEntity tempCheck = new CheckEntity();
        tempCheck.setClassId(classInfor);
        tempCheck.setTeacherId(teacherInfor);

        Gson gson = new Gson();
        String tempString = gson.toJson(tempCheck);

        String url = getString(R.string.url_head) + "/checkcontrol/getcheck";
        HttpUtil.sendOKHttpPost(url, "classTeacherInfor", tempString, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassDetTeacherActivity.this,"服务器抽风了呢！",
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
                            Toast.makeText(ClassDetTeacherActivity.this,"服务器抽风了呢！",
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
    public void showInfor(String classesCheckInforListStr,String classEntityStr){

        Gson gson = new Gson();
        ClassEntity classEntity = gson.fromJson(classEntityStr,ClassEntity.class);
        List<CheckEntity> classesSimpInforList = gson.fromJson(classesCheckInforListStr,new TypeToken<List<CheckEntity>>(){}.getType());

        titleClass.setText(classEntity.getClassName());

        checkNum.setText(classesSimpInforList.size()+"");
        checkNumText.setText("考勤次数");

        listTitle.setText("考勤概况");

        /**
         * 清除页面信息
         */
        if(listLayout != null){
            listLayout.removeAllViews();
        }

        for(CheckEntity check : classesSimpInforList){
            View view = LayoutInflater.from(this).inflate(R.layout.infor_list_item,
                    listLayout,false);

            TextView textView_check_time = (TextView)view.findViewById(R.id.text_time);
            TextView textView_check_kind = (TextView)view.findViewById(R.id.text_kind);
            TextView textView_check_stage = (TextView)view.findViewById(R.id.text_stage);

            final String checkId = check.getCheckId();

            textView_check_time.setText(check.getCheckTime().toString());
            switch (check.getCheckKind()){
                case 0:
                    textView_check_kind.setText("随机数字");
                    break;
                case 1:
                    textView_check_kind.setText("二维码");
                    break;
            }
            if(check.isCheckIsOver()){
                textView_check_stage.setText("点名结束");
            }else {
                textView_check_stage.setText("正在进行");
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClassDetTeacherActivity.this,ClassCheckTeacherActivity.class);
                    intent.putExtra("checkInfor",checkId);
                    startActivity(intent);
                }
            });

            listLayout.addView(view);
            scrollView.setVisibility(View.VISIBLE);
        }

    }
}
