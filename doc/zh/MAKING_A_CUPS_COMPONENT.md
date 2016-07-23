# 这是什么

CUPS数据包作为打印程序的组件（component），需要额外放在系统中，在打印程序第一次运行时会自动解压导入。它是打印程序的核心，打印功能靠它实现。

数据包实际上是自己定制的一个CUPS运行环境，里面封装了CUPS及一些打印机驱动。这些程序能够在openthos系统中独立运行。

# 原理

## 依赖Linux内核

Openthos系统基于Android x86改造而来,而Android系统是一种使用Linux内核的系统，这点和其他Linux类系统一样。在Linux类系统中CUPS[(Common UNIX Printing System，通用Unix打印系统)](http://www.cups.org/)是应用最广的打印解决方案。CUPS是开源的，可以编译安装在任何Linux类系统。Android使用Linux内核，所以有可能性把CUPS移植到Openthos系统中。

CUPS属于Linux用户态程序，从内核的角度来看，它的成功运行，只需要保证系统调用正常即可。即，无论CUPS使用了哪些依赖，比如：glibc，libusb等，最终都依靠系统调用实现功能。所以需要保证两方面：

1. 保证系统调用正常，需要标准Linux内核和足够权限，Android满足。
2. 用户态程序运行正常，需要CPU能正确识别的二进制程序。这点对于Openthos来说，编译x86程序即可保证兼容。

所以，移植CUPS理论上是可行的，事实也如此。

## 使用proot

我们希望让CUPS程序运行在指定的目录里，不对系统产生其他影响。如果让CUPS直接运行在系统中，CUPS必然会用到一些root权限才能读写的目录，很不方便。所以很显然的想到了`chroot`这种改变根目录的技术。但是chroot是需要root权限的，于是我们找到了一个限制更少的工具：PRoot。

PRoot是一种chroot的用户态开源实现工具。（PRoot项目地址: https://github.com/proot-me/PRoot ）

用户不需要拥有系统特权就可以在任意目录建立一个新的根文件系统。从而在建立的根文件系统内做任何事情。从技术上来说，PRoot是依靠ptrace机制实现的。ptrace允许程序在没有拿到系统特权（root）时，父进程观察并修改子进程的系统调用。

所以，我们使用PRoot就能够让CUPS运行在指定的目录里，不必弄脏系统。

# 制作

本节介绍制作一个包含CUPS的独立运行环境的步骤。

为了方便制作，这里选择使用32位的ArchLinux系统，该系统可定制性强，可只安装必须的程序包。并且编译要加入的程序时，直接编译即可，因为直接编译就是x86构架的程序，在基于x86或者amd64构架的Openthos系统是兼容，无需交叉编译。

## 基础环境

### 加入proot

先加入proot可执行文件。**注意：master分支的proot不支持自定义临时文件夹**，会使用 /tmp 目录，而在Android中这是不可用的。详见：[PRoot issue 94](https://github.com/proot-me/PRoot/issues/94)，所以可以使用 next 分支，该分支添加了`PROOT_TMP_DIR`参数可以指定临时目录。

创建好一个文件夹用于制作数据包，这里取名为component。将编译好的proot可执行文件加入，这里取名proot-x86-2，放入根目录。

我将每次执行的命令写入了脚本，命名为proot.sh放入根目录。
``` shell
#!/system/bin/sh

export HOME=/
export LD_HWCAP_MASK=0
export SHELL=/bin/sh
export PROOT_TMPDIR=`pwd`/tmp
export PROOT_TMP_DIR=$PROOT_TMPDIR
export PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin

`pwd`/proot-x86-2 -w / -r `pwd` -b /dev -b /sys -b /proc "$@"

#/usr/sbin/cupsd -f
```

### 加入bash 和 busybox

由于CUPS等程序在运行过程中需要执行很多脚本以及依赖很多命令，所以需要加入脚本执行器和命令。

脚本执行器这里选择 bash 。命令由于有很多，这里选择加入busybox，busybox是一个精简的一百多个Linux命令的工具集。**注意：busybox里含有一个精简的脚本执行器，但是并不能满足要求。**所以先加入busybox之后，将其sh文件删除，替换为bash。

步骤：

1. 进入数据包跟目录，执行sh proot.sh进入自定义环境。在根目录创建 bin sbin ，再分别创建usr/bin usr/sbin 软连接到bin sbin。制作软连接而不是创建文件夹的原因是，很多程序读取命令是指定绝对地址，而这个命令很可能没有被放在指定的bin里，软连接就可避免这种情况。**注意：一定要进入proot环境之后操作，否则软连接的绝对地址有误**。
2. 这里将busybox静态编译的可执行程序命名为busybox-i686放入。在proot环境中执行`./busybox-i686 --install`，该命令自动在各个bin sbin里创建命令硬链接。
3. 由于硬链接在打包的时候会重复占用空间，所有需要替换成软连接。我们编写了`buildcommand.sh`脚本（在数据包根目录）用于自动化替换替换busybox生成的硬链接到软连接。
4. 将静态编译（也可以动态编译，需要放入依赖文件）的bash放入 bin 目录，删除busybox创建的 sh 命令，创建sh软连接到bash。

## 安装CUPS等程序

## 安装

# 其他

## 调试

## 历史

## CUPS相关项目介绍


