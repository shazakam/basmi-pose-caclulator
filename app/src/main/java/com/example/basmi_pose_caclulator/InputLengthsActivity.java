package com.example.basmi_pose_caclulator;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputLengthsActivity extends AppCompatActivity {
    SharedPreferences sp;
    EditText indexToElbowLengthsText;
    EditText indexToWristLengthsText;
    EditText ankleToKneeLengthstext;
    Button nextInputLengths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_lengths);
        sp = getSharedPreferences("userLengths", Context.MODE_PRIVATE);

        indexToElbowLengthsText = findViewById(R.id.indexToElbowLengthsText);
        indexToWristLengthsText = findViewById(R.id.indexToWristLengthsText);
        ankleToKneeLengthstext = findViewById(R.id.ankleToKneeLengthsText);
        nextInputLengths = findViewById(R.id.nextInputLengths);
        nextInputLengths.setEnabled(false);

        //Checks to see if user already has data stored
        if(sp.contains("indexToElbow") == true
        && sp.contains("indexToWrist") == true
        && sp.contains("ankleToKnee") == true){
            indexToElbowLengthsText.setText(String.valueOf(sp.getInt("indexToElbow",-1)));
            indexToWristLengthsText.setText(String.valueOf(sp.getInt("indexToWrist",-1)));
            ankleToKneeLengthstext.setText(String.valueOf(sp.getInt("ankleToKnee",-1)));
            Calculator.indexToElbow = sp.getInt("indexToElbow",-1);
            Calculator.indexToWrist = sp.getInt("indexToWrist",-1);
            Calculator.ankleToKnee = sp.getInt("ankleToKnee",-1);
        }
    }

    public void onSubmitLengthsClick(View view){
            try{
                int indexToElbowValue = Integer.parseInt(indexToElbowLengthsText.getText().toString());
                int indexToWristValue = Integer.parseInt(indexToWristLengthsText.getText().toString());
                int ankleToKneeValue = Integer.parseInt(ankleToKneeLengthstext.getText().toString());
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("indexToElbow",indexToElbowValue);
                editor.apply();
                editor.putInt("indexToWrist",indexToWristValue);
                editor.apply();
                editor.putInt("ankleToKnee",ankleToKneeValue);
                editor.apply();
                Calculator.indexToElbow = indexToElbowValue;
                Calculator.indexToWrist = indexToWristValue;
                Calculator.ankleToKnee = ankleToKneeValue;
                toastMessage("Lengths Submitted");
            } catch(NumberFormatException e){
                toastMessage("Please input a valid length");
            }
            nextInputLengths.setEnabled(true);
        }

    public void onNextLengthsClick(View view){
        Intent intent = new Intent(this, TragusActivity.class);
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