package com.example.basmi_pose_caclulator;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class InputLengthsActivity extends AppCompatActivity {
    SharedPreferences sp;
    EditText indexToElbowLengthsText;
    EditText indexToWristLengthsText;
    EditText ankleToKneeLengthstext;
    Button nextInputLengths;
    ImageView exampleIndexToWrist;
    ImageView exampleIndexToElbow;
    ImageView exampleAnkleToKnee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_lengths);

        sp = getSharedPreferences("userLengths", Context.MODE_PRIVATE);
        indexToElbowLengthsText = findViewById(R.id.indexToElbowLengthsText);
        indexToWristLengthsText = findViewById(R.id.indexToWristLengthsText);
        ankleToKneeLengthstext = findViewById(R.id.ankleToKneeLengthsText);

        Bitmap indexToElbowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.louis_junk);
        Bitmap indexToWristBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.louis_junk);
        Bitmap ankleToKneeBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.louis_junk);

        exampleIndexToElbow = findViewById(R.id.indexToElbowExample);
        exampleIndexToWrist = findViewById(R.id.indexToWristExample);
        exampleAnkleToKnee = findViewById(R.id.ankleToKneeLengthExample);

        exampleIndexToElbow.setImageBitmap(indexToElbowBitmap);
        exampleIndexToWrist.setImageBitmap(indexToWristBitmap);
        exampleAnkleToKnee.setImageBitmap(ankleToKneeBitmap);

        nextInputLengths = findViewById(R.id.nextInputLengths);
        nextInputLengths.setEnabled(false);

        //Checks to see if user already has data stored
        if(sp.contains("indexToElbow") == true
        && sp.contains("indexToWrist") == true
        && sp.contains("ankleToKnee") == true){
            indexToElbowLengthsText.setText(String.valueOf(sp.getFloat("indexToElbow",-1)));
            indexToWristLengthsText.setText(String.valueOf(sp.getFloat("indexToWrist",-1)));
            ankleToKneeLengthstext.setText(String.valueOf(sp.getFloat("ankleToKnee",-1)));
            Calculator.indexToElbow = sp.getFloat("indexToElbow",-1);
            Calculator.indexToWrist = sp.getFloat("indexToWrist",-1);
            Calculator.ankleToKnee = sp.getFloat("ankleToKnee",-1);
        }
    }

    /**
     * @param view
     * Submits users measured lengths or prompts them to enter valid input in case of failure
     */
    public void onSubmitLengthsClick(View view){
            try{
                float indexToElbowValue = Float.parseFloat(indexToElbowLengthsText.getText().toString());
                float indexToWristValue = Float.parseFloat(indexToWristLengthsText.getText().toString());
                float ankleToKneeValue = Float.parseFloat(ankleToKneeLengthstext.getText().toString());
                SharedPreferences.Editor editor = sp.edit();
                editor.putFloat("indexToElbow",indexToElbowValue);
                editor.apply();
                editor.putFloat("indexToWrist",indexToWristValue);
                editor.apply();
                editor.putFloat("ankleToKnee",ankleToKneeValue);
                editor.apply();
                Calculator.indexToElbow = indexToElbowValue;
                Calculator.indexToWrist = indexToWristValue;
                Calculator.ankleToKnee = ankleToKneeValue;
                toastMessage("Lengths Submitted");
            } catch(NumberFormatException e){
                toastMessage("Please input a valid length");
            }
            nextInputLengths.setEnabled(true);
            nextInputLengths.setBackgroundColor(Color.BLACK);
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