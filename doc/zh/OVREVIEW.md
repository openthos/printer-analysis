# 简介

本项目属于openthos项目的一部分，提供openthos系统本地打印机以及网络打印机支持。

## 功能

* 支持usb接口打印机
* 支持网络打印机
* 接入安卓系统打印服务，为系统提供打印功能
* 打印任务管理（暂停，恢复）
* 打印机设置（在驱动支持的范围内）

# 支持的打印机型号

打印机支持详细情况请查看：[SUPPORTED_PRINTERS.md](https://github.com/openthos/printer-analysis/blob/master/doc/SUPPORTED_PRINTERS.md)

# 已知问题

| 简述 | 类别 | 备注
|---|---|---|---|
|无法双面打印|打印||
|无缺纸提示|打印|CUPS本身不支持，某些驱动可能支持|

# 开发

请按以下步骤依次进行。

## 构建 && 安装

请查看：[BUILDING.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/BUILDING.md)

## 制作CUPS数据包

请查看：[MAKING_A_CUPS_COMPONENT.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/MAKING_A_CUPS_COMPONENT.md)

## 开发打印程序

请查看：[APP.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/APP.md)
