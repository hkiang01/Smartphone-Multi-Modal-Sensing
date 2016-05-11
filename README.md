## ECE/CS 498: Mobile Sensing, Computing, and Applications
## [Programming Assignment 1: Exploring Multi-Modal Sensing on Smartphones](https://courses.engr.illinois.edu/ece498rc4/slides/PA1_ece498rr.pdf)

### Harrrison Kiang (hkiang2) and Sara Akgul (akgul1)

###Stage 1 (Android app):
  
  - For main code, ee MultiModalSensing/app/src/main/java/com/example/harry/multimodalsensing/MainActivity.java
  
  - For views, see MultiModalSensing/app/src/main/res/layout/

  - This stores in the 'Downloads' folder of Android OS the relevant CSV file
  
###Stage 2 (sample CSV files from app):
  
  - See analysis directory, the location for sample CSV's
  
###Stage 3 (step algorithm):

  - See analysis/steps.py
  
  - Algorithm: We compute sqrt(Accel.x^2 + Accel.y^2 + Accel.z^2) - 9.81, to compute the Energy in a
    given step in time. Then, we observe adjacent data points a,b where a and b are of opposite signs. 
    If a and b both have a magnitude above a threshold, it counts as a step
  
###Stage 4 (3D plotting):

  - See analysis/graphs.py
  
  - Process: parse CSV's into a 3d array indexed by activity, sensorcol, and feature
  
  - Ctrl+f for plotit[activity][sensorcol][feature] for details
  
  - For a given set of 3 [sensorcol][feature] pairs, calculate the mean distance between activity clusters
  
  - Acquire the best 3 [sensorcol][feature] pairs for classification using a heap
  
  - Generate the relevant graph, the 20 best of which are located in analysis/plots directory

  - Best: All Activities: AccY_Variance vs Light_Maxima vs AccX_Mean.png

![All Activities: AccY Variance vs Light Maxima vs AccX Mean](https://raw.githubusercontent.com/hkiang01/Smartphone-Multi-Modal-Sensing/07072101dd772156d5e8995b07a20058bb5e7aa8/analysis/plots/All%20Activities%3A%20AccY_Variance%20vs%20Light_Maxima%20vs%20AccX_Mean.png?token=AFMhnYrF7PFuDhmJ3GYSUC9ngrMey-ZLks5XPAhNwA%3D%3D)
