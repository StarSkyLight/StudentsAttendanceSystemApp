package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sas.ziyi.studentsattendancesystemapp.entity.AttendanceEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.CheckEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.ClassEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.StudentEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

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
    private NavigationView navigationView;


    private String classEntity;
    private String studentInfor;
    private CheckEntity checkEntForQR;

    private String userNameHeader;
    private String userBasicInfo;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_det_student);

        /**
         * 获取课程及学生信息
         */
        Intent intent = getIntent();
        classEntity = intent.getStringExtra("classEntity");
        studentInfor = intent.getStringExtra("studentInfor");
        userNameHeader = intent.getStringExtra("userNameHeader");
        userBasicInfo = intent.getStringExtra("userBasicInfo");

        /**
         * 扫码初始化
         */
        ZXingLibrary.initDisplayOpinion(this);

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
         * 设置滑动菜单中的用户名
         */
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        View viewHead = navigationView.getHeaderView(0);
        TextView userName = (TextView)viewHead.findViewById(R.id.user_name_nav_head) ;
        userName.setText(userNameHeader);

        Gson gson = new Gson();

        StudentEntity studentEntity = new StudentEntity();
        studentEntity = gson.fromJson(userBasicInfo,StudentEntity.class);

        Menu viewMenu = navigationView.getMenu();
        MenuItem item_name = (MenuItem)viewMenu.findItem(R.id.nav_name);
        MenuItem item_gender = (MenuItem)viewMenu.findItem(R.id.nav_gender);
        MenuItem item_school = (MenuItem)viewMenu.findItem(R.id.nav_school);
        MenuItem item_number = (MenuItem)viewMenu.findItem(R.id.nav_number);
        MenuItem item_email = (MenuItem)viewMenu.findItem(R.id.nav_email);

        item_name.setTitle(item_name.getTitle() + "  " + studentEntity.getStudentName());
        if(studentEntity.isStudentSex()){
            item_gender.setTitle(item_gender.getTitle() + "  " + "男");
        }else {
            item_gender.setTitle(item_gender.getTitle() + "  " + "女");
        }
        item_school.setTitle(item_school.getTitle() + "  " + studentEntity.getStudentSchool());
        item_number.setTitle(item_number.getTitle() + "  " + studentEntity.getStudentNumber());
        item_email.setTitle(item_email.getTitle() + "  " + studentEntity.getStudentEmail());

        /**
         * 调用方法，发送请求
         */
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


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (requestCode == 2) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();

                    AttendanceEntity attendanceEntity = new AttendanceEntity();
                    attendanceEntity.setCheckId(checkEntForQR.getCheckId());
                    attendanceEntity.setAttendanceKind(checkEntForQR.getCheckKind());
                    attendanceEntity.setStudentId(studentInfor);

                    /**
                     * 调用定位的方法
                     */
                    getLocation(ClassDetStudentActivity.this,result,attendanceEntity);

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(ClassDetStudentActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
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

            if(attendanceEntity != null){
                if(checkEntity.isCheckIsOver()){

                    if(attendanceEntity.isAttendanceValid()){
                        textView_check_stage.setText("已签到");
                        counter++;
                    }else {
                        textView_check_stage.setText("旷课");

                    }

                }else {
                    if(attendanceEntity.isAttendanceValid()){
                        textView_check_stage.setText("已签到");
                        counter++;

                    }else {
                        textView_check_stage.setText("正在进行");
                    }
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
                        if(!tAttendanceEntity.isAttendanceValid()){
                            /**
                             * 判断考勤种类，进入不同界面
                             *
                             */
                            switch (tCheckEntity.getCheckKind()){
                                case 0:
                                    showDialog(studentInfor,tCheckEntity);
                                    break;
                                case 1:
                                    checkEntForQR = tCheckEntity;
                                    Intent intent = new Intent(ClassDetStudentActivity.this, CaptureActivity.class);
                                    startActivityForResult(intent, 2);
                                    break;
                            }

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
     * 显示随机数签到对话框
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

                    /**
                     * 调用定位的方法
                     */
                    getLocation(ClassDetStudentActivity.this,tempString,attendanceEntity);
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
    public void addAttendance(String attendanceNum,AttendanceEntity attendanceEntity,
                              Double latitude,Double longitude){

        Gson gson = new Gson();
        String tempString = gson.toJson(attendanceEntity);

        Map<String,String> tempMap = new HashMap<String,String>();
        tempMap.put("attendanceNum",attendanceNum);
        tempMap.put("attendanceEntity",tempString);
        tempMap.put("latitude",String.valueOf(latitude));
        tempMap.put("longitude",String.valueOf(longitude));

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
                            if(responseText.equals("OK")){
                                /**
                                 * 刷新显示
                                 */
                                Gson gson = new Gson();
                                ClassEntity classEnt = gson.fromJson(classEntity,ClassEntity.class);
                                getCheckAttendance(classEnt.getClassId(),classEnt.getClassFounderId());
                            }else{
                                Toast.makeText(ClassDetStudentActivity.this,"签到失败！",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(ClassDetStudentActivity.this,"服务器抽风了呢！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    public void getLocation(Context context, String attendanceNum, AttendanceEntity attendanceEntity){

        final String attendanceN = attendanceNum;
        final AttendanceEntity attendanceEnt = attendanceEntity;

        //初始化定位
        mLocationClient = new AMapLocationClient(context);

        mLocationListener = new AMapLocationListener(){
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    /**
                     * 在这里对数据进行解析
                     */
                    if (amapLocation.getErrorCode() == 0) {
                        //调用方法，向服务器传递位置信息
                        /**
                         * 调用添加签到的方法
                         */
                        addAttendance(attendanceN,attendanceEnt,amapLocation.getLatitude(), amapLocation.getLongitude());
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            }
        };

        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(false);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLocationClient!=null) {
            mLocationClient.onDestroy();//销毁定位客户端。
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationClient!=null) {
            mLocationClient.stopLocation();//停止定位
        }
    }
}
