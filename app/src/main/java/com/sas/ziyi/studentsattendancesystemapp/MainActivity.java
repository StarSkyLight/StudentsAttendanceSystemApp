package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sas.ziyi.studentsattendancesystemapp.entity.LoginEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;
import com.sas.ziyi.studentsattendancesystemapp.util.Utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    /**
     * 用户名
     */
    private EditText editText_userName;
    /**
     * 密码
     */
    private EditText editText_password;

    private RadioGroup radioGroup_character;

    private String character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //动态修改标题栏颜色
       /* Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));*/   //这里动态修改颜色

        /**
         * 设置默认值
         */
        character = "student";

        editText_userName = (EditText)findViewById(R.id.user_name);
        editText_password = (EditText)findViewById(R.id.password);

        radioGroup_character = (RadioGroup) findViewById(R.id.character);


        radioGroup_character.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.student:
                        character = "student";
                        break;
                    case R.id.teacher:
                        character = "teacher";
                        break;
                }
            }
        });


        /**
         * 登陆
         */
        Button button_login = (Button)findViewById(R.id.login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 判断用户名是否为空
                 */
                if(editText_userName.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"用户名不能为空！",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    /**
                     * 判断密码是否为空
                     */
                    if(editText_password.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this,"密码不能为空！",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        LoginEntity loginEntity = new LoginEntity();
                        loginEntity.setUserName(editText_userName.getText().toString());
                        loginEntity.setUserPassword(Utility.HashCode(editText_password.getText().toString()));

                        Gson gson = new Gson();
                        String tempJson = gson.toJson(loginEntity);

                        Map<String,String > tempMap = new HashMap<String,String>();
                        tempMap.put("user",tempJson);
                        tempMap.put("character",character);

                        String tempLoginInfo = gson.toJson(tempMap);

                        postUnamePword("username_password",tempLoginInfo);
                    }
                }
            }
        });



        /**
         * 注册
         */
        TextView button_register = (TextView)findViewById(R.id.register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_register = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent_register);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * 提交用户名和密码
     */
    public void postUnamePword(String dataNaem,String jsonData){
        String url = getString(R.string.url_head) + "/logincontrol/login";
        HttpUtil.sendOKHttpPost(url,dataNaem,jsonData,new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"服务器抽风了呢！",
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
                            if(character.equals("teacher")){
                                Intent intent = new Intent(MainActivity.this,ClassesForTeacherActivity.class);
                                intent.putExtra("userInfor",responseText);
                                intent.putExtra("userName",editText_userName.getText().toString());
                                startActivity(intent);
                            }
                            else if(character.equals("student")) {
                                Intent intent = new Intent(MainActivity.this,ClassesForStudentActivity.class);
                                intent.putExtra("userInfor",responseText);
                                intent.putExtra("userName",editText_userName.getText().toString());
                                startActivity(intent);
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this,"用户名或密码错误！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
