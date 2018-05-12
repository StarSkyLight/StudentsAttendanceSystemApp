package com.sas.ziyi.studentsattendancesystemapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ShowQRCodeActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qrcode);

        /**
         * toolbar
         */
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        imageView = (ImageView)findViewById(R.id.qrcode);

        Intent intent = getIntent();
        String code = intent.getStringExtra("code");

        showQRCode(code);
    }


    public void showQRCode(String code){
        Bitmap mBitmap = CodeUtils.createImage(code, 400, 400, null);
        imageView.setImageBitmap(mBitmap);
    }
}
