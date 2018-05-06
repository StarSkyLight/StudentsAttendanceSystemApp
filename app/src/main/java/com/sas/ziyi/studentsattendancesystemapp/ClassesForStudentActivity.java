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
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassesForStudentActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private LinearLayout contentLayout;
    private ScrollView scrollView;
    private FloatingActionButton floatingActionButton;

    private String studentInfor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_for_student);

        scrollView = (ScrollView)findViewById(R.id.class_layout);
        contentLayout = (LinearLayout)findViewById(R.id.content_layout);
        scrollView.setVisibility(View.INVISIBLE);

        /**
         * 获取userid
         */
        Intent intent = getIntent();
        studentInfor = intent.getStringExtra("userInfor");

        /**
         * toolbar
         */
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * 更新课程列表
         */
        getStudentClasses("studentInfor",studentInfor);
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
                /**
                 * 显示对话框
                 */
                showDialog(studentInfor);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    getStudentClasses("studentInfor",studentInfor);
                }
                break;
        }
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
    public void getStudentClasses(String dataName,String userInfor){
        String url = getString(R.string.url_head) + "/classescontrol/getstudentclasssipminfo";
        HttpUtil.sendOKHttpPost(url, dataName, userInfor, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassesForStudentActivity.this,"服务器抽风了呢！",
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
    public void addClass(String dataName,String inviteNum){
        Gson gson = new Gson();

        Map<String,String> tempMap = new HashMap<String,String>();
        tempMap.put("inviteNum",inviteNum);
        tempMap.put("studentId",studentInfor);

        String tempMapStr = gson.toJson(tempMap);

        String url = getString(R.string.url_head) + "/classescontrol/addstudentclasss";
        HttpUtil.sendOKHttpPost(url, dataName, tempMapStr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassesForStudentActivity.this,"服务器抽风了呢！",
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
                            Toast.makeText(ClassesForStudentActivity.this,"无相应课程！",
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
            View view = LayoutInflater.from(this).inflate(R.layout.class_info_student_simple,
                    contentLayout,false);
            TextView textView_class_name = (TextView)view.findViewById(R.id.text_class_name);
            TextView textView_check_num = (TextView)view.findViewById(R.id.text_check_num);

            String tempStirng = tempMap.get("classesSimpInforList");
            List<CheckEntity> classesSimpInforList = gson.fromJson(tempStirng,new TypeToken<List<CheckEntity>>(){}.getType());
            tempStirng = tempMap.get("classEntity");
            final String classes = tempStirng;
            ClassEntity classEntity = gson.fromJson(tempStirng,ClassEntity.class);

            textView_class_name.setText(classEntity.getClassName());
            textView_check_num.setText(classesSimpInforList.size()+"");


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClassesForStudentActivity.this,ClassDetStudentActivity.class);
                    intent.putExtra("classEntity",classes);
                    intent.putExtra("studentInfor",studentInfor);
                    startActivityForResult(intent,1);
                }
            });

            contentLayout.addView(view);

            scrollView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示对话框
     */
    public void showDialog(String studentInfor){
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_student_add_class,mDrawerLayout,
                false);
        final String tempTeacherInfor = studentInfor;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("加入课程");
        builder.setView(view);
        builder.setPositiveButton("加入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText textView_class_inv_num = (EditText)view.findViewById(R.id.class_inv_num);

                String tempString = textView_class_inv_num.getText().toString();
                if(tempString != null && !tempString.equals("")){
                    ClassEntity classEntity = new ClassEntity();
                    classEntity.setClassName(textView_class_inv_num.getText().toString());
                    classEntity.setClassFounderId(tempTeacherInfor);

                    addClass("infor",tempString);
                }else {
                    Toast.makeText(ClassesForStudentActivity.this,"课程邀请码不能为空！",
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
