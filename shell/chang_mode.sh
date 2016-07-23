#########################################################################
# File Name: chang_mode.sh
# Author: yangmao
# mail: xyym1992@163.com
# Created Time: 2016年05月28日 星期六 15时05分15秒
#########################################################################
#!/bin/bash
function searchfile()
{
	filelist=`ls -l $1 | awk '{if($5!="")print $9}'`
	for filename in $filelist
	do
		filenamepath=$1/$filename
		if [ -d $filenamepath ]
		then
			change_mod $filenamepath
			searchfile $filenamepath
		else
			change_mod $filenamepath
		fi
	done
}
function change_mod()
{
	if [ -r $1 ]
	then
		chmod a+r $1
	fi
	if [ -w $1 ]
	then
		chmod a+w $1
	fi
	if [ -x $1 ]
	then
		chmod a+x $1
	fi
}
searchfile $1
