package com.example.basmi_pose_caclulator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerHandler {

    public static void checkConnection(OkHttpClient okHttpClient,String text){
        try {
            RequestBody formbody = new FormBody.Builder().add("sample",text).build();
            Request request = new Request.Builder().url("http://138.38.166.67:5000/debug")
                    .post(formbody).build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("SERVER FAILURE","NO CONNECTION "+text);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                }
            });
            Log.d(text,"RUNNING");

        } catch (Exception e) {
            Log.d(text,"FAILURE");
            e.printStackTrace();
        }
    }

    public static void tragularPostImage(int btnClicked, Bitmap image, OkHttpClient okHttpClient, ByteArrayOutputStream stream){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        image.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        RequestBody postBodyImage;
        String tragularRoute = null;

        if(btnClicked == 0){
            multipartBodyBuilder.addFormDataPart("leftTragular" , "left_tragular.png", RequestBody.create(byteArray,MediaType.parse("image/*jpg")));
            tragularRoute = "tragularLeft";
            }
        else{
            multipartBodyBuilder.addFormDataPart("rightTragular" , "right_tragular.png", RequestBody.create(byteArray,MediaType.parse("image/*jpg")));
            tragularRoute = "tragularRight";
        }
        postBodyImage = multipartBodyBuilder.build();
        Request request = new Request.Builder()
                .url("http://138.38.166.67:5000/"+tragularRoute)
                .post(postBodyImage)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
                Log.d("FAILURE TO CONNECT","ERROR L151");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Server Connection","HUZZAH");
            }
        });
    }

    public static void lumbarPostImage(int btnClicked, Bitmap image, OkHttpClient okHttpClient, ByteArrayOutputStream stream){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        image.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        RequestBody postBodyImage;
        String lumbarRoute = null;

        if(btnClicked == -1){
            multipartBodyBuilder.addFormDataPart("leftLumbar","left_lumbar.jpg",RequestBody.create(byteArray,MediaType.parse("image/*jpg")));
            lumbarRoute = "lumbarLeft";
        }
        else if(btnClicked == 0){
            multipartBodyBuilder.addFormDataPart("neutralLumbar","neutral_lumbar.jpg",RequestBody.create(byteArray,MediaType.parse("image/*jpg")));
            lumbarRoute = "lumbarNeutral";
        }
        else{
            multipartBodyBuilder.addFormDataPart("rightLumbar","Right_lumbar.jpg",RequestBody.create(byteArray,MediaType.parse("image/*jpg")));
            lumbarRoute = "lumbarRight";
        }

        postBodyImage = multipartBodyBuilder.build();
        Request request = new Request.Builder()
                .url("http://138.38.166.67:5000/"+lumbarRoute)
                .post(postBodyImage)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
                Log.d("FAILURE TO CONNECT","ERROR L151");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Server Connection","HUZZAH");
            }
        });
    }

    public static void intermalleolarPostImage(Bitmap image, OkHttpClient okHttpClient, ByteArrayOutputStream stream){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        image.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        RequestBody postBodyImage;
        multipartBodyBuilder.addFormDataPart("intermalleolar","intermalleolar.jpg",RequestBody.create(byteArray,MediaType.parse("image/*jpg")));

        postBodyImage = multipartBodyBuilder.build();
        Request request = new Request.Builder()
                .url("http://138.38.166.67:5000/intermalleolar")
                .post(postBodyImage)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
                Log.d("FAILURE TO CONNECT","ERROR");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Server Connection","HUZZAH");
            }
        });
    }

    public static void cervicalPostImage(int btnClicked,Bitmap image,OkHttpClient okHttpClient,ByteArrayOutputStream stream){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        image.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        RequestBody postBodyImage;
        String cervicalRoute = null;

        if(btnClicked == -1){
            multipartBodyBuilder.addFormDataPart("leftCervical","left_cervical.jpg",RequestBody.create(byteArray,MediaType.parse("image/*jpg")));
            cervicalRoute = "cervicalLeft";
        }
        else if(btnClicked == 0){
            multipartBodyBuilder.addFormDataPart("neutralCervical","neutral_cervical.jpg",RequestBody.create(byteArray,MediaType.parse("image/*jpg")));
            cervicalRoute = "cervicalNeutral";
        }
        else{
            multipartBodyBuilder.addFormDataPart("rightCervical","Right_cervical.jpg",RequestBody.create(byteArray,MediaType.parse("image/*jpg")));
            cervicalRoute = "cervicalRight";
        }

        postBodyImage = multipartBodyBuilder.build();
        Request request = new Request.Builder()
                .url("http://138.38.166.67:5000/"+cervicalRoute)
                .post(postBodyImage)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
                Log.d("FAILURE TO CONNECT","ERROR L151");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Server Connection","HUZZAH");
            }
        });
    }
}
