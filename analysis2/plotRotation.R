library('stringr')

normalized_time_interval<-200 # in ms
timestamp_col<-1
gyroX_col<-2
gyroY_col<-3
gyroZ_col<-4
setwd('/Users/harry/projects/ece498rr/analysis2/')
wdat<-read.csv('ACTIVITY_06-03-16_21-28-16-767.csv', header=TRUE, na.strings=c("", "null", "NA"))

gyroRaw<-wdat[,c(timestamp_col, gyroX_col, gyroY_col, gyroZ_col)]
gyroRaw<-na.omit(gyroRaw) #remove na's
gyroRaw<-t(gyroRaw)
gyroRaw<-na.omit(gyroRaw) #remove na's
gyroNormalized<-matrix(ncol=4)


#normalize to 200ms timestamps
tsLen<-nchar(gyroRaw[,c(1)][1])
zeroTS_string<-substr(gyroRaw[,c(1)][1],tsLen-11,tsLen) #extract ms for "zero" time
zeroTS_string<-str_replace_all(zeroTS_string, ":", "")
zeroTS<-as.numeric(zeroTS_string)
nextTS<-zeroTS+normalized_time_interval
gyroNormalized<-rbind(gyroNormalized, c(0, gyroRaw[,c(1)][2], gyroRaw[,c(1)][3], gyroRaw[,c(1)][4])) #first row at "zero" time
count<-1
for(i in 2:NCOL(gyroRaw)) {
  curTS_string<-substr(gyroRaw[,c(i)][1],tsLen-11,tsLen) #extract ms for "zero" time
  curTS_string<-str_replace_all(curTS_string, ":", "")
  curTS<-as.numeric(curTS_string)
  if(!is.na(curTS >= nextTS) && curTS >= nextTS) {
    gyroNormalized<-rbind(gyroNormalized, c(normalized_time_interval * count, gyroRaw[,c(i)][2], gyroRaw[,c(i)][3], gyroRaw[,c(i)][4]))
    nextTS<-nextTS+normalized_time_interval
    count<-count+1
  }
}
gyroNormalized<-na.omit(gyroNormalized)
#at this point, gyroNormalized has degree readings at normalized time intervals of normalized_time_interval


par(xpd=TRUE)
plot(gyroNormalized[,c(1)], gyroNormalized[,c(2)], col="black", type="l", main="GyroX Over Time", xlab="Time in ms", ylab="Rotation")
plot(gyroNormalized[,c(1)], gyroNormalized[,c(3)], col="red", type="l", main="GyroY Over Time", xlab="Time in ms", ylab="Rotation")
plot(gyroNormalized[,c(1)], gyroNormalized[,c(4)], col="green", type="l", main="GyroZ Over Time", xlab="Time in ms", ylab="Rotation")

legend(0,-60,c("X", "Y", "Z"),col=c("black", "red", "green"),pch=1, horiz=TRUE)
