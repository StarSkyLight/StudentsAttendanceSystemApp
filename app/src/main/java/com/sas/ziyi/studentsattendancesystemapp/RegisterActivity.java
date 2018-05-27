package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sas.ziyi.studentsattendancesystemapp.entity.LoginEntity;
import com.sas.ziyi.studentsattendancesystemapp.util.Utility;

public class RegisterActivity extends AppCompatActivity {
    /**
     * 用户名
     */
    private EditText editText_userName;
    /**
     * 密码
     */
    private EditText editText_password;
    /**
     * 确认密码
     */
    private EditText editText_password_check;

    /**
     * 单选框
     * @param savedInstanceState
     */
    private RadioGroup radioGroup_role;


    /**
     * 用于存储当前活动输入的注册信息
     * json形式
     * @param savedInstanceState
     */
    private JsonObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editText_userName = (EditText)findViewById(R.id.user_name);
        editText_password = (EditText)findViewById(R.id.password);
        editText_password_check = (EditText)findViewById(R.id.check_password);

        radioGroup_role = (RadioGroup)findViewById(R.id.choose_role);

        Button button_register_go = (Button)findViewById(R.id.next);
        button_register_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 判断用户名是否为空
                 */
                if(editText_userName.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"用户名不能为空！",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    /**
                     * 判断密码是否为空
                     */
                    if(editText_password.getText().toString().equals("")){
                        Toast.makeText(RegisterActivity.this,"密码不能为空！",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        /**
                         * 判断确认密码是否为空
                         */
                        if(editText_password_check.getText().toString().equals("")){
                            Toast.makeText(RegisterActivity.this,"请确认密码！",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{
                            /**
                             * 判断密码和确认密码是否一致
                             */
                            if(editText_password.getText().toString()
                                    .equals(editText_password_check.getText().toString())){
                                /**
                                 * 判断是否选择角色
                                 */
                                if(jsonObject != null){
                                    jsonObject.addProperty("userName",editText_userName.getText().toString());
                                    jsonObject.addProperty("password", Utility.HashCode(editText_password.getText().toString()));

                                    if(jsonObject.get("role").getAsString().equals("student")){
                                        Intent intent = new Intent(RegisterActivity.this,RegisterActivity2.class);
                                        intent.putExtra("NameAndPwd",jsonObject.toString());
                                        startActivity(intent);
                                    }
                                    else{
                                        Intent intent = new Intent(RegisterActivity.this,RegisterActivity3.class);
                                        intent.putExtra("NameAndPwd",jsonObject.toString());
                                        startActivity(intent);
                                    }
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this,"请选择角色！",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(RegisterActivity.this,"两次输入密码不一致！",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        radioGroup_role.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                jsonObject = new JsonObject();
                switch (checkedId){
                    case R.id.student:
                        jsonObject.addProperty("role","student");
                        break;
                    case R.id.teacher:
                        jsonObject.addProperty("role","teacher");
                        break;
                    default:
                        jsonObject.addProperty("role","student");
                        break;
                }
            }
        });
    }
}
