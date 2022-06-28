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

public class TragusActivity extends AppCompatActivity{
    Button leftButton;
    Button rightButton;
    double[] leftTragular;
    double[] rightTragular;
    PoseDetector tragusPoseDetector;
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
        leftTragular = null;
        rightTragular = null;
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
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.george_left_tragular_3);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ImageView imageView = findViewById(R.id.elbowToIndexView);
                            imageView.setImageBitmap(selectedImageBitmap);
                        }
                        else{
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.george_right_tragular_1);
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
                                                            calculator.printPoses(pose);
                                                        }
                                                        else if(btnClicked == 1){
                                                            rightButton.setBackgroundColor(Color.GREEN);
                                                            btnClicked = -1;
                                                            rightTragular = calculator.tragularResult(1,pose);
                                                            rightButton.setEnabled(false);
                                                            calculator.printPoses(pose);
                                                        }
                                                        else{
                                                            toastMessage("ERROR");
                                                            return;
                                                        }

                                                        if(extremeCaseEliminator()){
                                                            try{
                                                                Calculator.tragularLeftElbow = (float) leftTragular[0];
                                                                Calculator.tragularRightElbow = (float) rightTragular[0];
                                                                Calculator.tragularLeftWrist = (float) leftTragular[1];
                                                                Calculator.tragularRightWrist = (float) rightTragular[1];
                                                                Log.d("FINAL ELBOW AVERAGE", String.valueOf((leftTragular[0]+rightTragular[0])/2));
                                                                Log.d("FINAL WRIST AVERAGE", String.valueOf((leftTragular[1]+rightTragular[1])/2));
                                                                Log.d("FINAL ELBOW SCORE", String.valueOf(calculator.tragularScore((leftTragular[0]+rightTragular[0])/2)));
                                                                Log.d("FINAL WRIST SCORE", String.valueOf(calculator.tragularScore((leftTragular[1]+rightTragular[1])/2)));
                                                                Calculator.tragusToWallScore =  calculator.tragularScore((leftTragular[0]+leftTragular[1]+rightTragular[0]+rightTragular[1])/4);
                                                                Log.d("FINAL TRAGULAR SCORE", String.valueOf(Calculator.tragusToWallScore));
                                                            }catch(Exception e){

                                                            }
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
        try{
            if (leftTragular[0] >= 45 || leftTragular[1] >= 45 || rightTragular[0] >= 45 || rightTragular[1] >= 45) {
                toastMessage("Image result faulty, reload image again please");
                if ((leftTragular[0] >= 45 || leftTragular[1] >= 45) && (rightTragular[0] >= 45 || rightTragular[1] >= 45)) {
                    leftButton.setEnabled(true);
                    leftButton.setBackgroundColor(Color.BLACK);
                    rightButton.setEnabled(true);
                    rightButton.setBackgroundColor(Color.BLACK);
                }
                else if(leftTragular[0] >= 45 || leftTragular[1] >= 45){
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
        }catch (Exception e){
            return false;
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
        leftTragular = null;
        rightTragular = null;
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