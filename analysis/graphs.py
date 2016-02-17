import csv
from math import *
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math
import sys
import scipy.spatial.distance as distance
try:
    import Queue as Q
except ImportError:
    import queue as Q

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
    all_activities.append(zip(*sliced_data)) #transpose
    sliced_data = []

analyzed_data = []
plotit = []
for activity in all_activities:
    for chunk in activity:
        for elem in chunk:
            medians = np.append(medians, np.median(elem))
            means = np.append(means, np.mean(elem))
            variances = np.append(variances, np.var(elem))
            try:
		      maxes = np.append(maxes, np.max(elem))
            except ValueError: #if elem is empty
		      pass
            zero_crossings = np.append(zero_crossings, np.prod(np.where(np.diff(np.sign(elem)))[0].shape))
        analyzed_data.append([medians, means, variances, maxes, zero_crossings])
        medians = np.array([])
        means = np.array([])
        maxes = np.array([])
        variances = np.array([])
        zero_crossings = np.array([])
    plotit.append(analyzed_data)
    analyzed_data = []
    
# plotit[activity][sensorcol][feature]
# activity:
#       0: walking
#       1: running
#       2: jumping
#       3: idle
#       4: stairs
# sensorcol:
#       0-2: accelerometer (x,y,z)
#       3-5: gyroscope (x,y,z)
#       6-8: magnetometer (x,y,z)
#       9: light sensor
# feature:
#       0: means
#       1: medians
#       2: maxima
#       3: variances
#       4: zero crossings
#

activity_names = ['WALKING', 'RUNNING', 'JUMPING', 'IDLE', 'STAIRS']
sensor_names = ['AccX', 'AccY', 'AccZ', 'GyroX', 'GyroY', 'GyroZ', 'MagnetX', 'MagnetY', 'MagnetZ', 'Light']
feature_names = ['Mean', 'Median', 'Maxima', 'Variance', 'Zero Crossings']

# a = 0
# b = 3
# c = 5
# d = 0
# e = 2
# f = 4

# figCounter = 1
# for a in xrange(len(sensor_names)):
#     for b in xrange(len(feature_names)):
#         for c in xrange(len(sensor_names)):
#             for d in xrange(len(feature_names)):
#                 if(a==c and b==d):
#                     continue
#                 for e in xrange(len(sensor_names)):
#                     for f in xrange(len(feature_names)):
#                         if((e==a and f==b) or (e==c and f==d)):
#                             continue
#                         myfig = plt.figure(figsize=(20,15))
#                         myx = myfig.add_subplot(111, projection='3d')
#                         try:
#                             myx.scatter(plotit[0][a][b], plotit[0][c][d], plotit[0][e][f], zdir='z', s=15, c='b', label=activity_names[0])
#                         except ValueError:
#                             continue
#                         myx.scatter(plotit[1][a][b], plotit[1][c][d], plotit[1][e][f], zdir='z', s=15, c='r', label=activity_names[1])
#                         myx.scatter(plotit[2][a][b], plotit[2][c][d], plotit[2][e][f], zdir='z', s=15, c='g', label=activity_names[2])
#                         myx.scatter(plotit[3][a][b], plotit[3][c][d], plotit[3][e][f], zdir='z', s=15, c='k', label=activity_names[3])
#                         myx.scatter(plotit[4][a][b], plotit[4][c][d], plotit[4][e][f], zdir='z', s=15, c='c', label=activity_names[4])
#                         myx.legend(loc='upper left', prop={'size':20})
#                         myTitle = 'All Activities: ' + sensor_names[a] + '_' + feature_names[b] + ' vs ' + sensor_names[c] + '_' + feature_names[d] + ' vs ' + sensor_names[e] + '_' + feature_names[f];
#                         myx.set_title(myTitle)
#                         #plt.savefig('plots/fig' + str(figCounter))
#                         plt.savefig('plots/' + myTitle)
#                         #plt.show()
#                         plt.clf()
#                         plt.close()
#                         figCounter+=1;


# cluster points associated with each activity
# get average distance between cluster centroids
# goal: get settings associated with greatest avg distance between clusters
q = Q.PriorityQueue()

for a in xrange(len(sensor_names)):
    for b in xrange(len(feature_names)):
        for c in xrange(len(sensor_names)):
            for d in xrange(len(feature_names)):
                if(a==c and b==d):
                    continue
                for e in xrange(len(sensor_names)):
                    for f in xrange(len(feature_names)):
                        if((e==a and f==b) or (e==c and f==d)):
                            continue
                        avgs = []
                        for act in xrange(len(activity_names)):
                            x = plotit[act][a][b]
                            y = plotit[act][c][d]
                            z = plotit[act][e][f]
                            centroid = [sum(x)/len(x), sum(y)/len(y), sum(x)/len(z)]
                            avgs.append(centroid)
                        npavgs = np.asarray(avgs)
                        # for avg in npavgs:
                        #     print avg
                        dist = distance.pdist(npavgs)
                        dist = -dist
                        curr_avg = np.average(dist)
                        params = [a,b,c,d,e,f]
                        curr_pair = (curr_avg, params)
                        q.put(curr_pair)
                        #print curr_pair
                        #sys.exit(0)
# settings associated with greatest avg distance between clusters
for i in xrange(10): # xrange(n), n best graphs
    best_pair = q.get()
    a = best_pair[1][0]
    b = best_pair[1][1]
    c = best_pair[1][2]
    d = best_pair[1][3]
    e = best_pair[1][4]
    f = best_pair[1][5]

    # figure according to settings associated with greatest avg distance between clusters
    myfig = plt.figure(figsize=(20,15))
    myx = myfig.add_subplot(111, projection='3d')
    myx.scatter(plotit[0][a][b], plotit[0][c][d], plotit[0][e][f], zdir='z', s=15, c='b', label=activity_names[0])
    myx.scatter(plotit[1][a][b], plotit[1][c][d], plotit[1][e][f], zdir='z', s=15, c='r', label=activity_names[1])
    myx.scatter(plotit[2][a][b], plotit[2][c][d], plotit[2][e][f], zdir='z', s=15, c='g', label=activity_names[2])
    myx.scatter(plotit[3][a][b], plotit[3][c][d], plotit[3][e][f], zdir='z', s=15, c='k', label=activity_names[3])
    myx.scatter(plotit[4][a][b], plotit[4][c][d], plotit[4][e][f], zdir='z', s=15, c='c', label=activity_names[4])
    myx.legend(loc='upper left', prop={'size':20})
    myTitle = 'All Activities: ' + sensor_names[a] + '_' + feature_names[b] + ' vs ' + sensor_names[c] + '_' + feature_names[d] + ' vs ' + sensor_names[e] + '_' + feature_names[f];
    myx.set_title(myTitle)
    #plt.savefig('plots/fig' + str(figCounter))
    plt.savefig('plots/' + myTitle)
    plt.show()
    plt.clf()
    plt.close()
                        
# fig1 = plt.figure()
# ax = fig1.add_subplot(111, projection='3d')
# ax.scatter(plotit[0][0][0], plotit[0][1][0], plotit[0][2][0], zdir='z', s=15, c='b', label=activity_names[0])
# ax.scatter(plotit[1][0][0], plotit[1][1][0], plotit[1][2][0], zdir='z', s=15, c='r', label=activity_names[1])
# ax.scatter(plotit[2][0][0], plotit[2][1][0], plotit[2][2][0], zdir='z', s=15, c='g', label=activity_names[2])
# ax.scatter(plotit[3][0][0], plotit[3][1][0], plotit[3][2][0], zdir='z', s=15, c='k', label=activity_names[3])
# ax.scatter(plotit[4][0][0], plotit[4][1][0], plotit[4][2][0], zdir='z', s=15, c='c', label=activity_names[4])
# ax.legend(loc='upper left', prop={'size':6})
# ax.set_title('All Activities, Accelerometer, Means')
# plt.savefig('plots/accelerometer_means.png')
# # plt.show()

# fig2 = plt.figure()
# bx = fig2.add_subplot(111, projection='3d')
# bx.scatter(plotit[0][0][1], plotit[0][1][1], plotit[0][2][1], zdir='z', s=15, c='b', label=activity_names[0])
# bx.scatter(plotit[1][0][1], plotit[1][1][1], plotit[1][2][1], zdir='z', s=15, c='r', label=activity_names[1])
# bx.scatter(plotit[2][0][1], plotit[2][1][1], plotit[2][2][1], zdir='z', s=15, c='g', label=activity_names[2])
# bx.scatter(plotit[3][0][1], plotit[3][1][1], plotit[3][2][1], zdir='z', s=15, c='k', label=activity_names[3])
# bx.scatter(plotit[4][0][1], plotit[4][1][1], plotit[4][2][1], zdir='z', s=15, c='c', label=activity_names[4])
# bx.legend(loc='upper left')
# bx.set_title('All Activities, Accelerometer, Medians')
# plt.savefig('plots/accelerometer_medians.png')
# #plt.show()

# fig3 = plt.figure()
# cx = fig3.add_subplot(111, projection='3d')
# cx.scatter(plotit[0][0][2], plotit[0][1][2], plotit[0][2][2], zdir='z', s=15, c='b', label=activity_names[0])
# cx.scatter(plotit[1][0][2], plotit[1][1][2], plotit[1][2][2], zdir='z', s=15, c='r', label=activity_names[1])
# cx.scatter(plotit[2][0][2], plotit[2][1][2], plotit[2][2][2], zdir='z', s=15, c='g', label=activity_names[2])
# cx.scatter(plotit[3][0][2], plotit[3][1][2], plotit[3][2][2], zdir='z', s=15, c='k', label=activity_names[3])
# cx.scatter(plotit[4][0][2], plotit[4][1][2], plotit[4][2][2], zdir='z', s=15, c='c', label=activity_names[3])
# cx.legend(loc='upper left')
# cx.set_title('All Activities, Accelerometer, Maxima')
# plt.savefig('plots/accelerometer_maxima.png')
# #plt.show()

# fig4 = plt.figure()
# dx = fig4.add_subplot(111, projection='3d')
# dx.scatter(plotit[0][0][3], plotit[0][1][3], plotit[0][2][3], zdir='z', s=15, c='b', label=activity_names[0])
# dx.scatter(plotit[1][0][3], plotit[1][1][3], plotit[1][2][3], zdir='z', s=15, c='r', label=activity_names[1])
# dx.scatter(plotit[2][0][3], plotit[2][1][3], plotit[2][2][3], zdir='z', s=15, c='g', label=activity_names[2])
# dx.scatter(plotit[3][0][3], plotit[3][1][3], plotit[3][2][3], zdir='z', s=15, c='k', label=activity_names[3])
# dx.scatter(plotit[4][0][3], plotit[4][1][3], plotit[4][2][3], zdir='z', s=15, c='c', label=activity_names[4])
# dx.legend(loc='upper left')
# dx.set_title('All Activities, Acceleromenter, Variances')
# plt.savefig('plots/accelerometer_variances.png')
# #plt.show()

# fig5 = plt.figure()
# ex = fig5.add_subplot(111, projection='3d')
# ex.scatter(plotit[0][3][0], plotit[0][4][0], plotit[0][5][0], zdir='z', s=15, c='b', label=activity_names[0])
# ex.scatter(plotit[1][3][0], plotit[1][4][0], plotit[1][5][0], zdir='z', s=15, c='r', label=activity_names[1])
# ex.scatter(plotit[2][3][0], plotit[2][4][0], plotit[2][5][0], zdir='z', s=15, c='g', label=activity_names[2])
# ex.scatter(plotit[3][3][0], plotit[3][4][0], plotit[3][5][0], zdir='z', s=15, c='k', label=activity_names[3])
# ex.scatter(plotit[4][3][0], plotit[4][4][0], plotit[4][5][0], zdir='z', s=15, c='c', label=activity_names[4])
# ex.legend(loc='upper left')
# ex.set_title('All Activities, Gyroscope, Means')
# plt.savefig('plots/gyro_means.png')
# #plt.show()

# fig6 = plt.figure()
# fx = fig6.add_subplot(111, projection='3d')
# fx.scatter(plotit[0][3][1], plotit[0][4][1], plotit[0][5][1], zdir='z', s=15, c='b', label=activity_names[0])
# fx.scatter(plotit[1][3][1], plotit[1][4][1], plotit[1][5][1], zdir='z', s=15, c='r', label=activity_names[1])
# fx.scatter(plotit[2][3][1], plotit[2][4][1], plotit[2][5][1], zdir='z', s=15, c='g', label=activity_names[2])
# fx.scatter(plotit[3][3][1], plotit[3][4][1], plotit[3][5][1], zdir='z', s=15, c='k', label=activity_names[3])
# fx.scatter(plotit[4][3][1], plotit[4][4][1], plotit[4][5][1], zdir='z', s=15, c='c', label=activity_names[4])
# fx.legend(loc='upper left')
# fx.set_title('All Activities, Gyroscope, Medians')
# plt.savefig('plots/gyro_medians.png')
# #plt.show()

# fig7 = plt.figure()
# gx = fig7.add_subplot(111, projection='3d')
# gx.scatter(plotit[0][3][2], plotit[0][4][2], plotit[0][5][2], zdir='z', s=15, c='b', label=activity_names[0])
# gx.scatter(plotit[1][3][2], plotit[1][4][2], plotit[1][5][2], zdir='z', s=15, c='r', label=activity_names[1])
# gx.scatter(plotit[2][3][2], plotit[2][4][2], plotit[2][5][2], zdir='z', s=15, c='g', label=activity_names[2])
# gx.scatter(plotit[3][3][2], plotit[3][4][2], plotit[3][5][2], zdir='z', s=15, c='k', label=activity_names[3])
# gx.scatter(plotit[4][3][2], plotit[4][4][2], plotit[4][5][2], zdir='z', s=15, c='c', label=activity_names[4])
# gx.legend(loc='upper left')
# gx.set_title('All Activities, Gyroscope, Maxima')
# plt.savefig('plots/gyro_maxima.png')
# #plt.show()

# fig8 = plt.figure()
# hx = fig8.add_subplot(111, projection='3d')
# hx.scatter(plotit[0][3][3], plotit[0][4][3], plotit[0][5][3], zdir='z', s=15, c='b', label=activity_names[0])
# hx.scatter(plotit[1][3][3], plotit[1][4][3], plotit[1][5][3], zdir='z', s=15, c='r', label=activity_names[1])
# hx.scatter(plotit[2][3][3], plotit[2][4][3], plotit[2][5][3], zdir='z', s=15, c='g', label=activity_names[2])
# hx.scatter(plotit[3][3][3], plotit[3][4][3], plotit[3][5][3], zdir='z', s=15, c='k', label=activity_names[3])
# hx.scatter(plotit[4][3][3], plotit[4][4][3], plotit[4][5][3], zdir='z', s=15, c='c', label=activity_names[4])
# hx.legend(loc='upper left')
# hx.set_title('All Activities, Gyroscope, Variances')
# plt.savefig('plots/gyro_variances.png')
# #plt.show()

# fig9 = plt.figure()
# ix = fig9.add_subplot(111, projection='3d')
# ix.scatter(plotit[0][6][0], plotit[0][7][0], plotit[0][8][0], zdir='z', s=15, c='b', label=activity_names[0])
# ix.scatter(plotit[1][6][0], plotit[1][7][0], plotit[1][8][0], zdir='z', s=15, c='r', label=activity_names[1])
# ix.scatter(plotit[2][6][0], plotit[2][7][0], plotit[2][8][0], zdir='z', s=15, c='g', label=activity_names[2])
# ix.scatter(plotit[3][6][0], plotit[3][7][0], plotit[3][8][0], zdir='z', s=15, c='k', label=activity_names[3])
# ix.scatter(plotit[4][6][0], plotit[4][7][0], plotit[4][8][0], zdir='z', s=15, c='c', label=activity_names[4])
# ix.legend(loc='upper left')
# ix.set_title('All Activities, Magnetometer, Means')
# plt.savefig('plots/magnetometer_means.png')
# #plt.show()

# fig10 = plt.figure()
# jx = fig10.add_subplot(111, projection='3d')
# jx.scatter(plotit[0][6][1], plotit[0][7][1], plotit[0][8][1], zdir='z', s=15, c='b', label=activity_names[0])
# jx.scatter(plotit[1][6][1], plotit[1][7][1], plotit[1][8][1], zdir='z', s=15, c='r', label=activity_names[1])
# jx.scatter(plotit[2][6][1], plotit[2][7][1], plotit[2][8][1], zdir='z', s=15, c='g', label=activity_names[2])
# jx.scatter(plotit[3][6][1], plotit[3][7][1], plotit[3][8][1], zdir='z', s=15, c='k', label=activity_names[3])
# jx.scatter(plotit[4][6][1], plotit[4][7][1], plotit[4][8][1], zdir='z', s=15, c='c', label=activity_names[4])
# jx.legend(loc='upper left')
# jx.set_title('All Activities, Magnetometer, Medians')
# plt.savefig('plots/magnetometer_medians.png')
# #plt.show()

# fig11 = plt.figure()
# kx = fig11.add_subplot(111, projection='3d')
# kx.scatter(plotit[0][6][2], plotit[0][7][2], plotit[0][8][2], zdir='z', s=15, c='b', label=activity_names[0])
# kx.scatter(plotit[1][6][2], plotit[1][7][2], plotit[1][8][2], zdir='z', s=15, c='r', label=activity_names[1])
# kx.scatter(plotit[2][6][2], plotit[2][7][2], plotit[2][8][2], zdir='z', s=15, c='g', label=activity_names[2])
# kx.scatter(plotit[3][6][2], plotit[3][7][2], plotit[3][8][2], zdir='z', s=15, c='k', label=activity_names[3])
# kx.scatter(plotit[4][6][2], plotit[4][7][2], plotit[4][8][2], zdir='z', s=15, c='c', label=activity_names[4])
# kx.legend(loc='upper left')
# kx.set_title('All Activities, Magnetometer, Maxima')
# plt.savefig('plots/magnetometer_maxima.png')
# #plt.show()

# fig12 = plt.figure()
# lx = fig12.add_subplot(111, projection='3d')
# lx.scatter(plotit[0][6][3], plotit[0][7][3], plotit[0][8][3], zdir='z', s=15, c='b', label=activity_names[0])
# lx.scatter(plotit[1][6][3], plotit[1][7][3], plotit[1][8][3], zdir='z', s=15, c='r', label=activity_names[1])
# lx.scatter(plotit[2][6][3], plotit[2][7][3], plotit[2][8][3], zdir='z', s=15, c='g', label=activity_names[2])
# lx.scatter(plotit[3][6][3], plotit[3][7][3], plotit[3][8][3], zdir='z', s=15, c='k', label=activity_names[3])
# lx.scatter(plotit[4][6][3], plotit[4][7][3], plotit[4][8][3], zdir='z', s=15, c='c', label=activity_names[4])
# lx.legend(loc='upper left')
# lx.set_title('All Activities, Magnetometer, Variances')
# plt.savefig('plots/magnetometer_variances.png')
# #plt.show()


# grab x values for time elapsed for each activity (for use in line graphs)
# assumes same dimension for each feature (for an activity)
# reason: different activities run for different length of time
num_activities = len(plotit)
time_elapsed = [] #the X dim
for i in xrange(num_activities):
    time_elapsed.append([])
    num_light_chunks = plotit[i][9][0].shape[0] #dim for light feature 0
    ttl_time = 0
    for ts in xrange(num_light_chunks):
        time_elapsed[i].append(ttl_time)
        ttl_time += 0.2
        ttl_time = math.floor(ttl_time*10)/10 # round to 1 decimal place


# fig13 = plt.figure()
# mx = fig13.add_subplot(111)
# mx.plot(time_elapsed[0], plotit[0][9][0], c='b', label=activity_names[0])
# mx.plot(time_elapsed[1], plotit[1][9][0], c='r', label=activity_names[1])
# mx.plot(time_elapsed[2], plotit[2][9][0], c='g', label=activity_names[2])
# mx.plot(time_elapsed[3], plotit[3][9][0], c='k', label=activity_names[3])
# mx.plot(time_elapsed[4], plotit[4][9][0], c='c', label=activity_names[4])
# mx.legend(loc='upper left', prop={'size':6})
# mx.set_title('All Activities, Light Sensor, Means')
# plt.savefig('plots/light_means.png')
# # plt.show()

# fig14 = plt.figure()
# nx = fig14.add_subplot(111)
# nx.plot(time_elapsed[0], plotit[0][9][1], c='b', label=activity_names[0])
# nx.plot(time_elapsed[1], plotit[1][9][1], c='r', label=activity_names[1])
# nx.plot(time_elapsed[2], plotit[2][9][1], c='g', label=activity_names[2])
# nx.plot(time_elapsed[3], plotit[3][9][1], c='k', label=activity_names[3])
# nx.plot(time_elapsed[4], plotit[4][9][1], c='c', label=activity_names[4])
# nx.legend(loc='upper left', prop={'size':6})
# nx.set_title('All Activities, Light Sensor, Medians')
# plt.savefig('plots/light_medians.png')
# # plt.show()

# fig15 = plt.figure()
# ox = fig15.add_subplot(111)
# ox.plot(time_elapsed[0], plotit[0][9][2], c='b', label=activity_names[0])
# ox.plot(time_elapsed[1], plotit[1][9][2], c='r', label=activity_names[1])
# ox.plot(time_elapsed[2], plotit[2][9][2], c='g', label=activity_names[2])
# ox.plot(time_elapsed[3], plotit[3][9][2], c='k', label=activity_names[3])
# ox.plot(time_elapsed[4], plotit[4][9][2], c='c', label=activity_names[4])
# ox.legend(loc='upper left', prop={'size':6})
# ox.set_title('All Activities, Light Sensor, Maxima')
# plt.savefig('plots/light_maxima.png')
# # plt.show()

# fig16 = plt.figure()
# px = fig16.add_subplot(111)
# px.plot(time_elapsed[0], plotit[0][9][1], c='b', label=activity_names[0])
# px.plot(time_elapsed[1], plotit[1][9][1], c='r', label=activity_names[1])
# px.plot(time_elapsed[2], plotit[2][9][1], c='g', label=activity_names[2])
# px.plot(time_elapsed[3], plotit[3][9][1], c='k', label=activity_names[3])
# px.plot(time_elapsed[4], plotit[4][9][1], c='c', label=activity_names[4])
# px.legend(loc='upper left', prop={'size':6})
# px.set_title('All Activities, Light Sensor, Variances')
# plt.savefig('plots/light_variances.png')
# # plt.show()
