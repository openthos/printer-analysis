# Printer功能需求与设计实现文档
内容：

- 项目简介
- 功能需求
- 存在问题
- 项目进展
- 设计实现

# 项目简介

本项目属于openthos项目的一部分，提供 [Openthos](https://github.com/openthos/openthos/wiki) 系统本地打印机以及网络打印机支持。

# 功能需求

|完成|描述|
|---|---|
|√| 支持usb接口打印机（详见支持的打印机型号）
|√| 支持网络打印机(http、ipp、smb、lpd、beh等协议)
|√| 接入安卓系统打印服务，为系统提供打印功能
|√| 打印机管理（添加、删除、配置）
|√| 打印任务管理（暂停、恢复、取消）
|√| 在系统通知栏实现打印状态区

功能支持以及问题修复详细情况见：[REQUIREMENTS.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/REQUIREMENTS.md)

## 支持的打印机型号

打印机支持详细情况请查看：[SUPPORTED_PRINTERS.md](https://github.com/openthos/printer-analysis/blob/master/doc/SUPPORTED_PRINTERS.md)

# 存在问题

| 简述 | 类别 | 备注
|---|---|---|
|无法双面打印|打印||
|无缺纸提示|打印|CUPS本身不支持，某些驱动可能支持|
|网络打印任务状态不追踪|打印任务

# 项目进展

|开始时间|结束时间|内容
|---|---|---|
|2016-01-25|2016-02-24|调研桌面Linux打印情况，初步制定基于CUPS的驱动移植方案。
|2016-03-03|2016-03-13|针对HP P1108打印机，确定一个最简移植方案（foo2zjs + ghostscript），进行尝试。
|2016-03-14|2016-03-28|同时进行静态编译移植、基于bionic移植两种尝试，学习安卓打印服务插件制作。
|2016-03-29|2016-04-17|整合移植的打印驱动和安卓打印服务插件，制作针对HP P1108的打印DEMO，验证方案可行。
|2016-04-18|2016-05-30|移植、整合包含CUPS，ghostscript，foo2zjs等Linux程序的数据包，实现在Android下运行并打印。制作安卓打印程序对接CUPS程序。
|2016-06-01|2016-06-30|完成打印程序基本功能及数据包（期末考试时期工作较少）
|2016-07-01|2016-07-30|测试并完善网络打印功能，完善数据包（包含cups,cups-filter,ghostscript,foo2zjs,epson驱动，奔图驱动，hpcups && hplip plugin，samba等），整合程序进Openthos系统，编写开发文档。

# 设计实现

请按以下步骤依次进行。

## 构建 && 安装

请查看：[BUILDING.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/BUILDING.md)

## 制作CUPS数据包

请查看：[MAKING_A_CUPS_COMPONENT.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/MAKING_A_CUPS_COMPONENT.md)

## 开发打印程序

请查看：[APP.md](https://github.com/openthos/printer-analysis/blob/master/doc/zh/APP.md)
