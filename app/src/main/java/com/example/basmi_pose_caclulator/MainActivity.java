package com.example.basmi_pose_caclulator;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText indexToElbowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.titleBtn).setEnabled(false);
    }

    public void onSubmitClick(View view){

        indexToElbowText = findViewById(R.id.indexToElbowInput);
        int indexToElbowValue = Integer.valueOf(indexToElbowText.getText().toString());
        Calculator.indexToElbow = indexToElbowValue;

        Context context = getApplicationContext();
        CharSequence text = "Lengths Submitted!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        findViewById(R.id.titleBtn).setEnabled(true);
    }

    //When button is clicked it sends to the application to ImageActivity
    public void onTitleClick(View view){
            Intent intent = new Intent(this, TragusActivity.class);
            startActivity(intent);
    }
}