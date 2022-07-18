package com.example.basmi_pose_caclulator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.MainThread;
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
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;

public class CervicalActivity extends AppCompatActivity {
    Button neutralBtn;
    Button rightBtn;
    Button leftBtn;
    float leftRotation;
    float rightRotation;

    FaceDetectorOptions highAccuracyOpts =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .build();

    int btnClicked;
    PointF neutralNoseCoord;
    float radius;
    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cervical);
        leftBtn = findViewById(R.id.btnLeftCervicalUpload);
        neutralBtn = findViewById(R.id.btnNeutralCervicalUpload);
        rightBtn = findViewById(R.id.btnRightCervicalUpload);
        btnClicked = -2;
        radius = 0;
        okHttpClient = new OkHttpClient();
        ServerHandler.checkConnection(okHttpClient,"CERVICAL CONNECTED");
    }


    //This tells what getImage should do with the result from the intent
    ActivityResultLauncher<Intent> getImageCervical = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
                        Uri imageUri = result.getData().getData();

                        try {
                            Bitmap selectedImageBitmap = Calculator.getBitmapFromUri(getContentResolver(),imageUri);
                            InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            if(btnClicked == -1){
                                ServerHandler.cervicalPostImage(-1,selectedImageBitmap,okHttpClient,stream);
                            }
                            else if(btnClicked == 0){
                                ServerHandler.cervicalPostImage(0,selectedImageBitmap,okHttpClient,stream);
                            }
                            else{
                                ServerHandler.cervicalPostImage(1,selectedImageBitmap,okHttpClient,stream);
                            }

                            /*THIS IS USING FACE ESTIMATION*/
                            /*Thought process:
                             * Uses two methods to approximate rotation
                             * getRotationTwo assumes the distance between the neutral nose position and the rotated nose position
                             * to be an arc with the radius being equal to half the distance between both ears in the neutral position.
                             * This has a tendency to overestimate the angle for larger rotations (70+)
                             * The second method comes from MlFaceKit which calculates the euler angle with respect to
                             * the camera. This has a tendency to underestimate for larger angles. Hence the average of the two
                             * is taken to calculate an approximation of the true rotation.
                             **/

                            OnSuccessListener<List<Face>> onSuccessCervical = new OnSuccessListener<List<Face>>() {
                                @Override
                                public void onSuccess(List<Face> faces) {
                                    for(Face face:faces) {
                                        for (FaceLandmark landmark : face.getAllLandmarks()) {
                                            Log.d("LANDMARK " + landmark.toString(), String.valueOf(landmark.getPosition()));
                                        }
                                        if (btnClicked == -1) {
                                            //UI Change, info to see what is being executed
                                            try {
                                                Log.d("TRUE", "BUTTON LEFT CLICKED");
                                                leftBtn.setBackgroundColor(Color.GREEN);
                                                leftBtn.setEnabled(false);
                                                Calculator.cervicalLeftFace = face;
                                                PointF noseCoord = face.getLandmark(FaceLandmark.NOSE_BASE).getPosition();
                                                Calculator.cervicalYLeftEuler = face.getHeadEulerAngleY();
                                                leftRotation = (float) (Math.abs(face.getHeadEulerAngleY()) + Calculator.getRotationTwo(radius, neutralNoseCoord, noseCoord)) / 2;

                                                Log.d("LEFT RESULT", String.valueOf(leftRotation));
                                            } catch (Exception e) {
                                                toastMessage("ENTER NEUTRAL POSITION");
                                            }
                                        } else if (btnClicked == 0) {
                                            Log.d("TRUE", "BUTTON NEUTRAL CLICKED");
                                            neutralBtn.setBackgroundColor(Color.GREEN);
                                            neutralBtn.setEnabled(false);
                                            Calculator.cervicalNeutralFace = face;
                                            Calculator.cervicalYNeutralEuler = face.getHeadEulerAngleY();
                                            PointF leftEarCoord = face.getLandmark(FaceLandmark.LEFT_EAR).getPosition();
                                            PointF rightEarCoord = face.getLandmark(FaceLandmark.RIGHT_EAR).getPosition();
                                            neutralNoseCoord = face.getLandmark(FaceLandmark.NOSE_BASE).getPosition();
                                            radius = (Calculator.getDistance(leftEarCoord, rightEarCoord, 1)) / 2;
                                            Log.d("NEUTRAL NOSE", String.valueOf(neutralNoseCoord));
                                        } else {
                                            try {
                                                Log.d("TRUE", "BUTTON RIGHT CLICKED");
                                                rightBtn.setBackgroundColor(Color.GREEN);
                                                rightBtn.setEnabled(false);
                                                PointF noseCoord = face.getLandmark(FaceLandmark.NOSE_BASE).getPosition();
                                                Calculator.cervicalYRightEuler = face.getHeadEulerAngleY();
                                                Calculator.cervicalRightFace = face;
                                                rightRotation = (float) (Math.abs(face.getHeadEulerAngleY()) + Calculator.getRotationTwo(radius, neutralNoseCoord, noseCoord)) / 2;
                                                Log.d("RIGHT RESULT", String.valueOf(rightRotation));
                                            } catch (Exception e) {
                                                toastMessage("ENTER NEUTRAL POSITION");
                                            }
                                        }

                                        if(extremeCaseEliminator()){
                                            float cervicalAverage  = (rightRotation +leftRotation)/2;
                                            Calculator.cervicalLeftRotation = leftRotation;
                                            Calculator.cervicalRightRotation = rightRotation;
                                            Log.d("FINAL CERVICAL ROTATION",String.valueOf(cervicalAverage));
                                            Calculator.cervicalRotationScore = Calculator.getCervicalRotationScore(cervicalAverage);
                                            Log.d("FINAL CERVICAL SCORE",String.valueOf(Calculator.cervicalRotationScore));
                                        }
                                    }

                                }
                            };

                            Task<List<Face>> resultFace =
                                    detector.process(inputImage)
                                            .addOnSuccessListener(onSuccessCervical)
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

    public boolean extremeCaseEliminator(){
        float limit = 110;

        try{
            if(rightRotation > limit && leftRotation > limit){
                toastMessage("Both Images faulty please try again");
                rightBtn.setEnabled(true);
                rightBtn.setBackgroundColor(Color.BLACK);
                leftBtn.setEnabled(true);
                leftBtn.setBackgroundColor(Color.BLACK);
                return false;
            }
            else if(leftRotation > limit && rightRotation < limit){
                toastMessage("Left image faulty please try again");
                leftBtn.setEnabled(true);
                leftBtn.setBackgroundColor(Color.BLACK);
                return false;
            }
            else if(rightRotation > limit && leftRotation < limit){
                toastMessage("Right image faulty please try again");
                rightBtn.setEnabled(true);
                rightBtn.setBackgroundColor(Color.BLACK);
                return false;
            }
            else{
                return true;
            }
        }
        catch(Exception e){
            return false;
        }
    }

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
            toastMessage("ERROR WITH BUTTON CHOSEN: " + btnId);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        getImageCervical.launch(intent);
    }

    public void onClickCervicalNext(View view){
        Intent intent = new Intent(this, LumbarFlexionActivity.class);
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