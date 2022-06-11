package com.example.basmi_pose_caclulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //When button is clicked it sends to the application to ImageActivity
    public void onTitleClick(View view){
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }
}