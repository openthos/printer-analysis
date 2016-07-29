#!/bin/bash
#Author:Tao<taocr2005@163.com>
#Date:2016年 05月 09日 星期一 10:34:06 CST
#Description:
#This script is used to copy the dynamic libraries of a cmd to a aim directory

Liblist=$(ldd $1 |awk '$4==""{next}{print $3}')

if [ ! -f $1 ]
then
	echo "Source file is not exist"
	exit 1
fi

if [ ! -d $2 ]
then
	echo "Target is not a directory"
	exit 1
fi

for curr in $Liblist;
do
	cp --parents $curr $2;
done
