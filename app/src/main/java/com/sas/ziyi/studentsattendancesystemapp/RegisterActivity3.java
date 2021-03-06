package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sas.ziyi.studentsattendancesystemapp.entity.LoginEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.StudentEntity;
import com.sas.ziyi.studentsattendancesystemapp.entity.TeacherEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity3 extends AppCompatActivity {

    /**
     * 存储注册信息
     * json形式
     */
    private JsonObject json_infor;

    /**
     * 页面元素
     *
     */
    private EditText editText_name;
    private RadioGroup radioGroup_gender;
    private EditText editText_school;
    private EditText editText_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        Intent intent = getIntent();
        String json_NameAndPwd = intent.getStringExtra("NameAndPwd");
        json_infor = new JsonParser().parse(json_NameAndPwd).getAsJsonObject();
        /**
         * 添加性别默认值
         */
        json_infor.addProperty("gender","male");

        editText_name = (EditText)findViewById(R.id.name);
        radioGroup_gender = (RadioGroup) findViewById(R.id.gender);
        editText_school = (EditText)findViewById(R.id.school);
        editText_email = (EditText)findViewById(R.id.email);

        Button button_register = (Button)findViewById(R.id.register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 判断姓名是否为空
                 */
                if(editText_name.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity3.this,"姓名不能为空！",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    /**
                     * 判断学校是否为空
                     */
                    if(editText_school.getText().toString().equals("")){
                        Toast.makeText(RegisterActivity3.this,"学校不能为空！",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        /**
                         * 判断Email是否为空
                         */
                        if(editText_email.getText().toString().equals("")){
                            Toast.makeText(RegisterActivity3.this,"Email不能为空！",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{
                            /**
                             * 将与教师有关的信息封装在LoginEntity实体中
                             *
                             */
                            LoginEntity loginEntity = new LoginEntity();
                            loginEntity.setUserName(json_infor.get("userName").getAsString());
                            loginEntity.setUserPassword(json_infor.get("password").getAsString());
                            /**
                             * 将与教师有关的信息封装在StudentEntity实体中
                             *
                             */
                            TeacherEntity teacherEntity = new TeacherEntity();
                            teacherEntity.setTeacherName(editText_name.getText().toString());
                            if(json_infor.get("gender").getAsString().equals("female")){
                                teacherEntity.setTeacherSex(false);
                            }
                            else{
                                teacherEntity.setTeacherSex(true);
                            }
                            teacherEntity.setTeacherSchool(editText_school.getText().toString());
                            teacherEntity.setTeacherEmail(editText_email.getText().toString());

                            /**
                             * 调用http请求的方法发送数据
                             * 参数为 教师实体 和 登陆实体
                             */
                            postRegisterInfor(loginEntity,teacherEntity);
                        }
                    }
                }
            }
        });

        radioGroup_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.male:
                        json_infor.addProperty("gender","male");
                        break;
                    case R.id.female:
                        json_infor.addProperty("gender","female");
                        break;
                    default:
                        json_infor.addProperty("gender","noSelected");
                        break;
                }
            }
        });
    }

    /**
     * 使用post提交用户的注册信息
     * @param loginEntity
     * @param teacherEntity
     */
    public void postRegisterInfor(LoginEntity loginEntity,TeacherEntity teacherEntity){
        /**
         * 使用Gson对封装好的实体进行封装
         */
        Gson gson = new Gson();
        String json_loginInfor = gson.toJson(loginEntity);
        String json_studentInfor = gson.toJson(teacherEntity);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(json_loginInfor);
        jsonArray.add(json_studentInfor);
        String tempJson = gson.toJson(jsonArray);
        Log.d("return",gson.fromJson(tempJson,JsonArray.class).get(0).getAsString());

        String url = getString(R.string.url_head) + "/registercontrol/teacherregister";
        HttpUtil.sendOKHttpPost(url, "registerInfor", tempJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity3.this,"服务器抽风了呢！",
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
                            Toast.makeText(RegisterActivity3.this,"注册成功",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity3.this,MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(RegisterActivity3.this,"服务器抽风了呢！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
