package com.example.basmi_pose_caclulator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
    }

    public void onClickPrintResults(View view ) throws IOException {
        /*
        Log.d("TRAGULAR LEFT ELBOW",String.valueOf(Calculator.tragularLeftElbow));
        Log.d("TRAGULAR RIGHT ELBOW",String.valueOf(Calculator.tragularRightElbow));
        Log.d("TRAGULAR LEFT WRIST",String.valueOf(Calculator.tragularLeftWrist));
        Log.d("TRAGULAR RIGHT WRIST",String.valueOf(Calculator.tragularRightWrist));*/

        Log.d("PHYSICAL MEASUREMENTS","START");
        Log.d("TRAGULAR RIGHT",String.valueOf(Calculator.rightTragular));
        Log.d("TRAGULAR LEFT",String.valueOf(Calculator.leftTragular));
        Log.d("LUMBAR RIGHT NEUTRAL",String.valueOf(Calculator.rightLumbarNeutral));
        Log.d("LUMBAR LEFT NEUTRAL",String.valueOf(Calculator.leftLumbarNeutral));
        Log.d("RIGHT LUMBAR EXTENSION",String.valueOf(Calculator.rightLumbarExtension));
        Log.d("LEFT LUMBAR EXTENSION",String.valueOf(Calculator.leftLumbarExtension));
        Log.d("INTERMALLEOLAR",String.valueOf(Calculator.intermalleolar));
        Log.d("CERVICAL RIGHT ROTATION",String.valueOf(Calculator.cervicalRight));
        Log.d("CERVICAL LEFT ROTATION",String.valueOf(Calculator.cervicalLeft));
        Log.d("FLEXION",String.valueOf(Calculator.flexion));
        Log.d("PHYSICAL MEASUREMENTS","END");

        Calculator.printPoses(Calculator.tragularLeftPose,"TRAGULAR LEFT");
        Calculator.printPoses(Calculator.tragularRightPose,"TRAGULAR RIGHT");
        /*
        Log.d("LUMBAR LEFT ELBOW",String.valueOf(Calculator.lumbarLeftElbow));
        Log.d("LUMBAR RIGHT ELBOW",String.valueOf(Calculator.lumbarRightElbow));
        Log.d("LUMBAR LEFT WRIST",String.valueOf(Calculator.lumbarLeftWrist));
        Log.d("LUMBAR RIGHT WRIST",String.valueOf(Calculator.lumbarRightWrist));*/

        Calculator.printPoses(Calculator.lumbarNeutralPose,"LUMBAR NEUTRAL");
        Calculator.printPoses(Calculator.lumbarLeftPose,"LUMBAR LEFT");
        Calculator.printPoses(Calculator.lumbarRightPose,"LUMBAR RIGHT");

        //Log.d("INTERMALLEOLAR DIST",String.valueOf(Calculator.intermalleolarDistance));
        Calculator.printPoses(Calculator.intermalleolarPose,"INTERMALLEOLAR POSE");
        /*
        Log.d("CERVICAL LEFT",String.valueOf(Calculator.cervicalLeftRotation));
        Log.d("CERVICAL RIGHT",String.valueOf(Calculator.cervicalRightRotation));*/

        Calculator.printPoses(Calculator.cervicalNeutralFace,"CERVICAL NEUTRAL");
        Calculator.printPoses(Calculator.cervicalLeftFace,"CERVICAL LEFT");
        Calculator.printPoses(Calculator.cervicalRightFace,"CERVICAL RIGHT");


        Calculator.printPoses(Calculator.flexionLeftNeutralPose,"FLEXION LEFT NEUTRAL");
        Calculator.printPoses(Calculator.flexionLeftExtensionPose,"FLEXION LEFT EXTENSION");
        Calculator.printPoses(Calculator.flexionRightNeutralPose,"FLEXION RIGHT NEUTRAL");
        Calculator.printPoses(Calculator.flexionRightExtensionPose,"FLEXION RIGHT EXTENSION");
    }
}