package com.example.basmi_pose_caclulator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;


public class IntermalleolarActivity extends AppCompatActivity {
    Button intermalleolarUploadBtn;
    PoseDetector intermalleolarPoseDetector;
    AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build();

    OkHttpClient okHttpClient;
    SharedPreferences sp;
    EditText intermalleolarInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermalleolar);
        intermalleolarUploadBtn = findViewById(R.id.btnIntermalleolarUpload);
        okHttpClient = new OkHttpClient();
        ServerHandler.checkConnection(okHttpClient,"INTERMALLEOLAR CONNECTED");
        sp = getSharedPreferences("userLengths", Context.MODE_PRIVATE);
        intermalleolarInput = findViewById(R.id.intermalleolarPhysInput);

        if(sp.contains("intermalleolarMeasured") == true){
            intermalleolarInput.setText(String.valueOf(sp.getFloat("intermalleolarMeasured",-1)));
            Calculator.intermalleolar = sp.getFloat("intermalleolarMeasured",-1);
        }
    }

    ActivityResultLauncher<Intent> getImageIntermalleolar = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        intermalleolarPoseDetector = PoseDetection.getClient(options);
                        Uri imageUri = result.getData().getData();

                        try {
                            Bitmap selectedImageBitmap = Calculator.getBitmapFromUri(getContentResolver(),imageUri);
                            intermalleolarPoseDetector = PoseDetection.getClient(options);
                            InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            ServerHandler.intermalleolarPostImage(selectedImageBitmap,okHttpClient,stream);

                            OnSuccessListener<Pose> intermalleolarOnSuccess = new OnSuccessListener<Pose>() {
                                @Override
                                public void onSuccess(Pose pose) {
                                    Calculator calculator = new Calculator();
                                    float intermalleolarResult = calculator.getIntermalleolarResult(pose);
                                    Calculator.intermalleolarPose = pose;
                                    Calculator.intermalleolarDistance = intermalleolarResult;
                                    Calculator.intermalleolarScore = calculator.intermalleolarScore(intermalleolarResult);
                                    Log.d("INTERMALLEOLAR DISTANCE",String.valueOf(intermalleolarResult));
                                    Log.d("INTERMALLEOLAR SCORE",String.valueOf(Calculator.intermalleolarScore));
                                    toastMessage("Upload Successful");
                                }
                            };

                            Task<Pose> poseResult = intermalleolarPoseDetector.process(inputImage)
                                    .addOnSuccessListener(intermalleolarOnSuccess)
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    toastMessage("Upload Image Again");
                                                }
                                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    public void onClickIntermalleolarUpload(View view) {
        intermalleolarUploadBtn.setBackgroundColor(Color.GREEN);
        intermalleolarUploadBtn.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        getImageIntermalleolar.launch(intent);
    }

    public void onIntermalleolarNextClick(View view){
        Intent intent = new Intent(this, CervicalActivity.class);
        startActivity(intent);
    }

    public void toastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onIntermalleolarMeasurmentsClick(View view){

        try{
            float intermalleolarMeasured = Float.parseFloat(intermalleolarInput.getText().toString());
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("intermalleolarMeasured",intermalleolarMeasured);
            editor.apply();
            Calculator.intermalleolar = intermalleolarMeasured;
            toastMessage("Lengths Submitted");

            Log.d("intermalleolar measured",String.valueOf(Calculator.intermalleolar));
        } catch(NumberFormatException e){
            toastMessage("Please input a valid length");
        }
    }
}