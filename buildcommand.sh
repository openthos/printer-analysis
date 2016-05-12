#!/bin/bash
# Author: Tao<taocr2005@163.com>
# Created: 2016-05-12
# Description:
# This script used to rm the old command and build soft link to busybox.So that you can get a soft link command.

busyboxname=busybox-i686

if [ ! -f $busyboxname ]
then
	echo "Busybox file is not exit"
	exit 1
fi

MD5_busybox=$(/$busyboxname md5sum $busyboxname | awk '{print $1}')

getMD5code()
{
	MD5=$(/$busyboxname md5sum $2 | awk '{print $1}')

}

bin=$(ls /bin)
sbin=$(ls /sbin)
usr_bin=$(ls /usr/bin)
usr_sbin=$(ls /usr/sbin)

List="bin sbin usr_bin usr_sbin"

for dir in $List
do
	if [ $dir == 'usr_bin' ]
	then
		dirpath=usr/bin
	elif [ $dir == 'usr_sbin' ]
	then
		dirpath=usr/sbin
	elif [ $dir == 'bin' ]
	then
		dirpath=bin
	else
		dirpath=sbin
	fi

	eval dir='$'"$dir"

	for curr in $dir
	do
#		echo $curr
#		echo $dirpath	
		getMD5code $busyboxname /$dirpath/$curr
		if [ $MD5 == $MD5_busybox ]
		then
			rm -rf /$dirpath/$curr
			/$busyboxname ln -s /$busyboxname /$dirpath/$curr
		else
			continue
		fi
	done
done	
