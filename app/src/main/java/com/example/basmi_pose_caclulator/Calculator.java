package com.example.basmi_pose_caclulator;

import static java.lang.Math.atan2;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.IOException;


public class Calculator {
    static int indexToElbow = 0;
    static int ankleToKnee = 0;
    static int indexToWrist = 0;

    //Tragular Measurements
        static Pose tragularLeftPose;
        static Pose tragularRightPose;
        //Tragular ELBOW
        static float tragularLeftElbow = 0;
        static float tragularRightElbow = 0;
        //Tragular WRIST
        static float tragularLeftWrist = 0;
        static float tragularRightWrist = 0;

    //Lumbar Measurements
        static Pose lumbarNeutralPose;
        static Pose lumbarLeftPose;
        static Pose lumbarRightPose;
        //Lumbar ELBOW
        static float lumbarLeftElbow = 0;
        static float lumbarRightElbow = 0;
        //Lumbar WRIST
        static float lumbarLeftWrist = 0;
        static float lumbarRightWrist = 0;

    //Intermalleolar Measurements
    static Pose intermalleolarPose;
    static float intermalleolarDistance = 0;

    //Cervical Measurements
    static Face cervicalNeutralFace;
    static Face cervicalLeftFace;
    static Face cervicalRightFace;
    static float cervicalYLeftEuler;
    static float cervicalYNeutralEuler;
    static float cervicalYRightEuler;
    static float cervicalLeftRotation = 0;
    static float cervicalRightRotation = 0;

    //Final results obtained from each activity
    static int tragusToWallScore = 0;
    static int lumbarSideFlexionScore = 0;
    static int cervicalRotationScore = 0;
    static int intermalleolarScore = 0;

    public Calculator(){
    }

    public static float getDistance(PointF firstPoint, PointF secondPoint, double ratio) {

        //Calculate the vector from firstPoint to secondPoint and return its length
        float xCoord=  (firstPoint.x - secondPoint.x);
        float yCoord = (firstPoint.y - secondPoint.y);
        PointF finalVector = new PointF();
        finalVector.set(xCoord,yCoord);
        return (float) ratio*finalVector.length();
    }

    public static double[] tragularResult(int buttonClicked, Pose pose){
        PointF indexPosition;
        PointF elbowPosition;
        PointF earPosition;
        PointF wristPosition;

        //If the left button was clicked
        if(buttonClicked == 0){
            indexPosition = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
            elbowPosition = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition();
            earPosition = pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition();
            wristPosition = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition();
        }
        else {
            indexPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition();
            elbowPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition();
            earPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR).getPosition();
            wristPosition = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition();
        }
        double ratioIndexElbow = indexToElbow/getDistance(indexPosition,elbowPosition,1);
        double ratioIndexWrist = indexToWrist/getDistance(indexPosition,wristPosition,1);
        Log.d("Ratio I.E",String.valueOf(ratioIndexElbow));
        Log.d("RATIO I.W",String.valueOf(ratioIndexWrist));

        return new double[]{getDistance(earPosition, indexPosition, ratioIndexElbow), getDistance(earPosition, indexPosition, ratioIndexWrist)};
    }

    public static int tragularScore(double tragularAverage){
        if(tragularAverage < 10){
            return 0;
        }
        else if(tragularAverage >= 10 && tragularAverage < 12.9){
            return 1;
        }
        else if(tragularAverage >= 12.9 && tragularAverage < 15.9){
            return 2;
        }
        else if(tragularAverage >= 15.9 && tragularAverage < 18.9){
            return 3;
        }
        else if(tragularAverage >= 18.9 && tragularAverage < 21.9){
            return 4;
        }
        else if(tragularAverage >= 21.9 && tragularAverage < 24.9){
            return 5;
        }
        else if(tragularAverage >= 24.9 && tragularAverage < 27.9){
            return 6;
        }
        else if(tragularAverage >= 27.9 && tragularAverage < 30.9){
            return 7;
        }
        else if(tragularAverage >= 30.9 && tragularAverage < 33.9){
            return 8;
        }
        else if(tragularAverage >= 33.9 && tragularAverage < 36.9){
            return 9;
        }
        else{
            return 10;
        }
    }

    public static double[] lumbarResult(int buttonClicked, Pose pose, PointF neutralCoord){
        PointF indexCoord;
        PointF elbowCoord;
        PointF wristCoord;

        if(buttonClicked == -1){
            indexCoord = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition();
            elbowCoord = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition();
            wristCoord = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition();
        }
        else{
            indexCoord = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition();
            elbowCoord = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition();
            wristCoord = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition();
        }
        float ratioIndexElbow = indexToElbow/getDistance(indexCoord,elbowCoord,1);
        float ratioIndexWrist = indexToWrist/getDistance(indexCoord,wristCoord,1);
        return new double[] {getDistance(neutralCoord,indexCoord,ratioIndexElbow),getDistance(neutralCoord,indexCoord,ratioIndexWrist)};
    }

    public static int lumbarScore(double lumbarAverage){
        if(lumbarAverage >= 20){
            return 0;
        }
        else if(lumbarAverage >= 18 && lumbarAverage < 20){
            return 1;
        }
        else if(lumbarAverage >= 15.9 && lumbarAverage < 18){
            return 2;
        }
        else if(lumbarAverage >= 13.8 && lumbarAverage < 15.9){
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
    public static float getIntermalleolarResult(Pose pose){
        float finalIntermalleolarDist;
        PointF leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE).getPosition();
        PointF leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition();
        PointF rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE).getPosition();
        PointF rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition();

        float ratio = ankleToKnee/((getDistance(leftAnkle,leftKnee,1)+getDistance(rightAnkle,rightKnee,1))/(float)1.52);
        finalIntermalleolarDist = getDistance(leftAnkle,rightAnkle,ratio);
        return finalIntermalleolarDist;
    }

    public static int intermalleolarScore(float intermalleolarResult){
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

    public static double getRotationOne(float radius, PointF neutralNoseCoord, PointF noseCoord){
        float arc = getDistance(neutralNoseCoord,noseCoord,1);
        Log.d("ARC",String.valueOf(arc));
        double angle = Math.toDegrees(Math.acos((2*Math.pow(radius,2)-Math.pow(arc,2))/(2*Math.pow(radius,2))));
        Log.d("ANGLE",String.valueOf(angle));
        return angle;
    }

    /*NOTE TO SELF: ROTATION ONE SEEMS MOST PROMISING MAY UNDERESTIMATE ANGLE*/
    public static double getRotationTwo(float radius, PointF neutralNoseCoord, PointF noseCoord){
        float arc = getDistance(neutralNoseCoord,noseCoord,1);
        double angle = arc/radius;
        return  Math.toDegrees(angle);
    }

    public double getRotationThree(PointF midPoint, PointF neutralNoseCoord, PointF noseCoord){
        float xDirection = noseCoord.x - neutralNoseCoord.x;
        float yDirection = noseCoord.y - neutralNoseCoord.y;
        PointF newPoint = new PointF();
        newPoint.set(midPoint.x + xDirection, midPoint.y + yDirection);

        float midToNeutral = getDistance(midPoint,neutralNoseCoord,1);
        float midToNew = getDistance(midPoint,newPoint,1);
        float neutralToNew = getDistance(neutralNoseCoord,newPoint,1);
        double cosineOfAngle = (Math.pow(midToNeutral,2) + Math.pow(midToNew,2) - Math.pow(neutralToNew,2))/(2*midToNeutral*midToNew);
        return Math.toDegrees(Math.acos(cosineOfAngle));
    }

    public static int getCervicalRotationScore(float rotationAverage){
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

    static double getAngle(PointF firstPoint, PointF midPoint, PointF lastPoint) {
        double result =
                Math.toDegrees(
                        atan2(lastPoint.y - midPoint.y, lastPoint.x - midPoint.x)
                                - atan2(firstPoint.y - midPoint.y, firstPoint.x - midPoint.x));

        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    public static void printPoses(Pose pose){
        for(PoseLandmark p:pose.getAllPoseLandmarks()){
            Log.d("LANDMARK "+String.valueOf(p.getLandmarkType()),"Position: " + String.valueOf(p.getPosition()) + " likelihood: "+String.valueOf(p.getInFrameLikelihood()));
        }
    }

    public static void printPoses(Pose pose, String message){
        Log.d("MEASUREMENT",message);

        int[] tragularLeftLandmarks = {7,13,15,19};
        int[] tragularRightLandmarks = {8,14,16,20};
        int[] lumbarLandmarks = {13,14,15,16,19,20,25,26,27,28};
        int[] intermalleolarLandmarks = {25,26,27,28,29,30,31,32};

        if(message.equals("TRAGULAR LEFT")){
            for(int landmark: tragularLeftLandmarks){
                Log.d("LANDMARK "+pose.getPoseLandmark(landmark).getLandmarkType(),String.valueOf(pose.getPoseLandmark(landmark).getPosition()));
            }
        }
        else if(message.equals("TRAGULAR RIGHT")){
            for(int landmark: tragularRightLandmarks){
                Log.d("LANDMARK "+String.valueOf(pose.getPoseLandmark(landmark).getLandmarkType()),String.valueOf(pose.getPoseLandmark(landmark).getPosition()));
            }
        }
        else if(message.equals("LUMBAR RIGHT") || message.equals("LUMBAR LEFT") || message.equals("LUMBAR NEUTRAL")){
            for(int landmark: lumbarLandmarks){
                Log.d("LANDMARK "+String.valueOf(pose.getPoseLandmark(landmark).getLandmarkType()),String.valueOf(pose.getPoseLandmark(landmark).getPosition()));
            }
        }
        else if(message.equals("INTERMALLEOLAR POSE")){
            for(int landmark: intermalleolarLandmarks){
                Log.d("LANDMARK "+String.valueOf(pose.getPoseLandmark(landmark).getLandmarkType()),String.valueOf(pose.getPoseLandmark(landmark).getPosition()));
            }
        }
        else{
            Log.d("ERROR","PRINT FAILURE "+message);
        }
    }

    public static void printPoses(Face face, String message){
        int[] cervicalLeftLandmarks = {0,9};
        int[] cervicalNeutralLandmarks = {0,3,9};
        int[] cervicalRightLandmarks = {0,3};

        Log.d("MEASUREMENT",message);
        if(message.equals("CERVICAL LEFT")){
            Log.d("EULER ANGLE LEFT",String.valueOf(cervicalYLeftEuler));
            for(int landmark: cervicalLeftLandmarks){
                Log.d("LANDMARK "+String.valueOf(face.getLandmark(landmark).getLandmarkType()),String.valueOf(face.getLandmark(landmark).getPosition()));
            }
        }
        else if(message.equals("CERVICAL NEUTRAL")){
            Log.d("EULER ANGLE NEUTRAL",String.valueOf(cervicalYNeutralEuler));
            for(int landmark: cervicalNeutralLandmarks){
                Log.d("LANDMARK "+String.valueOf(face.getLandmark(landmark).getLandmarkType()),String.valueOf(face.getLandmark(landmark).getPosition()));
            }
        }
        else if(message.equals("CERVICAL RIGHT")){
            Log.d("EULER ANGLE RIGHT",String.valueOf(cervicalYRightEuler));
            for(int landmark: cervicalRightLandmarks){
                Log.d("LANDMARK "+String.valueOf(face.getLandmark(landmark).getLandmarkType()),String.valueOf(face.getLandmark(landmark).getPosition()));
            }
        }
        else{
            Log.d("ERROR","PRINT FAILURE "+message);
        }
    }

    public static Bitmap getBitmapFromUri(ContentResolver contentResolver, Uri imageUri) throws IOException {
        Bitmap decodedBitmap = MediaStore.Images.Media.getBitmap(contentResolver,imageUri);
        return decodedBitmap;
    }
}
