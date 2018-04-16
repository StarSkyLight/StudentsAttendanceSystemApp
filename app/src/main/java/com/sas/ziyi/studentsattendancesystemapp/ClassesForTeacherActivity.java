package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sas.ziyi.studentsattendancesystemapp.entity.ClassEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassesForTeacherActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Button button_classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_teacher);

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

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
        }

        button_classes = (Button)findViewById(R.id.test_classes);
        button_classes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 添加课程
                 */
                ClassEntity classEntity = new ClassEntity();
                classEntity.setClassName("高等数学A1");
                classEntity.setClassFounderId(teacherInfor);
                addClass("classInfor",classEntity);
                /**
                 * 调用方法，向服务器发送请求，获取当前用户的课程。
                 */
                //getTeacherClasses("teacherInfor",teacherInfor);
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
        String url = getString(R.string.url_head) + "/classescontrol/getteacherclasses";
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
                         * （暂未实现）
                         */
                        Log.d("return",responseText);
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
                             * （暂未实现）
                             */
                            Log.d("return",responseText);
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
}
