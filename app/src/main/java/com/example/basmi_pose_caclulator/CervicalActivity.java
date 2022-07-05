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
    float leftRotation;
    float rightRotation;
    PoseDetector cervicalPoseDetector;
    AccuratePoseDetectorOptions options =
            new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();
    int btnClicked;
    PointF neutralNoseCoord;
    float radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cervical);
        leftBtn = findViewById(R.id.btnLeftCervicalUpload);
        neutralBtn = findViewById(R.id.btnNeutralCervicalUpload);
        rightBtn = findViewById(R.id.btnRightCervicalUpload);
        btnClicked = -2;
        radius = 0;

        ImageView neutralExample = findViewById(R.id.cervicalNeutralExample);
        ImageView rightExample = findViewById(R.id.cervicalRightExample);
        ImageView leftExample = findViewById(R.id.cervicalLeftExample);

        Bitmap neutralBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.louis_junk);
        Bitmap rightBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.louis_junk);
        Bitmap leftBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.louis_junk);

        neutralExample.setImageBitmap(neutralBitmap);
        rightExample.setImageBitmap(rightBitmap);
        leftExample.setImageBitmap(leftBitmap);
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
                                                        Calculator.printPoses(pose);

                                                        if(btnClicked == -1){
                                                            //UI Change, info to see what is being executed
                                                            try{
                                                                Log.d("TRUE","BUTTON LEFT CLICKED");
                                                                leftBtn.setBackgroundColor(Color.GREEN);
                                                                leftBtn.setEnabled(false);
                                                                PointF noseCoord = pose.getPoseLandmark(PoseLandmark.NOSE).getPosition();
                                                                leftRotation = (float) Calculator.getRotationOne(radius,neutralNoseCoord,noseCoord);
                                                                Log.d("LEFT RESULT",String.valueOf(leftRotation));
                                                            }catch(Exception e){
                                                                toastMessage("ENTER NEUTRAL POSITION");
                                                            }
                                                        }
                                                        else if(btnClicked == 0) {
                                                            Log.d("TRUE", "BUTTON NEUTRAL CLICKED");
                                                            neutralBtn.setBackgroundColor(Color.GREEN);
                                                            neutralBtn.setEnabled(false);
                                                            PointF leftEarCoord = pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition();
                                                            PointF rightEarCoord = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR).getPosition();
                                                            neutralNoseCoord = pose.getPoseLandmark(PoseLandmark.NOSE).getPosition();
                                                            radius = (Calculator.getDistance(leftEarCoord,neutralNoseCoord,1)+Calculator.getDistance(rightEarCoord,neutralNoseCoord,1))/2;
                                                            Log.d("NEUTRAL NOSE",String.valueOf(neutralNoseCoord));
                                                        }
                                                        else{
                                                            try{
                                                                Log.d("TRUE","BUTTON RIGHT CLICKED");
                                                                rightBtn.setBackgroundColor(Color.GREEN);
                                                                rightBtn.setEnabled(false);
                                                                PointF noseCoord = pose.getPoseLandmark(PoseLandmark.NOSE).getPosition();
                                                                rightRotation = (float) Calculator.getRotationOne(radius,neutralNoseCoord,noseCoord);
                                                                Log.d("RIGHT RESULT",String.valueOf(rightRotation));
                                                            }catch(Exception e){
                                                                toastMessage("ENTER NEUTRAL POSITION");
                                                            }
                                                        }
                                                        //NEED TO IMPLEMENT EXTREME CASE ELIMINATOR HERE
                                                        if(extremeCaseEliminator()){
                                                            float cervicalAverage  = (rightRotation +leftRotation)/2;
                                                            Calculator.cervicalLeftRotation = leftRotation;
                                                            Calculator.cervicalRightRotation = rightRotation;
                                                            Log.d("FINAL CERVICAL ROTATION",String.valueOf(cervicalAverage));

                                                            Calculator.cervicalRotationScore = Calculator.getCervicalRotationScore(cervicalAverage);
                                                            Log.d("FINAL CERVICAL SCORE",String.valueOf(Calculator.cervicalRotationScore));
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
        float limit = 110;

        try{
            if(rightRotation > limit){
                toastMessage("image faulty please try again");
                rightBtn.setEnabled(true);
                rightBtn.setBackgroundColor(Color.BLACK);
                return false;
            }
            else if(leftRotation > limit){
                toastMessage("Image faulty please try again");
                leftBtn.setEnabled(true);
                leftBtn.setBackgroundColor(Color.BLACK);
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

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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