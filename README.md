# ece498rr

###Stage 1:
  
  - See MultiModalSensing/app/src/main/java/com/example/harry/multimodalsensing/MainActivity.java
  
  - This stores in the 'Downloads' folder of Android OS the relevant CSV file
  
###Stage 2:
  
  - See analysis directory, the location for sample CSV's
  
###Stage 3:

  - See analysis/steps.py
  
  - Algorithm: At a zero crossing of the Accel.x, observe adjacent data points a,b where
  a and b are of opposite signs. If a and b both have a magnitude above a threshold, it's a step
  
###Stage 4:

  - See analysis/graphs.py
  
  - Process: parse CSV's into a 3d array indexed by activity, sensorcol, and feature
  
  - Ctrl+f for plotit[activity][sensorcol][feature] for details
  
  - For a given set of 3 [sensorcol][feature] pairs, calculate the mean distance between activity clusters
  
  - Acquire the best 3 [sensorcol][feature] pairs for classification using a heap
  
  - Generate the relevant graph, the 20 best of which are located in analysis/plots directory

  - Best: All Activities: AccY_Variance vs Light_Maxima vs AccX_Mean.png
