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
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

public class LumbarActivity extends AppCompatActivity {
    Button neutralBtn;
    Button rightBtn;
    Button leftBtn;
    double[] leftResult;
    double[] rightResult;
    PoseDetector lumbarPoseDetector;
    AccuratePoseDetectorOptions options =
            new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();
    PointF leftNeutralCoordinate;
    PointF rightNeutralCoordinate;

    //-1=left,0=neutral,1=right, any other number indicates no clicking
    int btnClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumbar);
        neutralBtn = findViewById(R.id.btnNeutralLumbarUpload);
        rightBtn = findViewById(R.id.btnRightLumbarUpload);
        leftBtn = findViewById(R.id.btnLeftLumbarUpload);
        btnClicked = -2;
    }

    //This tells what getImage should do with the result from the intent
    ActivityResultLauncher<Intent> getImageLumbar = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //Initialises pose detector with desired options
                        lumbarPoseDetector = PoseDetection.getClient(options);
                         //Taking Picture (Currently using predefined images)
                        /*
                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);*/

                        //ImageView imageView = findViewById(R.id.imageView);
                        //imageView.setImageBitmap(selectedImageBitmap);
                        Bitmap selectedImageBitmap;
                        InputImage inputImage;

                        //This if-else statement is just used for pre-loaded images and will be removed for when photos need to be uploaded
                        //Left Clicked
                        if(btnClicked == -1){
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.george_left_flexion);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ImageView imageView = findViewById(R.id.lumbarNeutralExample);
                            imageView.setImageBitmap(selectedImageBitmap);
                        }
                        //Neutral Clicked
                        else if(btnClicked == 0){
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.george_neutral);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ImageView imageView = findViewById(R.id.lumbarNeutralExample);
                            imageView.setImageBitmap(selectedImageBitmap);
                        }
                        //Right Clicked
                        else{
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.george_right_flexion);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ImageView imageView = findViewById(R.id.lumbarNeutralExample);
                            imageView.setImageBitmap(selectedImageBitmap);
                        }

                        Task<Pose> poseResult =
                                lumbarPoseDetector.process(inputImage)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Pose>() {
                                                    @Override
                                                    public void onSuccess(Pose pose) {

                                                        Calculator calculator = new Calculator();
                                                        calculator.printPoses(pose);

                                                        if(btnClicked == -1){
                                                            //UI Change, info to see what is being executed
                                                            Log.d("TRUE","BUTTON LEFT CLICKED");
                                                            leftBtn.setBackgroundColor(Color.GREEN);
                                                            leftBtn.setEnabled(false);
                                                            leftResult = calculator.lumbarResult(-1,pose,leftNeutralCoordinate);
                                                        }
                                                        else if(btnClicked == 0){
                                                            Log.d("TRUE","BUTTON NEUTRAL CLICKED");
                                                            neutralBtn.setBackgroundColor(Color.GREEN);
                                                            neutralBtn.setEnabled(false);
                                                            leftNeutralCoordinate = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
                                                            rightNeutralCoordinate = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition();
                                                        }
                                                        else{
                                                            Log.d("TRUE","BUTTON RIGHT CLICKED");
                                                            rightBtn.setBackgroundColor(Color.GREEN);
                                                            rightBtn.setEnabled(false);
                                                            rightResult = calculator.lumbarResult(1,pose,rightNeutralCoordinate);
                                                        }

                                                        if(extremeCaseEliminator()){
                                                            Calculator.lumbarLeftElbow = (float) leftResult[0];
                                                            Calculator.lumbarRightElbow = (float) rightResult[0];
                                                            Calculator.lumbarLeftWrist = (float) leftResult[1];
                                                            Calculator.lumbarRightWrist = (float) rightResult[1];
                                                            double lumbarAverageElbow  = (rightResult[0]+leftResult[0])/2;
                                                            double lumbarAverageWrist = (rightResult[1]+leftResult[1])/2;
                                                            Log.d("FINAL LUMBAR ELBOW",String.valueOf(lumbarAverageElbow));
                                                            Log.d("FINAL LUMBAR WRIST",String.valueOf(lumbarAverageWrist));

                                                            calculator.lumbarSideFlexionScore = calculator.lumbarScore((lumbarAverageElbow+lumbarAverageWrist)/2);
                                                            Log.d("FINAL LUMBAR SCORE",String.valueOf(calculator.lumbarSideFlexionScore));
                                                        }
                                                        btnClicked = -2;
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        toastMessage("Upload Image Again");
                                                    }
                                                });
                        toastMessage("Upload Successful");
                    }
                }
            }
    );

    public boolean extremeCaseEliminator(){
        try{
            if(leftResult[0] >= 50 || leftResult[1] >= 50 || rightResult[0] >= 50 || rightResult[1] >= 50){
                toastMessage("Image result faulty, reload image again please");

                if((leftResult[0] >= 50 || leftResult[1] >= 50)&&(rightResult[0] >= 50 || rightResult[1] >= 50)){
                    leftBtn.setEnabled(true);
                    leftBtn.setBackgroundColor(Color.BLACK);
                    rightBtn.setEnabled(true);
                    rightBtn.setBackgroundColor(Color.BLACK);
                }
                else if(leftResult[0] >= 50 || leftResult[1] >= 50){
                    leftBtn.setEnabled(true);
                    leftBtn.setBackgroundColor(Color.BLACK);
                }
                else{
                    rightBtn.setEnabled(true);
                    rightBtn.setBackgroundColor(Color.BLACK);
                }
                lumbarPoseDetector.close();
                return false;
            }
            else{
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }


    /*BUTTONS*/
    public void onClickLumbarUpload(View view){
        int btnId = view.getId();

        if(btnId == R.id.btnLeftLumbarUpload){
            btnClicked = -1;
            Log.d("BUTTON CLICKED","-1");
        }
        else if(btnId == R.id.btnNeutralLumbarUpload){
            btnClicked = 0;
            Log.d("BUTTON CLICKED","0");
        }
        else if(btnId == R.id.btnRightLumbarUpload){
            btnClicked = 1;
            Log.d("BUTTON CLICKED","1");
        }
        else{
            toastMessage("ERROR WITH BUTTON CHOSEN: " + String.valueOf(btnId));
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImageLumbar.launch(intent);
    }

    public void onLumbarNextClick(View view){
        Intent intent = new Intent(this, IntermalleolarActivity.class);
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