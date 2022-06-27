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
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class TragusActivity extends AppCompatActivity{
    Button leftButton;
    Button rightButton;
    double leftTragular;
    double rightTragular;
    EditText indexToElbowText;
    PoseDetector tragusPoseDetector;
    SharedPreferences sp;
    AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();
    //0=left,1=right, any other number indicates no clicking
    int btnClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tragus);

        //Initialising all the views, buttons and values
        leftButton = findViewById(R.id.btnLeftUploadTragus);
        rightButton = findViewById(R.id.btnRightUploadTragus);
        leftTragular = 0;
        rightTragular = 0;
        btnClicked = -1;

    }

    //This tells what getImage should do with the result from the intent
    ActivityResultLauncher<Intent> getImageTragular = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //Initialises pose detector with desired options
                        tragusPoseDetector = PoseDetection.getClient(options);
                        /* Taking Picture (Currently using predefined images)
                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageBitmap(selectedImageBitmap);*/
                        Bitmap selectedImageBitmap;
                        InputImage inputImage;

                        //This if-else statement is just used for pre-loaded images and will be removed for when photos need to be uploaded
                        if(btnClicked == 0){
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.anna_left_tragular_4);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ImageView imageView = findViewById(R.id.elbowToIndexView);
                            imageView.setImageBitmap(selectedImageBitmap);
                        }
                        else{
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.anna_right_tragular_1);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ImageView imageView = findViewById(R.id.elbowToIndexView);
                            imageView.setImageBitmap(selectedImageBitmap);
                        }

                        Task<Pose> poseResult =
                                tragusPoseDetector.process(inputImage)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Pose>() {
                                                    @Override
                                                    public void onSuccess(Pose pose) {

                                                        Calculator calculator = new Calculator();

                                                        if(btnClicked == 0){
                                                            //UI Change, info to see what is being executed
                                                            leftButton.setBackgroundColor(Color.GREEN);
                                                            btnClicked = -1;
                                                            leftTragular = calculator.tragularResult(0,pose);
                                                            leftButton.setEnabled(false);
                                                            Log.d("LEFT TRAGULAR RESULT",String.valueOf(leftTragular));
                                                            calculator.printPoses(pose);

                                                        }

                                                        else if(btnClicked == 1){
                                                            rightButton.setBackgroundColor(Color.GREEN);
                                                            btnClicked = -1;
                                                            rightTragular = calculator.tragularResult(1,pose);
                                                            rightButton.setEnabled(false);
                                                            Log.d("RIGHT TRAGULAR RESULT",String.valueOf(rightTragular));
                                                            calculator.printPoses(pose);
                                                        }
                                                        else{
                                                            toastMessage("ERROR");
                                                            return;
                                                        }

                                                        if(extremeCaseEliminator()){
                                                            Log.d("FINAL AVERAGE", String.valueOf((leftTragular+rightTragular)/2));
                                                            Log.d("FINAL TRAGULAR SCORE", String.valueOf(calculator.tragularScore((leftTragular+rightTragular)/2)));
                                                            Calculator.tragusToWallScore =  calculator.tragularScore((leftTragular+rightTragular)/2);
                                                        }
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

    //Stops any ridiculous results being used
    public boolean extremeCaseEliminator() {
        if (leftTragular >= 45 || rightTragular >= 45) {
            toastMessage("Image result faulty, reload image again please");
            if (leftTragular >= 45 && rightTragular >= 45) {
                leftButton.setEnabled(true);
                leftButton.setBackgroundColor(Color.BLACK);
                rightButton.setEnabled(true);
                rightButton.setBackgroundColor(Color.BLACK);
            }
            else if(leftTragular >= 45){
                leftButton.setEnabled(true);
                leftButton.setBackgroundColor(Color.BLACK);
            }
            else{
                rightButton.setEnabled(true);
                rightButton.setBackgroundColor(Color.BLACK);
            }
            tragusPoseDetector.close();
            return false;
        }
        else{
            return true;
        }
    }


    /*BUTTONS*/
    public void onClickTragusImageBtnOne(View view) {
        int buttonId = view.getId();
        if(buttonId == R.id.btnRightUploadTragus){
            Log.d("1","BUTTON RIGHT CLICKED");
            btnClicked = 1;
        }
        else if(buttonId == R.id.btnLeftUploadTragus){
            Log.d("0","BUTTON LEFT CLICKED");
            btnClicked = 0;
        }
        else{
            Log.d("ERROR","ON CLICK TRAGUS NOT WORKING");
        }

        //Intent to take Photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImageTragular.launch(intent);
    }

    public void onTragusNextClick(View view){
        Intent intent = new Intent(this, LumbarActivity.class);
        startActivity(intent);
    }

    public void onRetakeClick(View view){
        leftTragular = 0;
        rightTragular = 0;
        Calculator.tragusToWallScore = 0;
        leftButton.setEnabled(true);
        leftButton.setBackgroundColor(Color.BLACK);
        rightButton.setEnabled(true);
        rightButton.setBackgroundColor(Color.BLACK);
    }

    public void toastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}