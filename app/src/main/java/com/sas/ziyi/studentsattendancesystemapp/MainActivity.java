package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sas.ziyi.studentsattendancesystemapp.entity.LoginEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.HttpUtil;
import com.sas.ziyi.studentsattendancesystemapp.util.Utility;

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_userName = (EditText)findViewById(R.id.user_name);
        editText_password = (EditText)findViewById(R.id.password);

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

                        postUnamePword("username_password",tempJson);
                    }
                }
            }
        });



        /**
         * 注册
         */
        Button button_register = (Button)findViewById(R.id.register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_register = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent_register);
            }
        });
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
                            /*Toast.makeText(MainActivity.this,responseText,
                                    Toast.LENGTH_LONG).show();*/
                            Intent intent = new Intent(MainActivity.this,ClassesActivity.class);
                            //intent.putExtra("userInfor",responseText);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MainActivity.this,"服务器抽风了呢！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
