###本周工作
本周主要完成了ghostscript及foo2zjs的静态编译及移植工作，并在android x86上通过usb连接打印机进行了实验，实验结果成功打印了测试页，与Windows上打印出的测试页无不同之处。

#####一、ghostscript的静态编译

之前的过程中使用了arm-linux-gcc进行编译，但是编译完成后不能够在android x86上运行，之后了解到需要打开android中中支持arm程序的选项，但是在实际测试中发现打开了后仍然无法运行，于是放弃使用arm-linux-gcc进行移植，改用gcc直接进行静态编译。


在使用configure生成Makefile时，执行：

>./configure CFLAGS=-static –enable-static LDLAGS=-static –disable-shared -prefix=指定路径

从而生成用于静态编译的Makefile文件。

之后是正常的```make```与```make install```用于编译安装ghostscript，由于在configure阶段设置了```prefix```参数，于是安装的位置会定位所指定的路径，便于移植。

由于目的是移植与测试，所以将ghostscript的share下的man目录进行了删除，其所存文件为用于man手册的帮助文档。

#####二、foo2zjs的静态编译

在下载了foo2zjs的源码后发现，其并没有configure程序，所以无法运用ghostscript的在configure执行命令中添加参数的方法，于是选择直接对Makefile文件进行修改。


在Makefile中添加```CC=gcc```及```CFLAGS+=-static```两句，用于指定编译器gcc以及C编译器的静态编译选项。

之后执行```make```命令进行编译，编译完成后在源码目录中直接得到了编译得出的多个可执行文件。

#####三、移植

完成编译后需要进行移植的工作，之前在虚拟机中安装了android x86操作系统用于测试。

由于之前没有接触过android方面的工作，所以并不清楚如何在android x86中调出命令行以及如何将文件移植入android系统中，询问何兴鹏同学后，得知adb工具可以完成这些工作，于是开始学习adb工具(Android Debug Bridge)的使用。


在我的机器的Windows系统上通过adb工具连接android x86进行调试，将编译好的ghostscript和foo2zjs放入了android的/data/user目录下。

移植完成后发现被移植进入的可执行文件都失去了执行权限，于是对照位于Linux中编译完成的文件利用chmod命令给予相应权限。

分别对ghostscript/bin目录下的gs以及foo2zjs目录下的foo2zjs进行了执行测试，成功执行。

#####四、测试

完成移植后开始测试在android x86下是否能够通过ghostscript进行转换文件格式再通过foo2zjs驱动打印机进行打印，这里所用的测试文件是在ghostscript中自带的测试文件chess.pdf,其位于ghostscript/share/ghostscript/9.14/examples目录下。

关于测试的过程是通过ghostscript进行测试文件的格式转换，之后通过foo2zjs将转换后的文件通过命令行指定打印参数后通过输出重定向至打印机的设备节点，从而将需打印的文件传送至打印机进行打印。

首先在虚拟机中添加usb控制器用于连接打印机，并连接PC与打印机。在/data/user/ghostscript/bin目录下执行

>gs -q -dBATCH -dSAFER -dQUIET -dNOPAUSE -sPAPERSIZE=a4 -r600x600 -sDEVICE=pbmraw -sOutputFile=test_1.pbm chess.ps

将测试文件chess.pdf进行格式转换并命令为test_1.pbm，输出在当前目录中，执行完毕后在当前目录下确实出现了test__1.phm文件，ghostscript运行成功。

接下来进入foo2zjs的目录下执行

>./foo2zjs -z3 -p9 -r600x600 ../gs-install/bin/test_1.pbm > /dev/usb/lp0```

但是执行后出现错误，错误信息提示无lp0设备文件。

查看/dev目录后发现确实无lp0文件（打印机设备节点）也无usb目录，于是查找原因。使用lsusb命令查看系统中的USB设备,发现有打印机的USB设备信息，于是推断android x86下无法自动创建设备节点，因此USB连接打印机后/dev目录下无lp0这个打印机的设备节点，那么需要手动创建设备节点。

Busybox是一个集成了大量常用linux命令及工具的软件，在android x86的测试环境中也带有此软件，于是使用其中的mdev命令创建设备节点（mdev通过扫描系统/sys/class/目录获取设备信息，进而在/dev/下创建节点），执行时发现因测试环境中所带的busybox因版本较低而没有mdev命令，于是放入新版本的busybox程序执行
>busybox mdev -s

再次查看/dev目录，发现其中出现了usb目录且其中有了lp0这个打印机设备节点。

回到foo2zjs目录下再次执行```./foo2zjs -z3 -p9 -r600x600 ../gs-install/bin/test_1.pbm > /dev/usb/lp0```,打印机成功驱动并打印出了测试文件，将打印出的文件与在linux下测试打印机所打印出的文件对比，没有不同。

###下周计划
由于issue #9的任务目前还没有完成，所以下周先进行这方面关于要求实现的打印机在Linux上的支持情况的调研。
