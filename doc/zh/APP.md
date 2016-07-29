# 打印原理

打印实现路径图：

![打印实现路径图](https://github.com/openthos/printer-analysis/blob/master/report/raw/3987526971.png)

在Android系统4.4及以上版本中，系统已加入一个打印框架，名为系统打印服务。打印机厂商可以制作打印服务插件，接入系统打印服务，从而实现在Android上使用自己的打印机。

整个打印程序包括两部分，转接CUPS插件和CUPS打印系统。其中CUPS打印系统部分请参考制作CUPS数据包页面：[MAKING_A_CUPS_COMPONENT.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/MAKING_A_CUPS_COMPONENT.md)。本文讲解制作转接CUPS插件。


# 打印APP

转接CUPS插件就是就是打印APP，这个打印应用程序承担了连接CUPS和系统打印服务的功能。

因此，主要分为系统打印服务插件部分和CUPS交流接口，另外会对打印任务状态更新机制进行介绍。

## 系统打印服务插件部分

制作系统打印服务插件请参考：[ANDROID_PRINTING_SERVICE_PLUGIN.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/ANDROID_PRINTING_SERVICE_PLUGIN.md)

## CUPS交流接口

CUPS交流接口即如何使程序连接CUPS。

连接CUPS其实有多种方式。

1. 使用ipp接口（网络）
2. 使用CUPS API for Java
3. 使用命令行调用命令

考虑到ipp接口可能较为复杂，以及Java版的CUPS接口不完善，所以采用命令行调用方式实现与CUPS的连接。

CUPS交流接口（调用命令）参考：[CHATTING_WITH_CUPS.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/CHATTING_WITH_CUPS.md)

## 打印任务触发事件

# 代码位置
