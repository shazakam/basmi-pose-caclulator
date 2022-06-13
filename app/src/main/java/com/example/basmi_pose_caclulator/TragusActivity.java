package com.example.basmi_pose_caclulator;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.List;


public class TragusActivity extends AppCompatActivity {

    //URI of images needs to be stored from OnClickTragusImage
    //Note to self may not be URI but BitMap instead
    //MVP = Minimun Viable Product
    Pose poseOne = null;
    Pose poseTwo = null;

    PoseDetector tragusPoseDetector;

    AccuratePoseDetectorOptions options =
            new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tragus);

        //Initialise pose detector with defined options above
        tragusPoseDetector = PoseDetection.getClient(options);

    }

    //This tells what getImage should do with the result from the intent
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");

                        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 90);

                        Task<Pose> poseResult =
                                tragusPoseDetector.process(inputImage)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Pose>() {
                                                    @Override
                                                    public void onSuccess(Pose pose) {

                                                        if(poseOne == null){
                                                            poseOne = pose;

                                                        }
                                                        else if(poseTwo == null){
                                                            poseTwo = pose;
                                                        }

                                                        // Task completed successfully
                                                        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });


                        //Toast
                        Context context = getApplicationContext();
                        CharSequence text = "Upload Succesful!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            }
    );


    public void onClickTragusImageBtnOne(View view) {
        //Intent to take Photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImage.launch(intent);

    }

    public void btnPrintPose(View view){
        for(PoseLandmark p: poseOne.getAllPoseLandmarks()){
            Log.d("Pose One LANDMARK TYPE " +  String.valueOf(p.getLandmarkType()), p.getPosition().toString());
        }

        for(PoseLandmark p: poseTwo.getAllPoseLandmarks()){
            Log.d("Pose Two LANDMARK TYPE " +  String.valueOf(p.getLandmarkType()), p.getPosition().toString());
        }
    }

}