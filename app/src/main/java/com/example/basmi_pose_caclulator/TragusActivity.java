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
import android.graphics.PointF;

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
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.IOException;


public class TragusActivity extends AppCompatActivity{

    //URI of images needs to be stored from OnClickTragusImage
    //Note to self may not be URI but BitMap instead
    //MVP = Minimun Viable Product
    Pose poseOne = null;
    Pose poseTwo = null;
    Boolean leftButtonClicked = false;
    Boolean rightButtonClicked = false;
    Button leftButton;
    Button rightButton;
    double leftTragular;
    double rightTragular;
    static double tragularSum;
    static int tragularCount;
    static double currentAverage;

    PoseDetector tragusPoseDetector;

    AccuratePoseDetectorOptions options =
            new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tragus);
        leftButton = findViewById(R.id.btnLeftUploadTragus);
        rightButton = findViewById(R.id.btnRightUploadTragus);
        leftTragular = 0;
        rightTragular = 0;
        tragularSum = 0;
        currentAverage = 0;
    }

    //This tells what getImage should do with the result from the intent
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        tragusPoseDetector = PoseDetection.getClient(options);
                        /*
                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageBitmap(selectedImageBitmap);*/

                        Bitmap selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.tragular_right);
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageBitmap(selectedImageBitmap);


                        Task<Pose> poseResult =
                                tragusPoseDetector.process(inputImage)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Pose>() {
                                                    @Override
                                                    public void onSuccess(Pose pose) {

                                                        Calculator calculator = new Calculator();

                                                        if(leftButtonClicked){
                                                            Log.d("TRUE","BUTTON LEFT CLICKED");
                                                            leftButton.setBackgroundColor(Color.GREEN);
                                                            leftButtonClicked = false;
                                                            leftTragular = calculator.tragularResult(0,pose);
                                                            extremeCaseEliminator(leftTragular);
                                                        }

                                                        else{
                                                            rightButton.setBackgroundColor(Color.GREEN);
                                                            rightButtonClicked = false;
                                                            rightTragular = calculator.tragularResult(1,pose);
                                                            extremeCaseEliminator(rightTragular);
                                                        }
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Context context = getApplicationContext();
                                                        CharSequence text = "Upload Image again";
                                                        int duration = Toast.LENGTH_SHORT;
                                                        Toast toast = Toast.makeText(context, text, duration);
                                                        toast.show();
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

    public void extremeCaseEliminator(double tragular) {

        if (tragular >= 45) {
            Context context = getApplicationContext();
            CharSequence text = "Image result faulty, upload image again please";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            tragularSum += tragular;
            leftButton.setEnabled(false);
        }
        tragusPoseDetector.close();
    }

    /*BUTTONS*/
    public void onClickTragusImageBtnOne(View view) {

        int buttonId = view.getId();
        if(buttonId == R.id.btnRightUploadTragus){
            Log.d("TRUEEEEE","BUTTON RIGHT CLICKED");
            rightButtonClicked = true;
        }

        else if(buttonId == R.id.btnLeftUploadTragus){
            Log.d("FALSE","BUTTON LEFT CLICKED");
            leftButtonClicked = true;
        }

        //Intent to take Photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImage.launch(intent);
    }

}