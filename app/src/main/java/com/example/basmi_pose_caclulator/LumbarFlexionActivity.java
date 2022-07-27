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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;

public class LumbarFlexionActivity extends AppCompatActivity {
    int btnClicked;
    Button leftNeutralButton;
    Button leftExtensionButton;
    Button rightExtensionButton;
    Button rightNeutralButton;

    PoseDetector flexionPoseDetector;
    AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build();

    OkHttpClient okHttpClient;
    SharedPreferences sp;

    EditText flexionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumbar_flexion);

        leftNeutralButton = findViewById(R.id.flexionLeftNeutral);
        leftExtensionButton = findViewById(R.id.flexionLeftExtension);
        rightNeutralButton = findViewById(R.id.flexionRightNeutral);
        rightExtensionButton = findViewById(R.id.flexionRightExtension);

        flexionInput = findViewById(R.id.flexionPhysInput);
        okHttpClient = new OkHttpClient();
        ServerHandler.checkConnection(okHttpClient,"LUMBAR FLEXION CONNECTED");
        sp = getSharedPreferences("userLengths", Context.MODE_PRIVATE);

        if(sp.contains("flexionMeasured") == true){
            flexionInput.setText(String.valueOf(sp.getFloat("flexionMeasured",-1)));
            Calculator.flexion= sp.getFloat("flexionMeasured",-1);
        }

    }

    ActivityResultLauncher<Intent> getImageFlexion = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override

                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        flexionPoseDetector = PoseDetection.getClient(options);
                        Uri imageUri = result.getData().getData();

                        try {
                            Bitmap selectedImageBitmap = Calculator.getBitmapFromUri(getContentResolver(),imageUri);
                            InputImage inputImage;
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            if(btnClicked == -1){
                                ServerHandler.flexionPostImage(-1,selectedImageBitmap,okHttpClient,stream);
                            }
                            else if(btnClicked == 0){
                                ServerHandler.flexionPostImage(0,selectedImageBitmap,okHttpClient,stream);
                            }
                            else if(btnClicked == 1){
                                ServerHandler.flexionPostImage(1,selectedImageBitmap,okHttpClient,stream);
                            }
                            else{
                                ServerHandler.flexionPostImage(2,selectedImageBitmap,okHttpClient,stream);
                            }

                            //If the pose detector is successful it executes onSuccess
                            OnSuccessListener<Pose> flexionOnSuccess = new OnSuccessListener<Pose>() {
                                @Override
                                public void onSuccess(Pose pose) {
                                    if(btnClicked == -1){
                                        leftNeutralButton.setBackgroundColor(Color.GREEN);

                                        leftNeutralButton.setEnabled(false);
                                        Calculator.flexionLeftNeutralPose = pose;
                                        Calculator.printPoses(pose);
                                        toastMessage("Upload Successful");
                                    }
                                    else if(btnClicked == 0){
                                        leftExtensionButton.setBackgroundColor(Color.GREEN);
                                        leftExtensionButton.setEnabled(false);
                                        Calculator.flexionLeftExtensionPose = pose;
                                        Calculator.printPoses(pose);
                                        toastMessage("Upload Successful");

                                    }

                                    else if(btnClicked == 1){
                                        rightNeutralButton.setBackgroundColor(Color.GREEN);
                                        rightNeutralButton.setEnabled(false);
                                        Calculator.flexionRightNeutralPose= pose;
                                        Calculator.printPoses(pose);
                                        toastMessage("Upload Successful");

                                    }
                                    else if(btnClicked == 2){
                                        rightExtensionButton.setBackgroundColor(Color.GREEN);
                                        rightExtensionButton.setEnabled(false);
                                        Calculator.flexionRightExtensionPose = pose;
                                        Calculator.printPoses(pose);
                                        toastMessage("Upload Successful");
                                    }
                                    else{
                                        toastMessage("ERROR");
                                        return;
                                    }
                                }
                            };

                            Task<Pose> poseResult = flexionPoseDetector.process(inputImage).addOnSuccessListener(flexionOnSuccess).addOnFailureListener(
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


    public void onClickFlexionUpload(View view){
        int btnId = view.getId();
        if(btnId == R.id.flexionLeftNeutral){
            btnClicked = -1;
            Log.d("BUTTON CLICKED","-1");
        }
        else if(btnId == R.id.flexionLeftExtension){
            btnClicked = 0;
            Log.d("BUTTON CLICKED","0");
        }
        else if(btnId == R.id.flexionRightNeutral){
            btnClicked = 1;
            Log.d("BUTTON CLICKED","1");
        }

        else if(btnId == R.id.flexionRightExtension){
            btnClicked = 2;
            Log.d("BUTTON CLICKED","2");
        }
        else{
            toastMessage("ERROR WITH BUTTON CHOSEN: " + btnId);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        getImageFlexion.launch(intent);
    }

    public void onClickFlexionNext(View view){
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }

    public void toastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onFlexionMeasurmentsClick(View view){

        try{
            float flexionMeasured = Float.parseFloat(flexionInput.getText().toString());

            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("flexionMeasured",flexionMeasured);
            editor.apply();
            Calculator.flexion = flexionMeasured;
            toastMessage("Lengths Submitted");

            Log.d("flexion",String.valueOf(Calculator.flexion));
        } catch(NumberFormatException e){
            toastMessage("Please input a valid length");
        }
    }
}