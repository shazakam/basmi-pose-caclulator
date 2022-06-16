package com.example.basmi_pose_caclulator;

import android.graphics.PointF;
import android.util.Log;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class Calculator {

    //Final results obtained from each activity
    static float tragusToWall = 0;
    static float lumbarSideFlexion = 0;
    static float getLumbarSideFlexionSchober = 0;
    static float cervicalRotation = 0;
    static float intermalleolarDist = 0;

    public Calculator(){
    }


    public static float getDistance(PointF firstPoint, PointF secondPoint, double ratio) {

        //Calculate the vector from firstPoint to secondPoint and return its length
        float xCoord= (float) (firstPoint.x - secondPoint.x);
        float yCoord = (float) (firstPoint.y - secondPoint.y);

        Log.d("X Coordinate",String.valueOf(xCoord));
        Log.d("Y Coordinate",String.valueOf(yCoord));

        PointF finalVector = new PointF();
        finalVector.set(xCoord,yCoord);
        Log.d("VECTOR LENGTH",String.valueOf(finalVector.length()));

        return (float) finalVector.length();
    }

    public static double tragularResult(int buttonClicked, Pose pose, Double indexToWristDist){
        double finalTragularDist;
        //If the left button was clicked
        if(buttonClicked == 0){
            PointF leftIndexPosition =  pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
            PointF leftWristPosition = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition();
            PointF leftEarPosition = pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition();

            //double ratio = myLIndexLWrist/euclideanDistance(leftIndexPosition, leftWristPosition,1.0);
            double ratio = indexToWristDist/getDistance(leftIndexPosition, leftWristPosition,1.0);

            //Calculate distance between tragus and index (test is for between L.I to R.I)
            finalTragularDist = getDistance(leftEarPosition, leftIndexPosition,ratio);

            Log.d("FINAL RESULT",String.valueOf(finalTragularDist));
            Log.d("RATIO",String.valueOf(ratio));
            Log.d("Distance",String.valueOf(getDistance(leftIndexPosition, leftWristPosition,1.0)));
        }
        else {
            PointF rightIndexPosition =  pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition();
            PointF rightWristPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition();
            PointF rightEarPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR).getPosition();

            //double ratio = myLIndexLWrist/euclideanDistance(leftIndexPosition, leftWristPosition,1.0);
            double ratio = indexToWristDist/getDistance(rightIndexPosition, rightWristPosition,1.0);

            //Calculate distance between tragus and index (test is for between L.I to R.I)
            finalTragularDist = getDistance(rightEarPosition, rightIndexPosition,ratio);

            Log.d("FINAL BLOODY RESULT",String.valueOf(finalTragularDist));
            Log.d("RATIO",String.valueOf(ratio));
            Log.d("Distance",String.valueOf(getDistance(rightIndexPosition, rightWristPosition,1.0)));

        }

        return finalTragularDist;
    }
}
