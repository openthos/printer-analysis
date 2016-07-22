# 这是什么

CUPS数据包作为打印程序的组件（component），需要额外放在系统中，在打印程序第一次运行时会自动解压导入。它是打印程序的核心，打印功能靠它实现。

数据包实际上是自己定制的一个CUPS运行环境，里面封装了CUPS及一些打印机驱动。这些程序能够在openthos系统中独立运行。

# 原理

## 依赖于Linux内核

Openthos系统基于Android x86改造而来,而Android系统是一种使用Linux内核的系统，这点和其他Linux类系统一样。在Linux类系统中CUPS(Common UNIX Printing System，通用Unix打印系统)是应用最广的打印解决方案。CUPS是开源的，可以编译安装在任何Linux类系统。Android使用Linux内核，所以有可能性把CUPS移植到Openthos系统中。

CUPS属于Linux用户态程序，从内核的角度来看，它的成功运行，只需要保证系统调用正常即可。即，无论CUPS使用了哪些依赖，比如：glibc，libusb等，最终都依靠系统调用实现功能。所以需要保证两方面：

1. 保证系统调用正常，需要标准Linux内核和足够权限，Android满足。
2. 用户态程序运行正常，需要CPU能正确识别的二进制程序。这点对于Openthos来说，编译x86程序即可保证兼容。

所以，移植CUPS理论上是可行的，事实也如此。

## 使用proot

我们希望让CUPS程序运行在指定的目录里，不对系统产生其他影响。如果让CUPS直接运行在系统中，CUPS必然会用到一些root权限才能读写的目录，很不方便。所以很显然的想到了`chroot`这种改变根目录的技术。但是chroot是需要root权限的，于是我们找到了一个限制更少的工具：PRoot。

PRoot是一种chroot的用户态开源实现工具。（PRoot项目地址: https://github.com/proot-me/PRoot ）

用户不需要拥有系统特权就可以在任意目录建立一个新的根文件系统。从而在建立的根文件系统内做任何事情。从技术上来说，PRoot是依靠ptrace机制实现的。ptrace允许程序在没有拿到系统特权（root）时，父进程观察并修改子进程的系统调用。

所以，我们使用PRoot就能够让CUPS运行在指定的目录里。

# 制作

# 基础环境（bash、命令）

# 安装CUPS

# 安装ghostscript

# 安装
