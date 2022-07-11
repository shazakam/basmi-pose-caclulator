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
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;


public class IntermalleolarActivity extends AppCompatActivity {
    Button intermalleolarUploadBtn;
    PoseDetector intermalleolarPoseDetector;
    AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermalleolar);
        intermalleolarUploadBtn = findViewById(R.id.btnIntermalleolarUpload);
    }

    ActivityResultLauncher<Intent> getImageIntermalleolar = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //Initialises pose detector with desired options

                        intermalleolarPoseDetector = PoseDetection.getClient(options);
                        /*
                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);*/

                        /*INTERMALLEOLAR PRE-DEFINED TEST CASES*/
                        intermalleolarPoseDetector = PoseDetection.getClient(options);
                        Bitmap selectedImageBitmap;
                        InputImage inputImage;
                        selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.intermalleolar_2);
                        inputImage = InputImage.fromBitmap(selectedImageBitmap,0);

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


                    }
                }
            }
    );

    public void onClickIntermalleolarUpload(View view) {
        intermalleolarUploadBtn.setBackgroundColor(Color.GREEN);
        intermalleolarUploadBtn.setEnabled(false);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
}