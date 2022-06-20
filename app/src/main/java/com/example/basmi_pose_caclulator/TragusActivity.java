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
import android.widget.TextView;
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

    //URI of images needs to be stored from OnClickTragusImage
    //Note to self may not be URI but BitMap instead
    //MVP = Minimun Viable Product
    Boolean leftButtonClicked = false;
    Boolean rightButtonClicked = false;
    Button leftButton;
    Button rightButton;
    double leftTragular;
    double rightTragular;
    static double tragularSum;
    static int tragularCount;
    EditText indexToElbowText;
    PoseDetector tragusPoseDetector;
    SharedPreferences sp;
    AccuratePoseDetectorOptions options =
            new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tragus);

        //Initialising all the views, buttons and values
        leftButton = findViewById(R.id.btnLeftUploadTragus);
        rightButton = findViewById(R.id.btnRightUploadTragus);
        indexToElbowText = findViewById(R.id.indexToElbowInput);
        leftTragular = 0;
        rightTragular = 0;
        tragularSum = 0;

        //Used to store user data
        sp = getSharedPreferences("userLengths",Context.MODE_PRIVATE);

        //Checks to see if user already has data stored
        if(sp.contains("indexToElbow") == true){
            indexToElbowText.setText(String.valueOf(sp.getInt("indexToElbow",-1)));
            Calculator.indexToElbow = sp.getInt("indexToElbow",-1);
        }
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
                        if(rightButtonClicked){
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.tragular_right);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                            ImageView imageView = findViewById(R.id.elbowToIndexView);
                            imageView.setImageBitmap(selectedImageBitmap);
                        }

                        else{
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.tragular_left);
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

                                                        if(leftButtonClicked){
                                                            //UI Change, info to see what is being executed
                                                            Log.d("TRUE","BUTTON LEFT CLICKED");
                                                            leftButton.setBackgroundColor(Color.GREEN);
                                                            leftButtonClicked = false;
                                                            leftButton.setEnabled(false);
                                                            calculator.printPoses(pose);
                                                            //Uses calculator to find distance to tragular with the input pose (0 indicates left input)
                                                            leftTragular = calculator.tragularResult(0,pose);
                                                            //Used to stop any extreme measurements from being used as the final result
                                                            extremeCaseEliminator(leftTragular, leftButton);
                                                        }

                                                        else{
                                                            rightButton.setBackgroundColor(Color.GREEN);
                                                            rightButtonClicked = false;
                                                            rightTragular = calculator.tragularResult(1,pose);
                                                            rightButton.setEnabled(false);
                                                            calculator.printPoses(pose);
                                                            extremeCaseEliminator(rightTragular, rightButton);
                                                        }

                                                        if(checkForFinalResult()){
                                                            Log.d("FINAL AVERAGE", String.valueOf(tragularSum/2));
                                                            Log.d("FINAL TRAGULAR SCORE", String.valueOf(calculator.tragularScore(tragularSum/2)));
                                                            Calculator.tragusToWall =  calculator.tragularScore(tragularSum/2);
                                                            TextView tragularScoreView = findViewById(R.id.tragularScoreValue);
                                                            tragularScoreView.setText(String.valueOf(Calculator.tragusToWall));
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
    public void extremeCaseEliminator(double tragular, View view) {

        if (tragular >= 45) {

            toastMessage("Image result faulty, reload image again please");
            view.setEnabled(true);
        } else {
            tragularSum += tragular;
        }
        tragusPoseDetector.close();
    }

    //Checks to see if a final result can be calculated
    public boolean checkForFinalResult(){
        if(rightTragular != 0 && leftTragular != 0){
            return true;
        }
        else{
            return false;
        }
    }

    /*BUTTONS*/
    public void onClickTragusImageBtnOne(View view) {

        int buttonId = view.getId();
        if(buttonId == R.id.btnRightUploadTragus){
            Log.d("TRUE","BUTTON RIGHT CLICKED");
            rightButtonClicked = true;
        }

        else if(buttonId == R.id.btnLeftUploadTragus){
            Log.d("FALSE","BUTTON LEFT CLICKED");
            leftButtonClicked = true;
        }

        //Intent to take Photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImageTragular.launch(intent);
    }

    public void onSubmitClick(View view){

        int indexToElbowValue = Integer.parseInt(indexToElbowText.getText().toString());
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("indexToElbow",indexToElbowValue);
        editor.apply();
        Calculator.indexToElbow = indexToElbowValue;

        toastMessage("Lengths Submitted");
    }

    public void onTragusNextClick(View view){
        Intent intent = new Intent(this, LumbarActivity.class);
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