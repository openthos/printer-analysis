### 目录
在此列出本文目录方便阅读

一 前言

二 名词解释

三 打印方案一： ghostscript + foo2zjs （备选方案）

四 打印方案二： 使用CUPS系统管理

五 版本差异介绍（重要）

六 其他

七 结语

八 附录

# 一 前言

由于在Linux中的打印驱动各种各样，虽然都是基于CUPS打印系统，但是具体打印实现方式区别较大,对于其他打印机会有很大不同,所以我决定把CUPS调研的内容和具体的打印机写在一起。本文的工作是基于**HP LaserJet P1108**打印机。

# 二 名词解释

## （一）专有名词

### PS
在这里PS是PostScript的缩写。
> PostScript是一种编程语言，最适用于列印图像和文字（无论是在纸、胶片或非物质的CRT都可）。用现今的行话讲，它是页面描述语言。它即可以像程序代码一样具有可读性，又能表示出可任意放大和缩小的矢量图。 ------ PostScript百度百科

以ps为后缀的文件即代表用该语言描述的文件，存放图像信息。cups可读取该类型文件，打印里面的内容。值得注意的是android打印框架输出的文件也是ps文件，但与CUPS中的ps是否有差别还有待调查。也就是说，**我们的目标是能够打印ps文件**。

### PPD
> CUPS uses PostScript Printer Description ("PPD") files to describe printer capabilities and features and a wide variety of generic and device-specific programs to convert and print many types of files. ------ 来自 CUPS 项目中的 README.txt

也就是说，PPD文件给CUPS提供了如何把需要打印的文件转换成打印机能识别的文件的信息。PPD文件通常需要相应的filter（过滤器？翻译为解释器更易懂）才能运作。

## （二）相关项目

这里先列出与打印有关的项目，简要解释及项目主页，后面可能会用到。以下简短解释皆来自官方主页。

### Gutenprint

High quality drivers for Canon, Epson, Lexmark, Sony, Olympus, and PCL printers for use with CUPS, Ghostscript, Foomatic, and GIMP.

http://gimp-print.sourceforge.net/

### ghostscript

an interpreter for the PostScript language and for PDF

http://www.ghostscript.com/

### foo2zjs

a linux printer driver for ZjStream protocol

e.g. Minolta magicolor 2200/2300/2430 DL,

HP LaserJet 1018/1020/1022/P2035,

HP LaserJet Pro CP1025nw,

HP LaserJet Pro P1102/P1102w/P1566/P1606dn

http://foo2zjs.rkkda.com/

### HPLIP

HP Linux Imaging and Printing

Print, Scan and Fax Drivers for Linux

http://hplipopensource.com/hplip-web/index.html

### foomatic

Foomatic is a database-driven system for integrating free software printer drivers with common spoolers under Unix.

http://www.linuxfoundation.org/collaborate/workgroups/openprinting/database/foomatic

这里面包含四个子项目

* foomatic-filters
The universal print filter "foomatic-rip", used by spoolers to convert PostScript job data into the printer's native format as described by a printer/driver-specific PPD file.

* foomatic-db-engine
Foomatic's database engine generates PPD files from the data in Foomatic's XML database. It also contains scripts to directly configure print queues and handle jobs.

* foomatic-db
The collected knowledge about printers, drivers, and driver options in XML files, used by foomatic-db-engine to generate PPD files. It also contains manufacturer-supplied PPD files which got released under free software licenses.

* foomatic-db-nonfree
Foomatic database extension consisting of manufacturer-supplied PPD files released under non-free licenses which restricts them in how they can get redistributed.

### cups-filters

This project provides backends, filters, and other software that was once part of the core CUPS distribution but is no longer maintained by Apple Inc.

From CUPS 1.6.0 on, this package is required for using printer drivers with CUPS under Linux. With CUPS 1.5.x and earlier this package can be used optionally to switch over to PDF-based printing.

http://www.linuxfoundation.org/collaborate/workgroups/openprinting/cups-filters

### CUPS

CUPS is the standards-based, open source printing system developed by Apple Inc. for OS X® and other UNIX®-like operating systems. CUPS uses the Internet Printing Protocol (IPP) to support printing to local and network printers.

https://www.cups.org/





# 三 打印方案一： ghostscript + foo2zjs （备选方案）

## （一）方案介绍

ghostscript提供了pdf和ps文件的读取及转换为打印机能够识别的文件等等功能。其中一个重要的可执行文件（功能）是gs，它能够把pdf和ps文件转换成打印机能识别的文件。我们利用gs先对ps文件进行转换。

之前计划使用hpcups（hpijs）驱动，但是却发现对于近年来的激光打印机，需要使用HP的闭源驱动才能打印，所以放弃使用。经过查找，发现foo2zjs项目提供了一些打印机的驱动。实验发现，对于HP P1108型号打印机，使用foo2zjs驱动可以成功打印。foo2zjs在该方案中的作用为把之前gs输出的文件以命令的方式发送给打印机，从而实现打印。

打印实现路径为 **gs(ghostscript) -> foo2zjs -> HP P1108打印机**

## （二）具体步骤

该方案参考下文实现
> [嵌入式打印机系统移植支持HP LaserJet 1020 plus 激光打印机和HP DeskJet 1010喷墨打印机][1]

本文这里给出的步骤是在桌面Linux（ArchLinux）上调研（测试打印）的步骤，不是实际移植的步骤。
软件包版本为ghostscript-9.18和foo2zjs 2015-10-24。此方案对ghostscript版本不敏感，高版本也可以，后面版本部分会有详细说明。

1.编译ghostscript

这里的参数并非最佳参数，仅供参考
```
#此时在ghostscript源代码根目录
./configure --disable-sse2 --disable-dbus --disable-fapi --disable-freetype --disable-threading --disable-gtk --disable-bswap32 --disable-byteswap-h
make
DESTDIR=/home/deep/testgho make install  这里是把把输出的文件统一放入testgho文件夹，方便观察
```

2.编译foo2zjs

foo2zjs需要可运行的ghostscript才能编译，所以导出gs所在目录。当然，如果系统安装了ghostscript，命令则已在系统的环境变量里，不需要手动导出。
```
#此时在foo2zjs源代码根目录
export PATH=/home/deep/testgho/usr/loca​l/bin:$PATH
make
```
该项目没有configure，另外这里不需要make install，我们只要编译出的可执行文件即可。

3.运行gs转换

```
#此时在编译好的ghostscript根目录
gs -q -dBATCH -dSAFER -dQUIET -dNOPAUSE -sPAPERSIZE=a4-r600x600 -sDEVICE=pbmraw -sOutputFile=test_1.pbm chess.ps
```
将chess.ps转换为test_1.pbm文件。test_1.pbm文件待会就要作为foo2zjs的输入。

4.使用foo2zjs打印

由于直接和usb端口通信，需要root用户。并且这时需要插上打印机。
```
#此时在编译好的foo2zjs根目录
su root
./foo2zjs -z3 -p9 -r600x600 /home/deep/Downloads/test_1.pbm > /dev/usb/lp0 这里端口是lp0，实际可能有所不同
```
就可以直接把数据发送给打印机端口了，打印成功。

关于-z参数这里得说明，其他参数参考man手册。（需安装该包，man手册才会有）
```
-z model          Model: [0]
                    0=KM 2300DL / HP 1000 / HP 1005
                    1=HP 1018 / HP 1020 / HP 1022
                    2=HP Pro P1102 / P1566 / P1606dn
                    3=HP Pro CP102?nw
```
-z代表打印机型号，我们可以发现这里并没有HP P1108。但是经过测试，选择-z3即HP Pro CP102?nw型号可以打印。说明HP P1108和HP Pro CP102?nw的协议可能相同。

打印的图片请见文章最后附录1。

## （三） 方案总结

该方案仅使用了ghostscript和foo2zjs就实现了打印，方便。但是该方案对于存在多种类型打印机的情况就不好管理了，没有统一的管理程序。因此引入方案二。该方案标记备选是因为如果CUPS在android上工作不好，则使用该方案并自己写个管理程序。

# 四 打印方案二： 使用CUPS系统管理

该方案是在方案一的基础上添加CUPS系统，对于打印进行管理。

## （一） CUPS打印的可行路径

这里展示一张图，详细说明了CUPS打印的每条可行路径。需要注意，该图对应的版本较旧，对于新版本的CUPS并不完全相同。对于cups 1.6及以上版本，cups功能的实现较大依赖cups-filter项目。这点在后面版本差异上详细说明。

![CUPS打印路径][2]

从上到下任意一条路径皆可实现打印，但对于一款打印机并不是每条路径都可用。Input Formats指输入的文件类型，这里可以看到列出了Text，PDF和HP/GL类型。对于PDF类型会使用PDFtops对PDF进行转换，转换成mime类型为appliaction/postscript的postscript文件。PDFtops就一个PreFilters，意为预转换器（前台过滤器）。后面还有很多转换路径，最终送到CUPS backends（后端过滤器）。由后端过滤器将打印内容输送给打印机，方案一中的foo2zjs就属于一种后端过滤器。

## （二） 方案介绍

### 1 软件版本

本实验均基于以下版本，关于版本介绍见后面版本差异章节。

* cups-1.5.4

* ghostscript-9.07

* foomatic-filters-4.0.17

* foo2zjs 2015-10-24

### 2 测试页介绍

这里以CUPS自带的测试页打印为例。测试页文件为/usr/share/cups/data/testprint(默认位置)。该文件里面只是列出了要打印的内容，文件结构类似shell。如下，第一行``#CUPS-BANNER``表明需要解释处理。
```
#CUPS-BANNER
2 Show printer-name printer-info printer-location printer-make-and-model printer-driver-name print er-driver-version paper-size imageable-area
3 Header Printer Test Page
4 Footer Printer Test Page
5 Notice CUPS 1.5.4.
6 Image images/cups.png
7 Image images/color-wheel.png
```
在CUPS管理界面点击print test page还是手动使用lp或者lpr命令指定打印testprint文件，效果都相同。

### 3 PPD文件简要查看

CUPS根据PPD的指示来执行打印。从方案一中得知，实验的打印机型号HP P1108在foo2zjs中是没有的，我们选择使用HP LaserJet Pro CP1025nw型号的PPD文件。

HP-LaserJet_Pro_CP1025nw.ppd（位于foo2zjs源码PPD目录里）的部分内容如下：
```
...
*cupsFilter:	"application/vnd.cups-postscript 0 foomatic-rip"
...
*FoomaticRIPCommandLine: "foo2zjs-wrapper -z3 -P -L0 %A"
...
```
其中有两行值得注意。cupsFilter指定了mime类型application/vnd.cups-postscript的文件处理器为foomatic-rip。我们结合CUPS打印的可行路径图，可以发现该路径指向Ghostscript，实验中也确实如此。FoomaticRIPCommandLine指定了foomatic的转换命令。这里调用了foo2zjs-wrapper命令，该命令作用是：
> a shell script (compatible with foomatic) which runs ghostscript and foo2zjs in a pipeline.
 ------ 官方主页 http://foo2zjs.rkkda.com/

foo2zjs-wrapper文件是一个shell脚本，关于**该shell脚本在android上能否正常运行还有待实验**。
foo2zjs-wrapper实际就是辅助调用ghostscript和foo2zjs命令。

更多PPD的解释见
> [Chapter 6. PPD Format Extensions][3]

### 4 实现路径

该方案的实现路径如下：

testprint(待打印文件) -> texttops -> pstops -> **foomatic-rip -> foo2zjs-wrapper(包含 gs -> foo2zjs)** -> HP P1108打印机

这里加粗的部分代表这些功能（命令）没有包含在CUPS包中。
* foomatic-rip包含在cups-filter和foomatic-filters项目里，二者取其一即可。实验中cups-filter编译失败，使用foomatic-filters。
* foo2zjs-wrapper，foo2zjs包含于foo2zjs项目中。
* gs包含在ghostscript项目。

所以我们需要编译cups，ghostscript，foomatic-filters和foo2zjs。

## （三） 具体步骤

同样，这里给出的步骤是在桌面Linux（ArchLinux）上调研（测试打印）的步骤，不是实际移植的步骤。参数不是最佳选项，仅供参考。

### 1 编译cups

下载解压cups-1.5.4-source.tar.gz
```
#在cups源码根目录
make distclean 若重新编译可运行该命令清除数据
./configure --disable-dnssd -disable-launchd --prefix=/indep1 --exec-prefix=/dep1 --disable-shared --enable-static
make
make BUILDROOT=/home/deep/test6 install
```
这里我将二进制执行文件和其他文件分开到indep1和dep1目录（仅仅是为了方便观察），并且最后输出到test6文件夹。

在/home/deep/test6文件夹里是无法直接运行的，需要移动（或复制）到根目录下，/indep1和dep1。

### 2 编译ghostscript

下载解压ghostscript-9.07.tar.bz2
```
#ghostscript源码根目录
make distclean 若重新编译可运行该命令清除数据
./configure --disable-sse2 --disable-dbus --disable-freetype --disable-gtk --disable-bswap32 --disable-byteswap-h --with-install-cups
make
DESTDIR=/home/deep/testgho3 make install
```
需要注意``--with-install-cups``参数（9.07及以下版本有效），该参数会编译出``gstoraster.convs``和``gstoraster`` filter等文件。列出的两个文件提供了``application/vnd.cups-pdf``和``application/vnd.cups-postscript``的转换功能，这在cups1.6及以上版本是需要的。而cups1.5版本的testprint不需要处理pdf，所以可以不需要。

编译好之后,**将gs等一堆工具复制进cups的bin目录**。
```
cd /dep1/bin
sudo cp ~/testgho3/usr/local/bin/* ./
```

### 3 编译foomatic-filters

下载解压foomatic-filters-4.0.17
```
#foomatic-filters源码根目录
make clean 重新编译时需要执行清除
./configure --disable-dbus --disable-file-converter-check
make
DESTDIR=/home/deep/testfoofil make install-cups
```
``--disable-file-converter-check``该参数不加，会configure失败，提示:
```
configure: error: cannot find a2ps, enscript, mpage, or CUPS' texttops. You need to have at least one installed
```
我选择忽略检查。

这里``make install-cups``表示仅安装cups支持。

```
cp foomatic-​rip /dep1/lib/cups/filter/foomatic-​rip
```
拷贝foomatic-​rip到CUPS中。

### 4 编译foo2zjs

下载解压foo2zjs
```
#foo2zjs根目录
export PATH=/home/deep/testgho3/usr/loca​l/bin:$PATH
make
```
直接make就行。
复制``foo2zjs`` ``foo2zjs-pstops`` ``foo2zjs-wrapper``到/dep1/bin/

### 5 打印测试页

前往/dep1/sbin执行：
```
sudo ./cupsd -f
```
即可启动cups。

插上打印机，随后可以在``http://localhost:631``中添加打印机。由于foo2zjs没有安装到CUPS，手动到foo2zjs源代码PPD/选择HP-LaserJet_Pro_CP1025nw.ppd文件。添加打印机完成。

之后可以在``http://localhost:631``点击print test page。

也可以进入/dep1/bin，直接执行``./lp /indep1/share/cups/data/testprin​t``。但是需要先在网页上设置该打印机为默认打印机（Set As Server Default）。

打印的文件见附录2。

# 五 版本差异介绍（重要）

CUPS在1.6及以后的版本，部分功能被分离出去，单独作为一个名为cups-filters的项目出现。
> From CUPS 1.6.0 on, this package is required for using printer drivers with CUPS under Linux. With CUPS 1.5.x and earlier this package can be used optionally to switch over to PDF-based printing. ------ [cups-filters homepage][4]

cups-filters中含有foomatic-rip，gstoraster，texttopdf，texttops等等文件（功能）。
* 不过foomatic-rip不是cups功能，属于foomatic-filters。项目foomatic-filters和cups-filters都有foomatic-rip，所以才能在实验中使用foomatic-filters而不使用cups-filters。
* gstoraster本属于ghostscript项目，但是在新版本（9.07之后）中被移动到cups-filters。它提供pdf转换功能。
* texttopdf等功能在cups1.6及之后被移动到cups-filters。打印测试页就需要这些过滤器，所以在cups1.6及以后必须要安装cups-filters才能打印测试页。

实验中，cups-filters尝试编译失败。但很幸运，foomatic-filters源码成功编译。

由于cups-filters尝试编译失败，只能使用CUPS 1.5.4实验。

ghostscript在9.0.7之后不包含gstoraster功能，pdf转换功能等将不可用。




# 六 其他

## 1 Unsupported document-format "text/plain"

当缺少texttops时，打印测试页会提示：
```
Unsupported document-format "text/plain"
```
经过查找，在已安装cups-filters的环境中/usr/share/cups/mime/cupsfilters.convs里记录了text/plain格式的解释器。
```
 text/plain application/pdf 32 texttopdf
```
说明这确实是cups-filter的功能。

## 2 打印机状态问题

打印机由于CUPS出现问题导致pause状态时，可以点击resume printer，从而恢复打印机状态。

## 3 "Dirty files"

当打印失败，``/var/log/cups/error.log``里显示
```
cupsdSetBusyState: newbusy="Dirty files", busy="Active clients and dirty files"
```
Dirty files有时并不意味着要打印的文件有问题，可能是CUPS无法处理这个文件。

如果要打印的是ps文件，可以使用``/bin/cupstestdsc``工具检查ps文件是否符合当前cups的要求。

有次日志显示
```
/dep1/bin/foo2zjs-wrapper: line 940: gs: command not found
```
说明确实gs命令，也就是ghostscript包的功能。

## 4 编译cups-filters失败

configure时提示依赖IJS_LIB有问题， 这个ijs是ghostscript项目里的。

重新编译cups-filters-1.8.2，加上IJS_LIB位置，
```
IJS_CFLAGS=-I/home/deep/Downloads/ghostscript​-9.18/ijs IJS_LIBS=-lijs  ./configure --enable-static --disable-avahi --disable-ldap --disable-dbus --disable-imagefilters --without-jpeg --without-png --without-tiff
```
这时configure成功

make错误Makefile:5027: recipe for target 'cups-notifier.c' failed ，还提示和gdbus-codegen有关。

查了查，gdbus-codegen这个玩意好像和glib有关系，没能解决。

看到``dbus``这个词就不太乐观，因为android中没有dbus，移植会出现问题。而且我也``--disable-dbus``，但还是出现问题。


# 七 结语

针对HP P1108移植调研暂时告一段落。根据目前的情况，可以尝试移植工作了。方案一、二在桌面Linux（ArchLinux）上编译运行成功，下一步就是依照这个调研结果进行移植。对于CUPS其他的一些问题，待以后再看情况解决。

# 八 附录

## 附录1 

方案一 foo2zjs打印的图片：

![foo2zjs打印的图片][5]

## 附录2

方案二 CUPS打印的图片：

![CUPS打印的图片][6]


  [1]: http://blog.csdn.net/xiaohuangzhilin/article/details/50134095
  [2]: https://github.com/openthos/printer-analysis/raw/master/%E7%A7%BB%E6%A4%8D%2Fraw%2F201638003837.png
  [3]: https://refspecs.linuxfoundation.org/LSB_3.2.0/LSB-Printing/LSB-Printing/ppdext.html
  [4]: http://www.linuxfoundation.org/collaborate/workgroups/openprinting/cups-filters
  [5]: https://github.com/openthos/printer-analysis/raw/master/%E7%A7%BB%E6%A4%8D%2Fraw%2F20163800121131.gif
  [6]: https://github.com/openthos/printer-analysis/raw/master/%E7%A7%BB%E6%A4%8D%2Fraw%2F201638141402.gif