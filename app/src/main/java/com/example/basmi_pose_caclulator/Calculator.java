package com.example.basmi_pose_caclulator;

import android.graphics.PointF;
import android.util.Log;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;



public class Calculator {
    static int indexToElbow = 0;
    static int ankleToKnee = 0;
    //Final results obtained from each activity
    static int tragusToWallDist = 0;
    static int lumbarSideFlexionScore = 0;
    static float getLumbarSideFlexionSchoberDist = 0;
    static float cervicalRotation = 0;
    static float intermalleolarDist = 0;

    public Calculator(){
    }

    public static float getDistance(PointF firstPoint, PointF secondPoint, double ratio) {

        //Calculate the vector from firstPoint to secondPoint and return its length
        float xCoord= (float) (firstPoint.x - secondPoint.x);
        float yCoord = (float) (firstPoint.y - secondPoint.y);
        PointF finalVector = new PointF();
        finalVector.set(xCoord,yCoord);
        return (float) ratio*finalVector.length();
    }

    public double tragularResult(int buttonClicked, Pose pose){
        PointF indexPosition;
        PointF wristPosition;
        PointF earPosition;

        //If the left button was clicked
        if(buttonClicked == 0){
            indexPosition = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
            wristPosition = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition();
            earPosition = pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition();
        }
        else {
            indexPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition();
            wristPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition();
            earPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR).getPosition();
        }
        double ratio = indexToElbow /getDistance(indexPosition,wristPosition,1);
        return getDistance(earPosition,indexPosition,ratio);
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

    public double lumbarResult(int buttonClicked, Pose pose, PointF neutralCoord){
        PointF indexCoord;
        PointF elbowCoord;

        if(buttonClicked == -1){
            indexCoord = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
            elbowCoord = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition();
        }
        else{
            indexCoord = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition();
            elbowCoord = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition();
        }
        float ratio = indexToElbow /getDistance(indexCoord,elbowCoord,1);
        return getDistance(neutralCoord,indexCoord,ratio);
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

    //Is this one needed???
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

    public PointF getMidPoint(PointF firstCoord,PointF secondCoord){
        float midX = (firstCoord.x + secondCoord.x)/2;
        float midY = (firstCoord.y + secondCoord.y)/2;
        PointF midPoint = new PointF(midX,midY);
        return midPoint;
    }

    public double getRotationOne(PointF midPoint, PointF neutralNoseCoord, PointF noseCoord){
        float radius = getDistance(midPoint,neutralNoseCoord,1);
        float arc = getDistance(neutralNoseCoord,noseCoord,1);
        double angle = Math.acos((2*Math.pow(radius,2)-Math.pow(arc,2))/(2*Math.pow(radius,2)));
        return angle;
    }

    public double getRotationTwo(PointF midPoint, PointF neutralNoseCoord, PointF noseCoord){
        float radius = getDistance(midPoint,neutralNoseCoord,1);
        float arc = getDistance(neutralNoseCoord,noseCoord,1);
        double angle = (180*arc)/(Math.PI*radius);
        return  angle;
    }

    public float getCervicalRotationScore(float rotationAverage){
        if(rotationAverage>= 85){
            return 0;
        }
        else if(rotationAverage < 85 && rotationAverage >= 76.6){
            return 1;
        }
        else if(rotationAverage < 76.6 && rotationAverage >= 68.1){
            return 2;
        }
        else if(rotationAverage < 68.1 && rotationAverage >= 59.6){
            return 3;
        }
        else if(rotationAverage < 59.6 && rotationAverage >= 51.1){
            return 4;
        }
        else if(rotationAverage < 51.1 && rotationAverage >= 42.6){
            return 5;
        }
        else if(rotationAverage < 42.6 && rotationAverage >= 34.1){
            return 6;
        }
        else if(rotationAverage < 34.1 && rotationAverage >= 25.6){
            return 7;
        }
        else if(rotationAverage < 25.6 && rotationAverage >= 17.1){
            return 8;
        }
        else if(rotationAverage < 17.1 && rotationAverage >= 8.6){
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
