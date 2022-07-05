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

public class LumbarFlexionActivity extends AppCompatActivity {
    int btnClicked = -2;
    Button neutralRightBtn;
    Button neutralLeftBtn;
    Button rightFlexionBtn;
    Button leftFlexionBtn;
    PoseDetector flexionPoseDetector;
    AccuratePoseDetectorOptions options =
            new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();

    PointF neutralLeftHip;
    PointF neutralLeftShoulder;
    PointF aOneLeft;
    PointF neutralRightHip;
    PointF neutralRightShoulder;
    PointF aOneRight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumbar_flexion);
        leftFlexionBtn = findViewById(R.id.btnFlexionLeftUpload);
        rightFlexionBtn = findViewById(R.id.btnFlexionRightUpload);
        neutralRightBtn = findViewById(R.id.btnFlexionRightNeutralUpload);
        neutralLeftBtn = findViewById(R.id.btnFlexionLeftNeutralUpload);

        ImageView flexionNeutralExample = findViewById(R.id.flexionNeutralExample);
        ImageView flexionExtensionExample = findViewById(R.id.flexionExtensionExample);

        Bitmap flexionNeutralBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.louis_junk);
        Bitmap flexionExtensionBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.louis_junk);

        flexionNeutralExample.setImageBitmap(flexionNeutralBitmap);
        flexionExtensionExample.setImageBitmap(flexionExtensionBitmap);

    }

    public void onLumbarFlexionNextClick(View view){
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }

    ActivityResultLauncher<Intent> getImageFlexion = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //Initialises pose detector with desired options
                        flexionPoseDetector = PoseDetection.getClient(options);
                        //Taking Picture (Currently using predefined images)

                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);

                        //Used for pre-loaded images and will be removed for when photos need to be uploaded
                        //-1 means the left lumbar extension, 0 indicates the neutral position and 1 indicates the right lumbar extension
                        /*
                        Bitmap selectedImageBitmap;
                        InputImage inputImage;
                        if(btnClicked == -1){
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),ENTER LEFT EXTENSION DRAWABLE HERE);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        }
                        //Neutral Clicked
                        else if(btnClicked == 0){
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),ENTER NEUTRAL POSITION DRAWABLE HERE);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        }
                        //Right Clicked
                        else{
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),ENTER RIGHT EXTENSION DRAWABLE HERE);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        }*/

                        Task<Pose> poseResult =
                                flexionPoseDetector.process(inputImage)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Pose>() {
                                                    @Override
                                                    public void onSuccess(Pose pose) {
                                                        Calculator.printPoses(pose);

                                                        //LEFT NEUTRAL
                                                        if(btnClicked == -1){
                                                            Log.d("TRUE","BUTTON LEFT NEUTRAL CLICKED");
                                                            neutralLeftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition();
                                                            neutralLeftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition();
                                                            aOneLeft = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
                                                            neutralLeftBtn.setBackgroundColor(Color.GREEN);
                                                            neutralLeftBtn.setEnabled(false);
                                                        }

                                                        //RIGHT NEUTRAL
                                                        else if(btnClicked == 0){
                                                            Log.d("TRUE","BUTTON RIGHT NEUTRAL CLICKED");
                                                            neutralRightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition();
                                                            neutralRightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition();
                                                            neutralRightBtn.setBackgroundColor(Color.GREEN);
                                                            aOneRight = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition();
                                                            neutralRightBtn.setEnabled(false);
                                                        }
                                                        //LEFT FLEXION
                                                        else if(btnClicked == 1){
                                                            Log.d("TRUE","BUTTON LEFT FLEXION CLICKED");
                                                            leftFlexionBtn.setBackgroundColor(Color.GREEN);
                                                            leftFlexionBtn.setEnabled(false);
                                                            Calculator.flexionLeft = (float) Calculator.getFlexionResult(pose,neutralLeftHip,neutralLeftShoulder,1);
                                                            Log.d("FlEXION L 1",String.valueOf(Calculator.getFlexionResult(pose,neutralLeftHip,neutralLeftShoulder,1)));
                                                            Log.d("FLEXION L 2",String.valueOf(Calculator.getFlexionResultTwo(pose,aOneLeft,neutralLeftHip,1)));
                                                            Log.d("FLEXION L 3",String.valueOf(Calculator.getFlexionResultThree(pose,neutralLeftHip,aOneLeft,1)));
                                                            Log.d("FLEXION L 4",String.valueOf(Calculator.getFlexionResultFour(pose,neutralLeftHip,neutralLeftShoulder,1)));
                                                            Log.d("FLEXION L 5",String.valueOf(Calculator.getFlexionResultFive(pose,neutralLeftHip,aOneLeft,1)));
                                                        }
                                                        //RIGHT FLEXION
                                                        else{
                                                            Log.d("TRUE","BUTTON RIGHT FLEXION CLICKED");
                                                            rightFlexionBtn.setBackgroundColor(Color.GREEN);
                                                            rightFlexionBtn.setEnabled(false);
                                                            Calculator.flexionRight = (float) Calculator.getFlexionResult(pose,neutralRightHip,neutralRightShoulder,2);
                                                            Log.d("FlEXION R 1",String.valueOf(Calculator.getFlexionResult(pose,neutralRightHip,neutralRightShoulder,2)));
                                                            Log.d("FLEXION R 2",String.valueOf(Calculator.getFlexionResultTwo(pose,aOneRight,neutralRightHip,2)));
                                                            Log.d("FLEXION R 3",String.valueOf(Calculator.getFlexionResultThree(pose,neutralRightHip,aOneRight,2)));
                                                            Log.d("FLEXION R 4",String.valueOf(Calculator.getFlexionResultFour(pose,neutralRightHip,neutralRightShoulder,2)));
                                                            Log.d("FLEXION R 5",String.valueOf(Calculator.getFlexionResultFive(pose,neutralRightHip,aOneRight,2)));
                                                        }

                                                        if(!(neutralRightBtn.isEnabled()) && !(neutralLeftBtn.isEnabled()) &&
                                                        !(rightFlexionBtn.isEnabled()) && !(leftFlexionBtn.isEnabled())){
                                                            Calculator.flexionScore = Calculator.getFlexionScore((Calculator.flexionRight+Calculator.flexionLeft)/2);
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

    public void onBtnUploadClick(View view){
        int btnId = view.getId();

        if(btnId == R.id.btnFlexionLeftNeutralUpload){
            btnClicked = -1;
            Log.d("BUTTON CLICKED","-1");
        }
        else if(btnId == R.id.btnFlexionRightNeutralUpload){
            btnClicked = 0;
            Log.d("BUTTON CLICKED","0");
        }
        else if(btnId == R.id.btnFlexionLeftUpload){
            btnClicked = 1;
            Log.d("BUTTON CLICKED","1");
        }
        else if(btnId == R.id.btnFlexionRightUpload){
            btnClicked = 2;
            Log.d("BUTTON CLICKED","2");
        }
        else{
            toastMessage("ERROR WITH BUTTON CHOSEN: " + String.valueOf(btnId));
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImageFlexion.launch(intent);
    }

    public void toastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}