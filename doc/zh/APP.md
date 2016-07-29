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

打印程序需要显示打印任务的状态，可以通过前面CUPS交流接口中看到查询打印任务的命令。

但是对于如何知道打印任务状态被更新，我们没有找到一个直接的途径。因此，我们通过枚举打印任务状态变化的可能，针对各个情况分别处理，从而实现监听打印任务的状态。

编号|事件|可感知
|---|---|---
|1|新建任务|主动
|2|取消任务（取消所有）|主动
|3|暂停任务（暂停所有）|主动
|4|恢复任务（恢复所有）|主动
|5|打印完毕（包括出错和成功）|被动
|6|远程操作任务（暂时忽略）|被动

总共发现了6个与打印任务状态相关的时事件，其中 6 远程操作任务 是从网络接收的指令，里面同样包含了 1~5 事件，但是我们暂时未能找到简单地主动监测6事件的方法。虽然有复杂的可行方法，但由于综合考虑暂时先做忽略处理。

其中 1~4 是由程序主动发出的任务，我们能够主动感知，从而刷新任务状态。

重点需要解决 5 事件，可以细分处理。首先 5 事件是有打印任务存在才会出现的情况。没有打印任务则无关。

对于 5 事件 ，目前收录的情况有：

编号|详细情况|监听方案
|---|---|---
|5.1|由正常打印状态变化到打印完毕|若是正常打印状态，则轮询查询，直至打印结束
|5.2|先前处于就绪态，离线打印机中途插入，打印完毕|打印机离线，则监测USB，有插入设备，则延时检测是否有变化。

对于 5.2 情况会有两种结果。

编号|结果
|---|---
|5.2.1|若是该打印机，状态发生变化则进入 5.1 情况
|5.2.2|若不是该打印机，回到 5.2 状态

至此，我们已经处理了除了 6 事件外所有的事件，没有网络打印时，能够及时有效的监听打印任务状态的变化。

若考虑网络打印，该方法会丢失接收网络打印任务极其状态变化的监听。

除此之外，为了打印任务状态的更新更加及时，在每次进入打印任务管理界面都会主动刷新打印任务。

对于存在就绪态任务，会延时刷新一次。因为有时虽然打印机插上，还是会短暂的处于就绪态。

# 代码位置

目前代码存放有两个地点。

1. 使用Android studio开发的程序位于 [printer-analysis](https://github.com/openthos/printer-analysis.git) 项目的dev-app分支。需要使用的程序是其中的 localprint 模块，这是一个app。
2. 可直接集成到系统中的程序地址：[oto_packages_apps_Printer](https://github.com/openthos/oto_packages_apps_Printer)。（更新可能较慢）
