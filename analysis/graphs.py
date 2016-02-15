import csv
from math import *
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

datafile  = open('WALKING_14-02-16_16-07-19_2430.csv', "rb")

reader = csv.reader(datafile)

e = []
time = []

accelX = np.array([])
accelY = np.array([])
accelZ = np.array([])
gyroX = np.array([])
gyroY = np.array([])
gyroZ = np.array([])
magX = np.array([])
magY = np.array([])
magZ = np.array([])
light = np.array([])
medians = np.array([])
means = np.array([])
maxes = np.array([])
variances = np.array([])
zero_crossings = np.array([])

sliced_data = []

x = 0
y = 0
z = 0

timestamps = np.array([])

#Cleanup the CVS file first, get rid of null data




interval = int((next(reader)[0])[-3])
rownum = 0
for row in reader:
    colnum = 0
    for col in row:
        mod = colnum % 11
        curr_ms = col[-3]
        if mod is 0:
            if curr_ms is interval + 2:
                interval = (interval + 2) % 10
                chunk = [accelX, accelY, accelZ, gyroX, gyroY, gyroZ, magX, magY, magZ, light]
                timestamps = np.append(timestamps, col)
                sliced_data.append(chunk)
        if list(col)[0] is 'n':
            pass
        elif mod is 1:
            accelX = np.append(accelX, float(col))
        elif mod is 2:
            accelY = np.append(accelY, float(col))
        elif mod is 3:
            accelZ = np.append(accelZ, float(col))
        elif mod is 4:
            gyroX = np.append(gyroX, float(col))
        elif mod is 5:
            gyroY = np.append(gyroY, float(col))
        elif mod is 6:
            gyroZ = np.append(gyroZ, float(col))
        elif mod is 7:
            magX = np.append(magX, float(col))
        elif mod is 8:
            magY = np.append(magY, float(col))
        elif mod is 9:
            magZ = np.append(magZ, float(col))
        elif mod is 10:
            light = np.append(light, float(col))
        colnum += 1
    rownum += 1

count = 0
current_max = 0

chunked_data = zip(*sliced_data)
analyzed_data = []
one_sensor_column = []
for chunk in chunked_data:
    for elem in chunk:
        medians = np.append(medians, np.median(elem))
        print medians
        means = np.append(means, np.mean(elem))
        variances = np.append(variances, np.variance(elem))
        maxes = np.append(maxes, np.max(elem))
      #  zero_crossings = np.append(zero_crossings, np.where(np.diff(np.sign(elem)))[0].shape
    analyzed_data.append([medians, means, variances, maxes])

ax.scatter(analyzed_data[0][0], analyzed_data[1][0], analyzed_data[2][0], zdir='z', s=20, c='b')

ax.set_xlabel('X Label')
ax.set_ylabel('Y Label')
ax.set_zlabel('Z Label')

plt.show()
