#!/bin/bash
# Author: Tao<taocr2005@163.com>
# Created: 2016-06-19
# Description:
#This script is used to hold print task or release print task

port=$[$#-1]
for((i=1;i<"$port";i++))
do
	eval "ipptool http://localhost:""$""{""$[$#-1]""}""/jobs -d job-id=""$""{""$i""}"" ""$""{""$#""}""-job.test"
done
