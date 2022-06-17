package com.example.basmi_pose_caclulator;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText indexToElbowText;
    Button sumbmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSubmitClick(View view){

        indexToElbowText = findViewById(R.id.indexToElbowInput);
        int indexToElbowValue = Integer.valueOf(indexToElbowText.getText().toString());
        Calculator.indexToElbow = indexToElbowValue;
    }

    //When button is clicked it sends to the application to ImageActivity
    public void onTitleClick(View view){
            Intent intent = new Intent(this, TragusActivity.class);
            startActivity(intent);
    }
}