import csv
from math import *

datafile  = open('DDR_14-02-16_12_19_20_80800.csv', "rb")
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
        if list(col)[0] is 'e':
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
    if a >= 0 and b <= 0:
        steps += 1
print steps;

