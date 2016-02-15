import csv
from math import *
import sys

args = sys.argv
#datafile  = open('WALKING_14-02-16_16-07-19_2430.csv', "rb")
datafile = open(str(args[1]), "rb")
reader = csv.reader(datafile)

e = []
time = []
x = 0
y = 0
z = 0 

rownum = 0
for row in reader:
    colnum = 0
    for col in row:
        mod = colnum % 11
        if list(col)[0] is 'n':
            pass
        elif mod is 0:
            time.append(col)
        elif mod is 1:
            x = float(col)
        elif mod is 2:
            y = float(col)
        elif mod is 3:
            z = float(col)
        colnum += 1
    e.append(sqrt(pow(x,2)+pow(y,2)+pow(z,2)) - 9.8)
    rownum += 1

datafile.close()

steps = 0;
for a, b in zip(e, e[1:]):
    if a >= 0 and b <= 0 and a>=0.10 and b <= -0.10:
       steps += 1
print steps;


