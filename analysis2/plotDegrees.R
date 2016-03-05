library('stringr')

normalized_time_interval<-200 # in ms
col_of_interest<-14 # the col storing the variable of interest

setwd('/Users/harry/projects/ece498rr/analysis2/')
wdat<-read.csv('ACTIVITY_05-03-16_00-51-34-051.csv', header=FALSE, na.strings=c("", "null", "NA"))
date_and_degrees<-wdat[,c(1,col_of_interest)]
date_and_degrees<-na.omit(date_and_degrees) #remove na's
date_and_degrees<-t(date_and_degrees)
date_and_degrees<-na.omit(date_and_degrees) #remove na's
new_dnd<-matrix(ncol=2)


#normalize to 200ms timestamps
tsLen<-nchar(date_and_degrees[,c(1)][1])
zeroTS_string<-substr(date_and_degrees[,c(1)][1],tsLen-11,tsLen) #extract ms for "zero" time
zeroTS_string<-str_replace_all(zeroTS_string, ":", "")
zeroTS<-as.numeric(zeroTS_string)
nextTS<-zeroTS+normalized_time_interval
new_dnd<-rbind(new_dnd, c(0, date_and_degrees[,c(1)][2]))
count<-1
for(i in 2:NCOL(date_and_degrees)) {
 curTS_string<-substr(date_and_degrees[,c(i)][1],tsLen-11,tsLen) #extract ms for "zero" time
 curTS_string<-str_replace_all(curTS_string, ":", "")
 curTS<-as.numeric(curTS_string)
 if(!is.na(curTS >= nextTS) && curTS >= nextTS) {
   print(nextTS)
   print(curTS)
   new_dnd<-rbind(new_dnd, c(normalized_time_interval * count, date_and_degrees[,c(i)][2]))
   nextTS<-nextTS+normalized_time_interval
   count<-count+1
 }
}

new_dnd<-na.omit(new_dnd)

#at this point, new_dnd has degree readings at normalized time intervals of normalized_time_interval

plot(new_dnd[,c(1)], new_dnd[,c(2)], type="l", main="Bearing Degrees Over Time", xlab="Time in ms", ylab="Bearing Degrees")