#!/system/bin/sh

export HOME=/
export LD_HWCAP_MASK=0
export SHELL=/bin/sh
export PROOT_TMPDIR=`pwd`/tmp
export PROOT_TMP_DIR=$PROOT_TMPDIR
export PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin

#/data/data/com.github.openthos.printer.testexec/files/component_1/proot-x86-2 -w / -r /data/data/com.github.openthos.printer.testexec/files/component_1 -b /dev -b /sys -b /proc cat /dev/bus/usb/001/002

`pwd`/proot-x86-2 -w / -r `pwd` -b /dev -b /sys -b /proc "$@"

#/usr/sbin/cupsd -f

