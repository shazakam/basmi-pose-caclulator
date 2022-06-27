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

public class CervicalActivity extends AppCompatActivity {
    Button neutralBtn;
    Button rightBtn;
    Button leftBtn;
    float leftResult;
    float rightResult;
    PoseDetector cervicalPoseDetector;
    AccuratePoseDetectorOptions options =
            new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();
    int btnClicked;
    PointF midPointCoord;
    PointF neutralNoseCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cervical);
        leftBtn = findViewById(R.id.btnLeftCervicalUpload);
        neutralBtn = findViewById(R.id.btnNeutralCervicalUpload);
        rightBtn = findViewById(R.id.btnRightCervicalUpload);
        btnClicked = -2;
    }


    //This tells what getImage should do with the result from the intent
    ActivityResultLauncher<Intent> getImageCervical = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //Initialises pose detector with desired options
                        cervicalPoseDetector = PoseDetection.getClient(options);

                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        ImageView imageView = findViewById(R.id.cervicalNeutralExample);
                        imageView.setImageBitmap(selectedImageBitmap);

                        /*NEED TO GET CERVICAL PRE-DEFINED TEST EXAMPLES*/


                        Task<Pose> poseResult =
                                cervicalPoseDetector.process(inputImage)
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
                                                            PointF noseCoord = pose.getPoseLandmark(PoseLandmark.NOSE).getPosition();

                                                            leftResult = (float) calculator.getRotationOne(midPointCoord,neutralNoseCoord,noseCoord);

                                                            Log.d("LEFT RESULT",String.valueOf(leftResult));
                                                        }

                                                        else if(btnClicked == 0) {
                                                            Log.d("TRUE", "BUTTON NEUTRAL CLICKED");
                                                            neutralBtn.setBackgroundColor(Color.GREEN);
                                                            neutralBtn.setEnabled(false);
                                                            midPointCoord = calculator.getMidPoint(pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition(),
                                                                    pose.getPoseLandmark(PoseLandmark.RIGHT_EAR).getPosition());
                                                            neutralNoseCoord = pose.getPoseLandmark(PoseLandmark.NOSE).getPosition();
                                                            Log.d("MIDPOINT", String.valueOf(midPointCoord));
                                                        }
                                                        else{
                                                            Log.d("TRUE","BUTTON RIGHT CLICKED");
                                                            rightBtn.setBackgroundColor(Color.GREEN);
                                                            rightBtn.setEnabled(false);
                                                            PointF noseCoord = pose.getPoseLandmark(PoseLandmark.NOSE).getPosition();
                                                            rightResult = (float) calculator.getRotationOne(midPointCoord,neutralNoseCoord,noseCoord);
                                                            Log.d("RIGHT RESULT",String.valueOf(rightResult));
                                                        }

                                                        if((!leftBtn.isEnabled()) && (!rightBtn.isEnabled()) && (!neutralBtn.isEnabled())){
                                                            float cervicalAverage  = (rightResult+leftResult)/2;
                                                            Log.d("FINAL CERVICAL ROTATION",String.valueOf(cervicalAverage));

                                                            calculator.cervicalRotationScore = calculator.getCervicalRotationScore(cervicalAverage);
                                                            Log.d("FINAL LUMBAR SCORE",String.valueOf(calculator.cervicalRotationScore));
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

    /*BUTTONS*/
    public void onClickCervicalUpload(View view){
        int btnId = view.getId();

        if(btnId == R.id.btnLeftCervicalUpload){
            btnClicked = -1;
            Log.d("BUTTON CLICKED","-1");
        }
        else if(btnId == R.id.btnNeutralCervicalUpload){
            btnClicked = 0;
            Log.d("BUTTON CLICKED","0");
        }
        else if(btnId == R.id.btnRightCervicalUpload){
            btnClicked = 1;
            Log.d("BUTTON CLICKED","1");
        }
        else{
            toastMessage("ERROR WITH BUTTON CHOSEN: " + String.valueOf(btnId));
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImageCervical.launch(intent);
    }

    public void toastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}