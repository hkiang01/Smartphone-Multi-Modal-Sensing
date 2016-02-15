import csv
from math import *
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np


#dx = fig.add_subplot(1,2,4, projection='3d')

datafile = [open('WALKING_14-02-16_16-07-19_2430.csv', "rb")]
datafile.append(open('RUNNING_14-02-16_16-08-06_2430.csv', "rb"))
datafile.append(open('JUMPING_14-02-16_16-18-52_0.csv', "rb"))
datafile.append(open('IDLE_14-02-16_16-10-48_0.csv',"rb"))
datafile.append(open('STAIRS_14-02-16_16-12-33_455.csv',"rb"))

reader = csv.reader(datafile[0])

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

all_activities = []
sliced_data = []
timestamps = np.array([])

interval = int((next(reader)[0])[-3:-2])
reader = csv.reader(datafile)

rownum = 0
for activity in datafile:
    reader = csv.reader(activity)
    for row in reader:
        colnum = 0
        for col in row:
            mod = colnum % 11
            if mod is 0:
                next_thing = (interval + 2) % 10
                number = int(col[-3:-2])
                if number is next_thing:
                    interval = (interval + 2) % 10
                    chunk = [accelX, accelY, accelZ, gyroX, gyroY, gyroZ, magX, magY, magZ, light]
                    timestamps = np.append(timestamps, col)
                    sliced_data.append(chunk)
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
                    chunk = []
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
    all_activities.append(zip(*sliced_data))
    sliced_data = []

analyzed_data = []
plotit = []
for activity in all_activities:
    for chunk in activity:
        for elem in chunk:
            medians = np.append(medians, np.median(elem))
            means = np.append(means, np.mean(elem))
            variances = np.append(variances, np.var(elem))
            maxes = np.append(maxes, np.max(elem))
            #zero_crossings = np.prod((np.append(zero_crossings, np.where(np.diff(np.sign(elem)))[0]).shape)
        analyzed_data.append([medians, means, variances, maxes])
        medians = np.array([])
        means = np.array([])
        maxes = np.array([])
        variances = np.array([])
        #zero_crossings = np.array([])
    plotit.append(analyzed_data)
    analyzed_data = []
    

fig1 = plt.figure()
ax = fig1.add_subplot(111, projection='3d')
ax.scatter(plotit[0][0][3], plotit[0][1][3], plotit[0][2][3], zdir='z', s=20, c='b')
ax.scatter(plotit[1][0][3], plotit[1][1][3], plotit[1][2][3], zdir='z', s=15, c='r')
ax.set_title('Walking, Accelerometer, Variance')
plt.savefig('plots/fig1.png')
plt.show()

fig2 = plt.figure()
bx = fig2.add_subplot(111, projection='3d')
bx.scatter(analyzed_data[0][1], analyzed_data[1][1], analyzed_data[2][1], zdir='z', s=20, c='b')
bx.set_title('Walking, Accelerometer, Medians')
plt.savefig('plots/fig2.png')

fig3 = plt.figure()
cx = fig3.add_subplot(111, projection='3d')
cx.scatter(analyzed_data[0][2], analyzed_data[1][2], analyzed_data[2][2], zdir='z', s=20, c='b')
cx.set_title('Walking, Accelerometer, Maxima')
plt.savefig('plots/fig3.png')

fig4 = plt.figure()
cx = fig4.add_subplot(111, projection='3d')
cx.scatter(analyzed_data[0][0], analyzed_data[1][0], analyzed_data[2][0], zdir='z', s=20, c='b')
cx.set_title('Walking, Accelerometer, Means')
plt.savefig('plots/fig4.png')

