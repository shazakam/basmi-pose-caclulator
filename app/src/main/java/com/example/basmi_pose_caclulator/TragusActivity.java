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

    double myLIndexLWrist = 21;

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

    }

    //This tells what getImage should do with the result from the intent
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        tragusPoseDetector = PoseDetection.getClient(options);

                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
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
                                                            //poseOne = pose;
                                                            calculator.tragularResult(0,pose,myLIndexLWrist);
                                                        }

                                                        else{
                                                            rightButton.setBackgroundColor(Color.GREEN);
                                                            rightButtonClicked = false;
                                                            //poseTwo = pose;
                                                            calculator.tragularResult(1,pose,myLIndexLWrist);
                                                        }
                                                        /*
                                                        PointF leftIndexPosition =  pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
                                                        PointF leftWristPosition = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition();
                                                        PointF leftEarPosition = pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition();

                                                        //double ratio = myLIndexLWrist/euclideanDistance(leftIndexPosition, leftWristPosition,1.0);
                                                        double ratio = myLIndexLWrist/calculator.getDistance(leftIndexPosition, leftWristPosition,1.0);

                                                        //Calculate distance between tragus and index (test is for between L.I to R.I)
                                                        float tragularPreDist = calculator.getDistance(leftEarPosition, leftIndexPosition,ratio);
                                                        //euclideanDistance(leftEarPosition, leftIndexPosition,ratio);

                                                        double finalTragularDist = ratio*tragularPreDist;

                                                        Log.d("FINAL BLOODY RESULT: ",String.valueOf(finalTragularDist));
                                                        Log.d("RATIO",String.valueOf(ratio));
                                                        Log.d("Distance",String.valueOf(euclideanDistance(leftIndexPosition, leftWristPosition,1.0)));*/
                                                        tragusPoseDetector.close();
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

    public static float euclideanDistance(PointF firstPoint, PointF secondPoint, double ratio) {

        //Calculate the vector from firstPoint to secondPoint and return its length
        float xCoord= (float) ratio*(firstPoint.x - secondPoint.x);
        float yCoord = (float) ratio*(firstPoint.y - secondPoint.y);

        Log.d("X Coordinate",String.valueOf(xCoord));
        Log.d("Y Coordinate",String.valueOf(yCoord));

        PointF finalVector = new PointF();
        finalVector.set(xCoord,yCoord);
        Log.d("VECTOR LENGTH",String.valueOf(finalVector.length()));

        return (float) ratio*finalVector.length();
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

    public void onClickDeletePoses(View view){
        poseOne = null;
        poseTwo = null;

        Context context = getApplicationContext();
        CharSequence text = "Upload Images with Buttons again Please";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void btnPrintPose(View view){

        if(poseOne != null && poseTwo != null){
            for(PoseLandmark p: poseOne.getAllPoseLandmarks()){
                Log.d("Pose One LANDMARK TYPE " +  String.valueOf(p.getLandmarkType()), p.getPosition().toString());
                Log.d("Pose One Probability: " +  String.valueOf(p.getLandmarkType()), String.valueOf(p.getInFrameLikelihood()));
            }

            for(PoseLandmark p: poseTwo.getAllPoseLandmarks()){
                Log.d("Pose Two LANDMARK TYPE " +  String.valueOf(p.getLandmarkType()), p.getPosition().toString());
                Log.d("Pose Two Probability: " +  String.valueOf(p.getLandmarkType()), String.valueOf(p.getInFrameLikelihood()));
            }
        }
        else{
            Context context = getApplicationContext();
            CharSequence text = "Upload all Images please";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

}