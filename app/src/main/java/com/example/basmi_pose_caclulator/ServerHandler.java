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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerHandler {

    public static void tragularPostImage(int btnClicked, Bitmap image, OkHttpClient okHttpClient, ByteArrayOutputStream stream){

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        image.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        RequestBody postBodyImage;

        String tragularRoute = null;
        if(btnClicked == 0){
            multipartBodyBuilder.addFormDataPart("leftTragular" , "left_tragular.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
            tragularRoute = "tragularLeft";
            }
        else{
            multipartBodyBuilder.addFormDataPart("rightTragular" , "right_tragular.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
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
}
