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
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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


public class TragusActivity extends AppCompatActivity {

    //URI of images needs to be stored from OnClickTragusImage
    //Note to self may not be URI but BitMap instead
    //MVP = Minimun Viable Product
    Pose poseOne = null;
    Pose poseTwo = null;

    double myLIndexLWrist = 25;

    PoseDetector tragusPoseDetector;

    AccuratePoseDetectorOptions options =
            new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tragus);

        //Initialise pose detector with defined options above
        //tragusPoseDetector = PoseDetection.getClient(options);
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
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 90);
                        Task<Pose> poseResult =
                                tragusPoseDetector.process(inputImage)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Pose>() {
                                                    @Override
                                                    public void onSuccess(Pose pose) {

                                                        if(poseOne == null){
                                                            poseOne = pose;
                                                        }
                                                        else if(poseTwo == null) {
                                                            poseTwo = pose;
                                                        }

                                                        //Calculate distance between tragus and index (test is for between L.I to R.I)
                                                        float leftItorightI = euclideanDistance(pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition3D(),
                                                                pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition3D());

                                                        double ratio = myLIndexLWrist/euclideanDistance(pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition3D(),
                                                                pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition3D());

                                                        double final_result = ratio*leftItorightI;

                                                        Log.d("FINAL BLOODY RESULT: ",String.valueOf(final_result));
                                                        Log.d("RATIO",String.valueOf(ratio));
                                                        Log.d("Distance",String.valueOf(euclideanDistance(pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition3D(),
                                                                        pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition3D())));
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


    public static float euclideanDistance(PointF3D firstPoint, PointF3D secondPoint) {

        return (float) Math.sqrt(//Math.pow((secondPoint.getX() - firstPoint.getX()),2)
                //+Math.pow((secondPoint.getY() - firstPoint.getY()),2)
                Math.pow((secondPoint.getZ() - firstPoint.getZ()),2));
    }

    public void onClickTragusImageBtnOne(View view) {
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