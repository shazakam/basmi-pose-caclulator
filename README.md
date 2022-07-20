
# BASMI Pose Estimation #
#  Example of input Pose and Result using MediaPipe Holistic![lumbar](https://user-images.githubusercontent.com/76884408/179982081-e406c8e3-e77d-407d-92d4-4bda7db1e484.png)



Axial Spondyloarthritis (AxSpA) is a condition affecting the spine that causes stiffness, pain and lack of mobility. 
Currently, the best way to detect this condition is using the BASMI (Bath Ankylosing Spondylitis Metrology Index), which uses mobility to measure the severity of the disease.
However, many patients struggle to access the BASMI as it can currently only be conducted by a trained physio and requires specialist measurement equipment. 
The BASMI consists of the following five measurements:
  1. Tragular to Wall
  2. Lumbar Side Flexion
  3. Intermalleolar Distance
  4. Cervical Rotation
  5. Lumbar Flexion (Schober's Modified)

Pose Estimation is a technology that aims to accurately track the position of a person’s joints and limbs using a picture or video input, without the need for markers, 
such as those used in motion capture technology.

This Android App is an attempt to automate taking measurements used to calculate the Bath Ankylosing Spondylitis Index (BASMI) using marker-less pose estimation
libraries. The current version only uses ML Kit and MediaPipe (which runs on a flask server in Jupyter NoteBook). The app outputs the landmark positions
required to calculate the distances for each measurement from each library (ML Kit landmarks are printed into the Logcat whilst the server outputs an 
excel file for MediaPipe landmarks).

The server outputs multiple landmarks for the same point for MediaPipe. This is due to MediaPipe being able to give world coordinates in metres around
the detected indivual as well as normalised image coordinates. Whilst ML Kit functions on a different coordinate system and only has one.

The primary goal of the project is to determine the viability of these pose libraries for the afforementioned purpose as well as comparing them hence,
UI was not worked on particularly as it is assumed people familiar with the program are using it (this may be improved on in future versions).

Lumbar Flexion will not be tested (although the code is written for it) as the assumptions had too great an impact on the accuracy of results.
The input distances are used as references to convert the distances in the respective coordinate system to centimetres.

If you have any questions or suggestions for improvement send me an email at lg801@bath.ac.uk or submit a pull request
