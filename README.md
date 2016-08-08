# dev-app-cs branch

该分支存放 安卓打印插件 服务与界面分离版本

## 特性

localprint模块为打印服务部分，localprintui为控制界面。

两个APP通过AIDL进行通信，已基本调试通过。

## 模块介绍

```
localprint				模块	打印APP服务

localprintui				模块	打印APP控制界面

openthosprintservice    模块	针对HP P1108打印机的DEMO

app						模块	系统打印服务接入DEMO

testexec				模块	命令行调用DEMO

testunit				模块	临时测试方法的DEMO

testusb					模块	安卓框架下USB读写的DEMO
```
