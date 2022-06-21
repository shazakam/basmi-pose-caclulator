package com.example.basmi_pose_caclulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.util.Log;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;



public class Calculator {
    static int indexToElbow = 0;
    static int ankleToKnee = 0;
    //Final results obtained from each activity
    static int tragusToWallDist = 0;
    static int lumbarSideFlexionDist = 0;
    static float getLumbarSideFlexionSchoberDist = 0;
    static float cervicalRotationDist = 0;
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

        return (float) ratio*finalVector.length();
    }

    public double tragularResult(int buttonClicked, Pose pose){
        double finalTragularDist;
        //If the left button was clicked
        if(buttonClicked == 0){
            PointF leftIndexPosition =  pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
            PointF leftElbowPosition = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition();
            PointF leftEarPosition = pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition();

            //double ratio = myLIndexLWrist/euclideanDistance(leftIndexPosition, leftWristPosition,1.0);
            double ratio = indexToElbow/getDistance(leftIndexPosition, leftElbowPosition,1.0);

            //Calculate distance between tragus and index (test is for between L.I to R.I)
            finalTragularDist = getDistance(leftEarPosition, leftIndexPosition,ratio);

            Log.d("FINAL RESULT",String.valueOf(finalTragularDist));
            Log.d("RATIO",String.valueOf(ratio));
            Log.d("Distance",String.valueOf(getDistance(leftIndexPosition, leftElbowPosition,1.0)));
        }
        else {
            PointF rightIndexPosition =  pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition();
            PointF rightElbowPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition();
            PointF rightEarPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR).getPosition();

            double ratio = indexToElbow/getDistance(rightIndexPosition, rightElbowPosition,1.0);

            //Calculate distance between tragus and index (test is for between L.I to R.I)
            finalTragularDist = getDistance(rightEarPosition, rightIndexPosition,ratio);
            Log.d("FINAL RESULT RIGHT",String.valueOf(finalTragularDist));
            Log.d("RATIO",String.valueOf(ratio));
            Log.d("Distance",String.valueOf(getDistance(rightIndexPosition, rightElbowPosition,1.0)));
        }
        return finalTragularDist;
    }

    public int tragularScore(double tragularAverage){
        if(tragularAverage < 10){
            return 0;
        }
        else if(tragularAverage >= 10 && tragularAverage <= 12.9){
            return 1;
        }
        else if(tragularAverage >= 13 && tragularAverage <= 15.9){
            return 2;
        }
        else if(tragularAverage >= 16 && tragularAverage<= 18.9){
            return 3;
        }
        else if(tragularAverage >= 19 && tragularAverage <= 21.9){
            return 4;
        }
        else if(tragularAverage >= 22 && tragularAverage <= 24.9){
            return 5;
        }
        else if(tragularAverage >= 25 && tragularAverage <= 27.9){
            return 6;
        }
        else if(tragularAverage >= 28 && tragularAverage <= 30.9){
            return 7;
        }
        else if(tragularAverage >= 31 && tragularAverage <= 33.9){
            return 8;
        }
        else if(tragularAverage >= 34 && tragularAverage <= 36.9){
            return 9;
        }
        else{
            return 10;
        }
    }

    public int lumbarScore(float lumbarAverage){
        if(lumbarAverage >= 20){
            return 0;
        }
        else if(lumbarAverage >= 18 && lumbarAverage < 20){
            return 1;
        }
        else if(lumbarAverage>=15.9 && lumbarAverage < 18){
            return 2;
        }
        else if(lumbarAverage>=13.8 && lumbarAverage < 15.9){
            return 3;
        }
        else if(lumbarAverage >= 11.7 && lumbarAverage <13.8){
            return 4;
        }
        else if(lumbarAverage >= 9.6 && lumbarAverage <11.7){
            return 5;
        }
        else if(lumbarAverage >= 7.5 && lumbarAverage < 9.6){
            return 6;
        }
        else if(lumbarAverage >= 5.4 && lumbarAverage < 7.5){
            return 7;
        }
        else if(lumbarAverage >= 3.3 && lumbarAverage < 5.4){
            return 8;
        }
        else if(lumbarAverage >= 1.2 && lumbarAverage < 3.3){
            return 9;
        }
        else{
            return 10;
        }
    }

    public float getIntermalleolarResult(Pose pose){
        float finalIntermalleolarDist;
        PointF leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE).getPosition();
        PointF leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition();
        PointF rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE).getPosition();
        PointF rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition();

        float ratio = ankleToKnee/((getDistance(leftAnkle,leftKnee,1)+getDistance(rightAnkle,rightKnee,1))/2);
        finalIntermalleolarDist = getDistance(leftAnkle,rightAnkle,ratio);
        return finalIntermalleolarDist;
    }

    public int intermalleolarScore(float intermalleolarResult){
        if(intermalleolarResult >= 120){
            return 0;
        }
        else if(intermalleolarResult >= 110 && intermalleolarResult < 120){
            return 1;
        }
        else if(intermalleolarResult >= 100 && intermalleolarResult < 110){
            return 2;
        }
        else if(intermalleolarResult >= 90 && intermalleolarResult < 100){
            return 3;
        }
        else if(intermalleolarResult >= 80 && intermalleolarResult < 90){
            return 4;
        }
        else if(intermalleolarResult >= 70 && intermalleolarResult < 80){
            return 5;
        }
        else if(intermalleolarResult >= 60 && intermalleolarResult < 70){
            return 6;
        }
        else if(intermalleolarResult >= 50 && intermalleolarResult < 60){
            return 7;
        }
        else if(intermalleolarResult >= 40 && intermalleolarResult <50){
            return 8;
        }
        else if(intermalleolarResult >= 30 && intermalleolarResult < 40){
            return 9;
        }
        else{
            return 10;
        }
    }


    public void printPoses(Pose pose){
        for(PoseLandmark p:pose.getAllPoseLandmarks()){
            Log.d("LANDMARK "+String.valueOf(p.getLandmarkType()),"Position: " + String.valueOf(p.getPosition()) + " likelihood: "+String.valueOf(p.getInFrameLikelihood()));
        }
    }

}
