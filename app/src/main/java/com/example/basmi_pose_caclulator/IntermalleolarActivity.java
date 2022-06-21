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

public class IntermalleolarActivity extends AppCompatActivity {
    EditText heelToKneeText;
    PoseDetector intermalleolarPoseDetector;
    SharedPreferences sp;
    AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermalleolar);

        //Used to store user data
        sp = getSharedPreferences("userLengths",Context.MODE_PRIVATE);

        //Checks to see if user already has data stored
        Log.d("DATA STORED", String.valueOf(sp.getInt("ankleToKnee",-1)));
        /*
        if(sp.contains("ankleToKnee") == true){
            heelToKneeText.setText(String.valueOf(sp.getInt("ankleToKnee",-1)));
            Calculator.indexToElbow = sp.getInt("ankleToKnee",-1);
        }*/
    }

    ActivityResultLauncher<Intent> getImageTragular = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //What should be done once the result from the intent has been received
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //Initialises pose detector with desired options
                        intermalleolarPoseDetector = PoseDetection.getClient(options);
                        //Taking Picture (Currently using predefined images)
                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        ImageView imageView = findViewById(R.id.intermalleolarExample);
                        imageView.setImageBitmap(selectedImageBitmap);

                        Task<Pose> poseResult =
                                intermalleolarPoseDetector.process(inputImage)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Pose>() {
                                                    @Override
                                                    public void onSuccess(Pose pose) {

                                                        Calculator calculator = new Calculator();
                                                        float intermalleolarResult = calculator.getIntermalleolarResult(pose);
                                                        calculator.intermalleolarDist = calculator.intermalleolarScore(intermalleolarResult);
                                                        TextView intermalleolarResultView = findViewById(R.id.intermalleolarScore);
                                                        intermalleolarResultView.setText(String.valueOf(calculator.intermalleolarDist));
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

    public void onClickIntermalleolarUpload(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImageTragular.launch(intent);
    }

    public void onSubmitHeelToKneeClick(View view){
        int ankleToKnee = 2; //Integer.parseInt(heelToKneeText.getText().toString());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("ankleToKnee",ankleToKnee);
        editor.apply();
        Calculator.ankleToKnee = ankleToKnee;
        toastMessage("Lengths Submitted");
    }

    public void toastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}