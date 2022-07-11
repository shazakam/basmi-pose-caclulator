package com.example.basmi_pose_caclulator;
import static java.lang.Math.max;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityManager;
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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TragusActivity extends AppCompatActivity{
    Button leftButton;
    Button rightButton;
    /*The following two store the measured results for the right side and left side.
    First the [0] position in both leftTragular and rightTragular is the result calculated
    using the index to elbow length whilst [1] are the results calculated using the
    index to wrist length as the reference.*/
    double[] leftTragular;
    double[] rightTragular;
    private GraphicOverlay graphicOverlayRight;
    private GraphicOverlay graphicOverlayLeft;
    PoseDetector tragusPoseDetector;
    AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();
    //0=left,1=right, any other number indicates no clicking
    int btnClicked;
    ImageView leftPicture;
    ImageView rightPicture;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();


    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tragus);
        try {
            Log.d("RUNNING","RUNNING");
            post("http://138.38.102.31:5000/debug","HIHIHIHI");
        } catch (Exception e) {
            Log.d("WTF NO SERVER","WTF");
            e.printStackTrace();
        }

        //Initialising all the views, buttons and values
        graphicOverlayRight = findViewById(R.id.graphicOverlayRight);
        graphicOverlayLeft = findViewById(R.id.graphicOverlayLeft);
        leftButton = findViewById(R.id.btnLeftUploadTragus);
        rightButton = findViewById(R.id.btnRightUploadTragus);
        leftPicture = findViewById(R.id.tragularLeftExample);
        rightPicture = findViewById(R.id.tragularRightExample);
        leftTragular = null;
        rightTragular = null;
        btnClicked = -1;

    }


    //This tells what getImage should do with the result from the intent
    ActivityResultLauncher<Intent> getImageTragular = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override

                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //Initialises pose detector with desired options
                        tragusPoseDetector = PoseDetection.getClient(options);
                        /* Taking Picture */
                        /*
                        Bundle extras = result.getData().getExtras();
                        Bitmap selectedImageBitmap = (Bitmap) extras.get("data");
                        InputImage inputImage = InputImage.fromBitmap(selectedImageBitmap,0);*/

                        //Used for pre-loaded images and will be removed for when photos need to be uploaded
                        //0 means the left side is being uploaded and 1 indicates the right side is being uploaded

                        Bitmap selectedImageBitmap;
                        InputImage inputImage;
                        if(btnClicked == 0){
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left_tragular_6);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        }
                        else{
                            selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.right_tragular_2);
                            inputImage = InputImage.fromBitmap(selectedImageBitmap,0);
                        }

                        //If the pose detector is successful it executes onSuccess
                        OnSuccessListener<Pose> tragusOnSuccess = new OnSuccessListener<Pose>() {
                            @Override
                            public void onSuccess(Pose pose) {
                                if(btnClicked == 0){
                                    leftButton.setBackgroundColor(Color.GREEN);
                                    leftTragular = Calculator.tragularResult(0,pose);
                                    leftButton.setEnabled(false);
                                    Calculator.tragularLeftPose = pose;
                                    Calculator.printPoses(pose);
                                    toastMessage("Upload Successful");
                                }
                                else if(btnClicked == 1){
                                    rightButton.setBackgroundColor(Color.GREEN);
                                    rightTragular = Calculator.tragularResult(1,pose);
                                    rightButton.setEnabled(false);
                                    Calculator.tragularRightPose = pose;
                                    Calculator.printPoses(pose);
                                    toastMessage("Upload Successful");
                                }
                                else{
                                    toastMessage("ERROR");
                                    return;
                                }
                                tryReloadAndDetectInImage(selectedImageBitmap,pose);
                                if(extremeCaseEliminator()){
                                    try{

                                        Calculator.tragularLeftElbow = (float) leftTragular[0];
                                        Calculator.tragularRightElbow = (float) rightTragular[0];
                                        Calculator.tragularLeftWrist = (float) leftTragular[1];
                                        Calculator.tragularRightWrist = (float) rightTragular[1];
                                        Log.d("FINAL ELBOW AVERAGE", String.valueOf((leftTragular[0]+rightTragular[0])/2));
                                        Log.d("FINAL WRIST AVERAGE", String.valueOf((leftTragular[1]+rightTragular[1])/2));
                                        Log.d("FINAL ELBOW SCORE", String.valueOf(Calculator.tragularScore((leftTragular[0]+rightTragular[0])/2)));
                                        Log.d("FINAL WRIST SCORE", String.valueOf(Calculator.tragularScore((leftTragular[1]+rightTragular[1])/2)));
                                        Calculator.tragusToWallScore =  Calculator.tragularScore((leftTragular[0]+leftTragular[1]+rightTragular[0]+rightTragular[1])/4);
                                        Log.d("FINAL TRAGULAR SCORE", String.valueOf(Calculator.tragusToWallScore));
                                    }catch(Exception e){

                                    }
                                }
                            }
                        };

                        Task<Pose> poseResult = tragusPoseDetector.process(inputImage).addOnSuccessListener(tragusOnSuccess).addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        toastMessage("Upload Image Again");
                                                    }
                                                });


                    }
                }
            }
    );


    public void processBitmap(Bitmap bitmap, final GraphicOverlay graphicOverlay) {
        graphicOverlay.add(new CameraImageGraphic(graphicOverlay, bitmap));
    }

    public Bitmap resizeBitmap(Bitmap inputBitmap,ImageView view){
        if (inputBitmap.getWidth() == view.getWidth()
                && inputBitmap.getHeight() == view.getHeight()) {
            return inputBitmap;
        } else {
            // Determine how much to scale down the image
            float scaleFactor =
                    max((float) inputBitmap.getWidth() / (float) view.getWidth(),
                            (float) inputBitmap.getHeight() / (float) view.getHeight());

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(inputBitmap,
                            (int) (inputBitmap.getWidth() / scaleFactor),
                            (int) (inputBitmap.getHeight() / scaleFactor),
                            true);
            return resizedBitmap;
        }
    }


    private void tryReloadAndDetectInImage(Bitmap imageBitmap, Pose pose) {
        ImageView imageFrame;
        GraphicOverlay theOverlay;
        try {
            if(btnClicked == 0){
                imageFrame = findViewById(R.id.tragularLeftExample);
                graphicOverlayLeft.clear();
                theOverlay = findViewById(R.id.graphicOverlayLeft);
            }
            else{
                imageFrame = findViewById(R.id.tragularRightExample);
                graphicOverlayRight.clear();
                theOverlay = findViewById(R.id.graphicOverlayRight);
            }
            Bitmap resizedBitmap = resizeBitmap(imageBitmap,imageFrame);
            imageFrame.setImageBitmap(resizedBitmap);

            if (pose != null) {
                theOverlay.setImageSourceInfo(
                        resizedBitmap.getWidth(), resizedBitmap.getHeight(),false);
                processBitmap(resizedBitmap, theOverlay);
            } else {
                Log.e("ERROR", "Null imageProcessor, please check adb logs for imageProcessor creation error");
            }
        } catch (Exception e) {
            Log.e("ERROR", "Error retrieving saved image");
        }
    }

    /**
     * Checks to see if any clearly false/outlier results are being calculated and returns false
     * if one of the uploaded images is faulty and resets the relevant button for the user to re-upload
     * a photo.
     * @return
     */
    public boolean extremeCaseEliminator() {
        float limit = 45;
        try{
            if (leftTragular[0] >= limit || leftTragular[1] >= limit || rightTragular[0] >= limit || rightTragular[1] >= limit) {
                toastMessage("Image result faulty, reload image again please");
                if ((leftTragular[0] >= limit || leftTragular[1] >= limit) && (rightTragular[0] >= limit || rightTragular[1] >= limit)) {
                    leftButton.setEnabled(true);
                    leftButton.setBackgroundColor(Color.BLACK);
                    rightButton.setEnabled(true);
                    rightButton.setBackgroundColor(Color.BLACK);
                }
                else if(leftTragular[0] >= limit || leftTragular[1] >= limit){
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

    public void toastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}