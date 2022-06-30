package com.example.basmi_pose_caclulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LumbarFlexionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumbar_flexion);
    }

    public void onLumbarFlexionNextClick(View view){
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }

    /*NOT YET COMPLETED*/
}