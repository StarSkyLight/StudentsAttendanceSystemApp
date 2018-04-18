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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sas.ziyi.studentsattendancesystemapp.entity.CheckEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.ClassEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassesForTeacherActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private LinearLayout contentLayout;
    private ScrollView scrollView;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_teacher);

        scrollView = (ScrollView)findViewById(R.id.class_layout);
        contentLayout = (LinearLayout)findViewById(R.id.content_layout);
        scrollView.setVisibility(View.INVISIBLE);

        /**
         * 获取userid
         */
        Intent intent = getIntent();
        final String teacherInfor = intent.getStringExtra("userInfor");

        /**
         * toolbar
         */
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * 更新课程列表
         */
        getTeacherClasses("teacherInfor",teacherInfor);
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
         * 悬浮按钮
         */
        floatingActionButton = (FloatingActionButton)findViewById(R.id.add_class);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassEntity classEntity = new ClassEntity();
                /**
                 * 显示对话框
                 */
                showDialog(teacherInfor);

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }


    /**
     * 向服务器发送请求，获取当前用户的课程。
     */
    public void getTeacherClasses(String dataName,String userInfor){
        String url = getString(R.string.url_head) + "/classescontrol/getteacherclasssipminfo";
        HttpUtil.sendOKHttpPost(url, dataName, userInfor, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassesForTeacherActivity.this,"服务器抽风了呢！",
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
                        /**
                         * 以列表显示
                         *
                         */
                        showClassesInfo(responseText);
                    }
                });
            }
        });
    }

    /**
     * 向服务器发送请求，添加课程
     */
    public void addClass(String dataName,ClassEntity classEntity){
        Gson gson = new Gson();
        String json_class = gson.toJson(classEntity);

        String url = getString(R.string.url_head) + "/classescontrol/addclass";
        HttpUtil.sendOKHttpPost(url, dataName, json_class, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassesForTeacherActivity.this,"服务器抽风了呢！",
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
                            showClassesInfo(responseText);
                        }
                        else{
                            Toast.makeText(ClassesForTeacherActivity.this,"服务器抽风了呢！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 操作UI元素，显示所有课程简略信息
     */
    public void showClassesInfo(String checkInfo){
        Log.d("return",checkInfo);
        String tempJson = checkInfo;
        Gson gson = new Gson();
        List<Map<String,String>> infoList = new ArrayList<Map<String,String>>();

        infoList = gson.fromJson(tempJson,new TypeToken<List<Map<String,String>>>(){}.getType());

        /**
         * 清除页面信息
         */
        if(contentLayout != null){
            contentLayout.removeAllViews();
        }
        /**
         * 遍历整个list，将对应数据显示在界面上
         */
        for(Map<String,String> tempMap :infoList ){
            View view = LayoutInflater.from(this).inflate(R.layout.class_infor_teacher_simple,
                    contentLayout,false);
            TextView textView_class_name = (TextView)view.findViewById(R.id.text_class_name);
            TextView textView_students_num = (TextView)view.findViewById(R.id.text_students_num);
            TextView textView_check_num = (TextView)view.findViewById(R.id.text_check_num);

            String tempStirng = tempMap.get("classesSimpInforList");
            List<CheckEntity> classesSimpInforList = gson.fromJson(tempStirng,new TypeToken<List<CheckEntity>>(){}.getType());
            tempStirng = tempMap.get("classEntity");
            ClassEntity classEntity = gson.fromJson(tempStirng,ClassEntity.class);
            tempStirng = tempMap.get("studentsNum");
            List<String> studentsNum = gson.fromJson(tempStirng,new TypeToken<List<String>>(){}.getType());

            textView_class_name.setText(classEntity.getClassName());
            textView_students_num.setText(studentsNum.size()+"");
            textView_check_num.setText(classesSimpInforList.size()+"");

            contentLayout.addView(view);

            scrollView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示对话框
     */
    public void showDialog(String teacherInfor){
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog,mDrawerLayout,
                false);
        final String tempString = teacherInfor;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加课程");
        builder.setView(view);
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView textView_class_name = view.findViewById(R.id.class_name);

                String tempString = textView_class_name.getText().toString();
                if(tempString != null && !tempString.equals("")){
                    ClassEntity classEntity = new ClassEntity();
                    classEntity.setClassName(textView_class_name.getText().toString());
                    classEntity.setClassFounderId(tempString);

                    addClass("classInfor",classEntity);
                }else {
                    Toast.makeText(ClassesForTeacherActivity.this,"课程名不能为空！",
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
}
