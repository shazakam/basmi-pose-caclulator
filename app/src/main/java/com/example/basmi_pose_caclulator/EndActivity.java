package com.example.basmi_pose_caclulator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
    }

    public void onClickPrintResults(View view ){
        Log.d("TRAGULAR LEFT ELBOW",String.valueOf(Calculator.tragularLeftElbow));
        Log.d("TRAGULAR RIGHT ELBOW",String.valueOf(Calculator.tragularRightElbow));
        Log.d("TRAGULAR LEFT WRIST",String.valueOf(Calculator.tragularLeftWrist));
        Log.d("TRAGULAR RIGHT WRIST",String.valueOf(Calculator.tragularRightWrist));

        Log.d("LUMBAR LEFT ELBOW",String.valueOf(Calculator.lumbarLeftElbow));
        Log.d("LUMBAR RIGHT ELBOW",String.valueOf(Calculator.lumbarRightElbow));
        Log.d("LUMBAR LEFT WRIST",String.valueOf(Calculator.lumbarLeftWrist));
        Log.d("LUMBAR RIGHT WRIST",String.valueOf(Calculator.lumbarRightWrist));

        Log.d("INTERMALLEOLAR DIST",String.valueOf(Calculator.intermalleolarDistance));

        Log.d("CERVICAL LEFT",String.valueOf(Calculator.cervicalLeftRotation));
        Log.d("CERVICAL RIGHT",String.valueOf(Calculator.cervicalRightRotation));
    }
}