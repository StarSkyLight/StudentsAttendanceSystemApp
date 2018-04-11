package com.sas.ziyi.studentsattendancesystemapp.util;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static void sendOKHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public static void sendOKHttpPost(String address,String dataName,String jsonData,okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add(dataName,jsonData)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
