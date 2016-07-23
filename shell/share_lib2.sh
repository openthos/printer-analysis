#########################################################################
# File Name: share_lib.sh
# Author: yangmao
# mail: xyym1992@163.com
# Created Time: 2016年05月18日 星期三 15时28分32秒
#########################################################################
#!/bin/bash
#!/bin/bash
destdir=$1
function copylib()
{
	filename=$1
	ldd $filename
	test=$?
	if [ $test==0 ]
	then
	liblist=`ldd $filename | awk '{if($3~"/")print $3}'`
		for libname in $liblist
		do
			dir_name=`dirname $libname`
			endpath=$destdir$dir_name
			finalname=$destdir$libname
			if [ ! -e $finalname ]
			then
				if [ -d $endpath ]
				then
					cp -d $libname $endpath
				else
					mkdir -p $endpath
					cp -d $libname $endpath
				fi
				if [ -L $libname ]
				then
					sourcename=`ls -l $libname | awk '{print $11}'`
					sourcepath=$dir_name/$sourcename
					cp -d $sourcepath $endpath
				fi
			copylib $libname
			fi
		done
	else
		return
	fi
}
function searchfile()
{
	if [ -d $1 ]
	then
		filelist=`ls $1`
		for filename in $filelist
		do
			filenamepath=$1/$filename
			searchfile $filenamepath
		done
	else
		copylib $filenamepath 
	fi
}
searchfile $1





