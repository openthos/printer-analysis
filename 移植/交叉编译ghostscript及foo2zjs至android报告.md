#目录

一、前言

二、名词解释

三、静态编译、移植

四、测试

五、结语

六、附录
___

#一、 前言

针对之前对于打印方案一的计划:使用ghostscript+foo2zjs进行打印，在对所选打印机HP P1108的调研完成的基础上，进行了静态编译与移植的工作，并进行了测试。PS：测试所使用的是在VMware中所安装的android x86虚拟机。


#二、 名词解释

gcc
>The GNU Compiler Collection，通常简称 GCC，是一套由 GNU 开发的编译器集。GCC 对硬件平台的支持无所不在，它不仅支持 X86处理器架构, 还支持 ARM、MIPS等处理器架构，不过需要使用不同的命令，例如要为 ARM 机器编译程序时命令应为arm-linux-gcc。

adb工具
>adb的全称为Android Debug Bridge，是一个 客户端-服务器端 程序，其中客户端是用于操作的电脑，而服务器端是android设备，其起到调试桥的作用。

Busybox
>BusyBox 是一个集成了一百多个最常用linux命令和工具的软件。BusyBox 包含了一些简单的工具，例如ls、cat和echo等等，还包含了一些更大、更复杂的工具，例grep、find、mount以及telnet。简单的说BusyBox就好像是个大工具箱，它集成压缩了 Linux 的许多工具和命令，也包含了 Android 系统的自带的shell。


#三、 静态编译、移植

由于android x86基于x86硬件架构，所以使用gcc对ghostscript进行静态编译进而移植。

#####1、ghostscript编译

下载ghostscript源码，地址：http://downloads.ghostscript.com/public/ ，实验中所使用为9.14版本。

完成后解压

```
tar -xvf ghostscript-9.14.tar.gz
```

进入ghostscript源码目录，分别执行一下几步创建Makefile文件、编译及安装：

```
64位平台：./configure CFLAGS=-static –enable-static LDFLAGS=-static –disable-shared prefix=指路径  
32位平台： ./configure CFLAGS="-static -m32" --enable-static LDFLAGS="-static -m32" --disable-shared --prefix=指定路径    

make  
make install
```

其中指定路径自己设定，用于安装时将所有的可执行文件放于此路径下，方便移植

#####2、foo2zjs编译

下载foo2zjs源码，执行命令：

```
$ wget -O foo2zjs.tar.gz http://foo2zjs.rkkda.com/foo2zjs.tar.gz
```

完成后解压

```
tar zxf foo2zjs.tar.gz
```

进入foo2zjs源码目录，在Makefile文件末尾添加```CC=gcc```、```CFLAGS+=-static(64位平台) / CFLAGS+=-static -m32(32位平台)```，执行命令：

```
make
```

此项目中无configure，也不需make install，只需要编译出可执行文件即可，且可执行文件在存放在源码目录中。


#####3、移植ghostscript及foo2zjs

打开VMware中的android x86虚拟机，配置其连接主机网络，首先设置虚拟机网络连接处于NAT模式，并查看虚拟网络编辑器中VMnet8的子网IP。进入虚拟机，使用 Alt+Ctrl+F1 快捷键进入console模式（即命令行模式），通过 ```ip a``` 命令查询ip地址，将其设置为与VMnet8的子网IP处于同一网段后即可完成连接。

更改ip的命令：

```
ifconfig eth0 xxx.xxx.xxx.xxx netmask 255.255.255.0 up //改变ip与子网掩码  
route add default gw xxx.xxx.xxx.xxx dev eth0 //两条命令其中的ip地址都需要与子网ip位于同一网段
```

当android x86成功连接网络后就可以使用adb工具从主机的Windows中对android x86进行调试。

下载好adb后配置环境变量，然后打开cmd进行输入

```
adb devices //查看连接的设备，首次输入显示为空  
adb connect xxx.xxx.xxx.xxx  //此处ip即为android x86虚拟机中的ip地址，连接成功后可以通过前一个命令查看  
adb push 原路径 目标路径 //此命令用于将指定Windows中路径的文件放入android x86指定路径中，用其将编译好的ghostscript与foo2zjs放入虚拟机中  
adb shell //进入android x86的命令行中，进行调试
```

放入编译好的ghostscript与foo2zjs后进入android x86命令行，给两者的执行文件执行权限，ghostscript是在bin目录下，foo2zjs就在其根目录下,执行下面命令

```
chmod 755 * //位于ghostscript的bin目录下执行  
chmod  775 arm2hpdl foo2hbpl2 foo2hbpl2-wrapper.in foo2hiperc foo2hiperc-wrapper.in foo2hp foo2hp2600-wrapper.in foo2lava foo2lava-wrapper.in foo2oak foo2oak-wrapper.in foo2qpdl foo2qpdl-wrapper.in foo2slx foo2slx-wrapper.in foo2xqx foo2xqx-wrapper.in foo2zjs foo2zjs-pstops foo2zjs-pstops.sh foo2zjs-wrapper.in foomatic-test freebsd-install getweb.in gipddecode hbpldecode hipercdecode hplj1000 hplj10xx_gui.tcl includer-man lavadecode modify-ppd msexpand oakdecode opldecode ppd-adjust printer-profile printer-profile.sh qpdldecode slxdecode usb_printerid xqxdecode zjsdecode   //位于foo2zjs根目录下执行
```

#四、 测试

#####1、准备工作：

首先在虚拟机上连接打印机，在虚拟机上添加一个usb控制器，将电脑与打印机用usb连接，使用adb进行调试。

输入 lsusb 查询系统中usb设备，如果出现了代表打印机的usb设备代表已成功接入，由于android x86无法自动创建设备节点，所以执行以下命令创建节点:

>busybox mdev -s //虚拟机中所带的busybox可能版本过低而无mdev命令，可放入新版本的busybox执行此命令


查询/dev目录，如出现/dev/usb/lp0文件，则创建设备节点成功。

#####2、ghostscript+foo2zjs进行打印的测试

进入ghostscript的bin目录，执行

```
./gs -q -dBATCH -dSAFER -dQUIET -dNOPAUSE -sbinPAPERSIZE=a4 -r600x600 -sDEVICE=pbmraw -sOutputFile=test_1.pbm chess.ps  
//参数说明： 
"-dBATCH",    执行到最后一页后退出；  
"-dQUIET",    安静的意思，指代执行过程中尽可能少的输出日志等信息。  
"-dNOPAUSE",    每一页转换之间没有停顿；  
"-sPAPERSIZE=a4"，    纸张大小；  
"-r600x600",    图片分辨度；  
"-sDEVICE=pbmraw",    转换输出的文件类型装置，默认值为x11alpha；  
"-sOutputFile=test_1.pbm",    图片输出路径，使用%d或%ld输出页数。
```

表示将chess.ps文件进行格式转换，在当前目录中输出名为test_1.phm的转换后文件

进入foo2zjs的根目录下，执行

```
./foo2zjs -z3 -p9 -r600x600 ../gs-install/bin/test_1.pbm > /dev/usb/lp0
```

表示将test_.phm文件利用输出重定位至/dev/usb/lp0这个打印机设备节点进行打印


打印机成功打印！（具体打印出的测试页在附录中）

#五、 结语
ghostscript及foo2zjs的移植工作暂时完成，目前只针对HP P1108打印机进行了测试，对于其他型号的打印机需要完成对其在Linux上的支持情况调研后再解决。

#六、 附录

#####附录1：
android x86中通过ghostscript+foo2zjs打印的图片
![android x86中通过ghostscript+foo2zjs打印的图片][1]




  [1]:https://github.com/openthos/printer-analysis/blob/master/%E7%A7%BB%E6%A4%8D%2Fraw%2FP60326-142446.jpg


