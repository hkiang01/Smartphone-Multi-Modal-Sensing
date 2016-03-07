library('stringr')

normalized_time_interval<-200 # in ms
timestamp_col<-1
degree_col<-14
dir_col<-16
ground_truth_col<-17
setwd('/Users/harry/projects/ece498rr/analysis2/')
wdat<-read.csv('ACTIVITY_06-03-16_20-59-14-493.csv', header=TRUE, na.strings=c("", "null", "NA"))

date_and_degrees<-wdat[,c(timestamp_col, degree_col, dir_col,ground_truth_col)]
date_and_degrees<-na.omit(date_and_degrees) #remove na's
date_and_degrees<-t(date_and_degrees)
date_and_degrees<-na.omit(date_and_degrees) #remove na's
new_dnd<-matrix(ncol=4)


#normalize to 200ms timestamps
tsLen<-nchar(date_and_degrees[,c(1)][1])
zeroTS_string<-substr(date_and_degrees[,c(1)][1],tsLen-11,tsLen) #extract ms for "zero" time
zeroTS_string<-str_replace_all(zeroTS_string, ":", "")
zeroTS<-as.numeric(zeroTS_string)
nextTS<-zeroTS+normalized_time_interval
new_dnd<-rbind(new_dnd, c(0, date_and_degrees[,c(1)][2], date_and_degrees[,c(1)][3], date_and_degrees[,c(1)][4])) #first row at "zero" time
count<-1
for(i in 2:NCOL(date_and_degrees)) {
 curTS_string<-substr(date_and_degrees[,c(i)][1],tsLen-11,tsLen) #extract ms for "zero" time
 curTS_string<-str_replace_all(curTS_string, ":", "")
 curTS<-as.numeric(curTS_string)
 if(!is.na(curTS >= nextTS) && curTS >= nextTS) {
   new_dnd<-rbind(new_dnd, c(normalized_time_interval * count, date_and_degrees[,c(i)][2], date_and_degrees[,c(i)][3], date_and_degrees[,c(i)][4]))
   nextTS<-nextTS+normalized_time_interval
   count<-count+1
 }
}
new_dnd<-na.omit(new_dnd)
#at this point, new_dnd has degree readings at normalized time intervals of normalized_time_interval

#color matrix
colMatrix<-matrix()
t_new_dnd<-t(new_dnd)
for(i in 1:NCOL(t_new_dnd)) {
  curDir<-t_new_dnd[,c(i)][3]
  if(curDir=="N") {
    colMatrix<-rbind(colMatrix, "black")
  }
  else if(curDir=="E") {
    colMatrix<-rbind(colMatrix, "red")
  }
  else if(curDir=="S") {
    colMatrix<-rbind(colMatrix, "green")
  }
  else if(curDir=="W") {
    colMatrix<-rbind(colMatrix, "blue")
  }
}
colMatrix<-na.omit(colMatrix)
colMatrix<-t(colMatrix)
par(xpd=TRUE)
plot(new_dnd[,c(1)], new_dnd[,c(2)], col=colMatrix, type="h", main="Bearing Degrees Over Time", xlab="Time in ms", ylab="Bearing Degrees")
legend(0,-60,c("N", "E", "S", "W"),col=c("black", "red", "green", "blue"),pch=1, horiz=TRUE)

truth_dir_matrix<-matrix()
for(i in 1:NCOL(t_new_dnd)) {
  curDir<-t_new_dnd[,c(i)][4]
  if(curDir=="N") {
    truth_dir_matrix<-rbind(truth_dir_matrix, 0)
  }
  else if(curDir=="E") {
    truth_dir_matrix<-rbind(truth_dir_matrix, 1)
  }
  else if(curDir=="S") {
    truth_dir_matrix<-rbind(truth_dir_matrix, 2)
  }
  else if(curDir=="W") {
    truth_dir_matrix<-rbind(truth_dir_matrix, 3)
  }
}
truth_dir_matrix<-na.omit(truth_dir_matrix)

truth_color_matrix<-matrix()
for(i in 1:NCOL(t_new_dnd)) {
  curDir<-t_new_dnd[,c(i)][4]
  if(curDir=="N") {
    truth_color_matrix<-rbind(truth_color_matrix, "black")
  }
  else if(curDir=="E") {
    truth_color_matrix<-rbind(truth_color_matrix, "red")
  }
  else if(curDir=="S") {
    truth_color_matrix<-rbind(truth_color_matrix, "green")
  }
  else if(curDir=="W") {
    truth_color_matrix<-rbind(truth_color_matrix, "blue")
  }
}
truth_color_matrix<-na.omit(truth_color_matrix)

plot(new_dnd[,c(1)], truth_dir_matrix, col=truth_color_matrix, type="h", main="Ground Truth Direction Over Time", xlab="Time in ms", ylab="Bearing Degrees")
legend(0,-0.5,c("N", "E", "S", "W"),col=c("black", "red", "green", "blue"),pch=1, horiz=TRUE)