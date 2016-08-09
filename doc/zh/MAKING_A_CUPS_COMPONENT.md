# 1 这是什么

CUPS数据包作为打印程序的组件（component），需要额外放在系统中，在打印程序第一次运行时会自动解压导入。它是打印程序的核心，打印功能靠它实现。

数据包实际上是自己定制的一个CUPS运行环境，里面封装了CUPS及一些打印机驱动。这些程序能够在openthos系统中独立运行。

# 2 原理

## 2.1 依赖Linux内核

Openthos系统基于Android x86改造而来,而Android系统是一种使用Linux内核的系统，这点和其他Linux类系统一样。在Linux类系统中CUPS[(Common UNIX Printing System，通用Unix打印系统)](http://www.cups.org/)是应用最广的打印解决方案。CUPS是开源的，可以编译安装在任何Linux类系统。Android使用Linux内核，所以有可能性把CUPS移植到Openthos系统中。

CUPS属于Linux用户态程序，从内核的角度来看，它的成功运行，只需要保证系统调用正常即可。即，无论CUPS使用了哪些依赖，比如：glibc，libusb等，最终都依靠系统调用实现功能。所以需要保证两方面：

1. 保证系统调用正常，需要标准Linux内核和足够权限，Android满足。
2. 用户态程序运行正常，需要CPU能正确识别的二进制程序。这点对于Openthos来说，编译x86程序即可保证兼容。

所以，移植CUPS理论上是可行的，事实也如此。

## 2.2 使用proot

我们希望让CUPS程序运行在指定的目录里，不对系统产生其他影响。如果让CUPS直接运行在系统中，CUPS必然会用到一些root权限才能读写的目录，很不方便。所以很显然的想到了`chroot`这种改变根目录的技术。但是chroot是需要root权限的，于是我们找到了一个限制更少的工具：PRoot。

PRoot是一种chroot的用户态开源实现工具。（PRoot项目地址: https://github.com/proot-me/PRoot ）

用户不需要拥有系统特权就可以在任意目录建立一个新的根文件系统。从而在建立的根文件系统内做任何事情。从技术上来说，PRoot是依靠ptrace机制实现的。ptrace允许程序在没有拿到系统特权（root）时，父进程观察并修改子进程的系统调用。

所以，我们使用PRoot就能够让CUPS运行在指定的目录里，不必弄脏系统。

# 3 制作

本节介绍制作一个包含CUPS的独立运行环境的步骤。

为了方便制作，这里选择使用32位的ArchLinux最简系统，该系统可定制性强，可只安装必须的程序包。并且编译要加入的程序时，直接编译即可，因为直接编译就是x86构架的程序，在基于x86或者amd64构架的Openthos系统是兼容，无需交叉编译。

在这里尽可能的给出编译时所需的依赖，但肯定会有很多遗漏需要自行解决。

## 3.1 基础环境

### 3.1.1 加入proot

先加入proot可执行文件。**注意：master分支的proot不支持自定义临时文件夹**，会使用 /tmp 目录，而在Android中这是不可用的。详见：[PRoot issue 94](https://github.com/proot-me/PRoot/issues/94)，所以可以使用 next 分支，该分支添加了`PROOT_TMP_DIR`参数可以指定临时目录。

创建好一个文件夹用于制作数据包，这里取名为component。将编译好的proot可执行文件加入，这里取名proot-x86-2，放入根目录。

我将每次执行的命令写入了脚本，命名为[proot.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/proot.sh)放入根目录。
``` shell
#!/system/bin/sh

export HOME=/
export LD_HWCAP_MASK=0
export SHELL=/bin/sh
export PROOT_TMPDIR=`pwd`/tmp
export PROOT_TMP_DIR=$PROOT_TMPDIR
export PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin

`pwd`/proot-x86-2 -w / -r `pwd` -b /dev -b /sys -b /proc "$@"
```

### 3.1.2 加入bash 和 busybox

由于CUPS等程序在运行过程中需要执行很多脚本以及依赖很多命令，所以需要加入脚本执行器和命令。

脚本执行器这里选择 bash 。命令由于有很多，这里选择加入busybox，busybox是一个精简的一百多个Linux命令的工具集。**注意：busybox里含有一个精简的脚本执行器，但是并不能满足要求。**所以先加入busybox之后，将其sh文件删除，替换为bash。

步骤：

1. 进入数据包跟目录，执行sh proot.sh进入自定义环境。在根目录创建 /bin /sbin ，再分别创建/usr/bin usr/local/bin 到 /bin 以及 /usr/sbin /usr/local/sbin 软连接到/bin /sbin。制作软连接而不是创建文件夹的原因是，很多程序读取命令是指定绝对地址，而这个命令很可能没有被放在指定的bin里，软连接就可避免这种情况。**注意：一定要进入proot环境之后操作，否则软连接的绝对地址有误**。
2. 这里将busybox静态编译的可执行程序命名为busybox-i686放入。在proot环境中执行`./busybox-i686 --install`，该命令自动在各个bin sbin里创建命令硬链接。
3. 由于硬链接在打包的时候会重复占用空间，所有需要替换成软连接。我们编写了[buildcommand.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/buildcommand.sh)脚本（在数据包根目录）用于自动化替换替换busybox生成的硬链接到软连接。
4. 将静态编译（也可以动态编译，需要放入依赖文件）的bash放入 bin 目录，删除busybox创建的 sh 命令，创建sh软连接到bash。

## 3.2 安装CUPS等程序

编译安装这些程序时，先动态编译，之后再使用脚本把依赖复制过来。这些程序的介绍请看最后 CUPS相关项目介绍 。

### 3.2.1 libusb-1.0.9

编译安装libusb到系统以替换其自带libusb是因为Archlinux自带的libusb可能加入了一些特性，导致cups无法在Android环境下成功读写usb端口。

```
./confgiure
make
make install
```

### 3.2.2 cups-2.1.2

参考命令:
```
make distclean                                    若是第二次编译清除上次结果
./configure --disable-gnutls --disable-gssapi --disable-dbus --disable-dnssd -disable-launchd
make
make BUILDROOT=/home/deep/component_10 install    安装到数据包里
make install                                      安装到当前系统
```
同时安装到当前系统的目的是为了接下来安装其他程序方便，因为有一些依赖要用到。

**注意：**

屏蔽 cupsd.conf 中的`AuthType`和`Require`行，并且修改`Allow @LOCAL`为`Allow all`，否则由于用户权限问题无法操作cups。

屏蔽 cups-files.conf .中的SystemGroup行，否则在Android中由于用户组问题无法运行cups。

由于Openthos中的tar命令问题，会导致解压后部分权限丢失。cups网页文件的权限必须是所有用户都有读权限的，所以我们编写了[chang_mode.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/chang_mode.sh)脚本自动修复网页文件的权限。网页文件位于`/usr/share/cups`文件夹，连同cups文件夹都进行修复。

### 3.2.3 ghostscript-9.18

参考命令:
```
make distclean
./configure --disable-sse2 --disable-dbus --disable-freetype --disable-fontconfig --disable-gtk --disable-bswap32 --disable-byteswap-h
make
DESTDIR=/home/deep/component_10  make install
make install
```

### 3.2.4 cups-filters-1.8.2

先到ghostscript的ijs目录，单独编译ijs，执行：
```
sudo autoreconf -ivf
./configure
make
sudo make install
```
从 pacman （ArchLinux的包管理器）安装python glib2 poppler依赖。

到cups-filters-1.8.2源码根目录执行：
```
IJS_CFLAGS=-L/usr/local/lib IJS_LIBS=-lijs ./configure --enable-static --disable-avahi --disable-ldap --disable-dbus --disable-imagefilters --without-jpeg --without-png --without-tiff
make
DESTDIR=/home/deep/component_10  make install
make install
```

### 3.2.5 foo2zjs

foo2zjs可以支持一些激光打印机，比如HP的。由于未能找到foo2zjs的版本号，所以请到官网下载最新版。

执行`sudo pacman -S bc`安装bc软件包。
```
make
DESTDIR=/home/deep/component_10  make install
make install
```

### 3.2.6 Pantum官方闭源驱动

从官网搜集下载P和M两个系列的打印机Linux驱动：

1. 20150901-Pantum-P2200-P2500-Series-Linux-Driver-V1.80-2
2. Pantum-M6200-M6500-M6550-M6600-MS6000-Series-LINUX-Driver-V1-4-0-tar

Pantum的驱动是闭源的，会解压出 deb 文件，使用i386版本。

使用`ar`命令解压deb，如：`ar -x Pantum-M6500-Series-3.0.i386.deb`。

解压 data.tar.gz 文件，按照解压出的目录结构，将文件都复制进数据包目录（包含PPD和filter两种文件）。

注意：奔图的PPD文件有空格，需要全部去除，否则打印程序解析会出错，我们这里将空格替换为`_`。

### 3.2.7 Eposn官方开源驱动

由于最新的Epson驱动是以守护进程的形式运行，无法找到任何PPD文件。我们不希望额外的程序一直运行在后台，所以决定先使用其旧版驱动。

从Arch的程序库中找到了两个驱动

1. epson-inkjet-printer-escpr：https://aur.archlinux.org/packages/epson-inkjet-printer-escpr/
2. epson-inkjet-printer-201401w：https://aur.archlinux.org/packages/epson-inkjet-printer-201401w/

epson-inkjet-printer-escpr正常编译安装即可。

epson-inkjet-printer-201401w缺少库libjpeg62，下载编译好的 libjpeg62-62.1.0-30.5.1.i586.rpm ，解压出来cpio。
执行`cpio -idmv < libjpeg62-62.1.0-30.5.1.i586.cpio`,进入usr/lib/，`cp * ~/component_10/usr/lib/`。
编译好后不用改变目录结构，就是要放在/opt下面。

**注意：该程序是 Linux Standard Base (LSB) 程序，使用的是 /lib/ld-lsb.so.3 链接器**，因此执行`ln -s  /lib/ld-linux.so.2 /lib/ld-lsb.so.3`链接到 ld-linux.so 。

### 3.2.8 Hpcups && Hplip plugin

#### 编译安装 hpcups

解压官网下载的 hplip-3.16.2

make distclean
./configure --disable-qt4 --disable-gui-build --disable-doc-build --disable-fax-build --disable-dbus-build --disable-network-build --disable-scan-build --enable-cups-ppd-install
make
DESTDIR=/home/deep/component_10 make install

值得一提的是加上 --enable-hpcups-only-build 参数，就会只留下hpcups驱动，没有python等脚本写的专有功能。但是也就不能用hplip plugin了。之后可以手动添加配置文件hplip.conf（不建议），也可以去掉该参数编译自动会生成改文件。

#### 解压安装 hplip plugin

hplip plugin 是hp的闭源驱动部分，对于很多激光打印机都需要该部分才能够打印。
从 http://www.openprinting.org/download/printdriver/auxfiles/HP/plugins/?C=M;O=A 下载 hplip-3.16.2-plugin.run

解压run文件 ``sh *.run --noexec --target 文件夹``

根据文件 plugin.spec 里的指示位置，将so文件和ppd复制到数据包里的相应位置。

文件夹 prnt 在 ubuntu 中放到 /usr/share/ 文件夹，但是我们需要存放在 /usr/local/share/ 目录。

#### 编写地址转换脚本

对于使用 hplip plugin 的打印机，必须要使用 hp 前端过滤器，不能直接使用 usb ，否则会提示 this module is designed to work with hp printers only 。

在ubuntu中，系统会自动转换地址为 hp 开头，从而使用 hp 前端过滤器。因此我们编写地址转换脚本，进行动态转换。

使用 hpcups 驱动的打印机，会调用 /usr/lib/cups/filter/hpc​ups 后端过滤器，因此我们编写脚本替换hpcups，执行转换操作之后再调用真正的 hpcups 。

在数据包的 /usr/lib/cups/filter/ 文件夹，重命名 hpcups 为 hpcups1 ，创建 hpcups 脚本文件，文件内容详见：[dev 分支 /shell/hpcups](https://github.com/openthos/printer-analysis/blob/dev/shell/hpcups)。

hpcups脚本的功能就是检测地址是否为 usb 开头，如果是则转换为 hp 开头。 hp 开头的地址结构为``hp:/usb/PRINTER_NAME?serial=DEVICE_URI``。之后再调用 hpcups1 。

### 3.2.9 嘉华龙马打印机驱动

...

### 3.2.10 samba

添加samba是为了能够与Windows进行网络打印，Linux与Windows上打印系统的实现各不相同，因此想要实现两者通信，samba是必须的。

这里我们并不将samba服务器完全加入，而只加入我们需要用到的一部分。

步骤：

1、将所需要的samba可执行文件```smbspool```拷贝至数据包的可执行文件目录中，执行```cp /usr/bin/smbspool componet_10/usr/bin/```

2、将samba执行所需要的配置文件拷贝至相应目录下，执行```cp /etc/samba/smb.conf componet_10/etc/samba/smb.conf``` 

3、建立Cups后台可执行文件与```smbspool```的软连接，从而让Cups可以去调用其进行与Windows连接，执行```ln -s `which smbspool` /usr/lib/cups/backend/smb```

4、使用[cmdldcopy.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/cmdldcopy.sh)脚本将```smbspool```执行所需要依赖的动态库进行拷贝，执行```./cmdldcopy.sh /usr/bin/smbspool ./componet_10/```

## 3.3 复制依赖

如果所需的程序都加入了，最后需要补全程序缺少的依赖，因此我们编译了[share_lib2.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/share_lib2.sh)脚本来递归遍历所有动态链接程序的依赖，包括so库文件的so库依赖。

原理是依次调用`ldd`命令解析依赖并复制。

## 3.4 打包

```
tar -zvcf component_10.tar.gz component_10
```

我们在开发中，通常会用数字命名数据包，以区分不同的版本，防止混淆。最后放入系统时再统一名称。

# 4 其他

## 4.1 调试

为了便于调试数据包里程序的问题，除了cups的日志外，可使用strace程序记录系统调用。因此，我们还放入一个静态编译的strace在数据包中。

通常，先在ArchLinux中调试通过，再放入Openthos中测试。

示例：
```
sh proot.sh
strace -f cupsd -f &> logPrint
```
这样就能记录所有的系统调用到logPrint文件中，一定要加上`-f`参数追踪子进程的系统调用。

## 4.2 CUPS相关项目介绍

请查看：https://github.com/openthos/printer-analysis/blob/master/doc/RELATED_PROJECTS.md

## 4.3 当时的开发环境

在开发整个数据包时，使用的是ArchLinux虚拟机。为了保证数据包继续开发时的兼容性，请下载VMware 12和我们提供的镜像文件。
当然也可以选择从头制作，则不需要一致的制作环境。

镜像文件地址：

* 地址1：
百度云链接：http://pan.baidu.com/s/1nuX8spN 密码：l2xk

* 地址2：
实验室内网docker服务器中：/home/lh/hxp/archlinux 文件夹。

使用说明请查看镜像文件地址中的 readme.txt 文件。
