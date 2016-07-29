#概述

##Openthos与Windows进行网络打印情况    
请见下面章节。

##Openthos与Linux（Cups）进行网络打印情况
服务器端选择打印机驱动，客户端选择```Generic```中的```Generic IPP Everywhere Printer```驱动，如不符合具体情况，请看下面章节。

##Openthos与自带网络功能打印机进行网络打印情况
请见下面章节。

___


# 1 openthos作为打印服务器（接收打印任务）
测试机型：HP P1108、奔图P2500W、嘉华龙马
操作方法：
>打开“本地打印服务”后，点击“添加本地打印”，输入打印机名称、正确的品牌、驱动后，将“分享打印机”一项勾上，添加成功后即完成此打印机的网络打印服务设置。
其后根据打印客户机操作系统的不同，添加此打印机进行打印的方式不同，具体见以下各情况。


## 1.1 **Windows为客户机**（**成功打印**）
操作方法：
>依次点击“开始”菜单、“设备与打印机”、“添加打印机”、“添加网络打印机”，点击“我需要的打印机不在列表中”，在“按名称选择共享打印机”一栏中填入Openthos共享的打印机的具体http地址，添加完成后即可打印。    
http地址格式：**http://ip地址:6310/printers/打印机名称**


## 1.2 **Linux为客户机**（**成功打印**）
（这里说的Linux测试了Ubuntu以及ArchLinux，使用Ubuntu自带的Cups作为客户机进行打印，ArchLinux则是安装了最新版Cups进行打印）
操作方法：
>打开网页浏览器输入```localhost:631```，依次点击“Administrator”、“Add Printer”，勾上“Internet Printing Protocol (ipp)”，输入具体ipp地址，根据需要填写打印机名称（必填）、描述、位置，在“Make”一栏中选择正确的品牌，之后选择正确的驱动，点击“Add Printer”，添加成功后即可打印。    
ipp地址格式：**ipp://ip地址:6310/printers/打印机名称**

驱动的选择：
### 1.2.1 Cups版本为2.0之前（测试系统Ubuntu Cups版本1.7.4）
+ 选择```Generic```栏中```Generic PostScript Printer```驱动    
测试结果：打印出的测试页丢失边框，打印文档正常    
测试机型：Pantum P2500W

+ 选择与打印机品牌型号相应的驱动    
测试结果：打印正常    
测试机型：Pantum P2500W、HP P1108

### 1.2.2 Cups版本为2.0之后（测试系统ArchLinux Cups版本2.1.4）
+ 选择```Generic```栏中```Generic IPP Everywhere Printer```驱动    
测试结果：打印出的测试页丢失边框，打印文档正常    
测试机型：Pantum P2500W


# 2  openthos作为打印客户机（发送打印任务）
此种情况下无法像本地USB连接打印机那样检测到设备，因此需要输入相应的url地址才能够连接打印机打印。

操作方法：
>打开“本地打印服务”后，点击“添加网络打印机”，输入打印机名称、正确的url地址、正确的品牌及驱动，之后添加成功后即可进行打印。
具体的url地址以及驱动根据打印机服务器操作系统的不同而不同，具体见以下各情况。

## 2.1 **Windows作为服务器**（**成功打印**）
测试机型：HP P1108    
需要在Windows下开启相应打印机的共享功能，下面所说打印机名称是在设置共享时所设置的名称，具体在Windows下共享打印机的步骤，请见[Windows7系统共享打印机设置方法](https://www.baidu.com/s?wd=windows%E5%A6%82%E4%BD%95%E5%85%B1%E4%BA%AB%E6%89%93%E5%8D%B0%E6%9C%BA&rsv_spt=1&rsv_iqid=0xf3deb8930002ed7c&issp=1&f=8&rsv_bp=0&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug3=34&rsv_sug1=2&rsv_sug7=101&rsv_sug2=0&inputT=5290&rsv_sug4=5293)    

客户机URL地址：
>使用samba服务器连接，具体格式：**smb://Windows用户名:密码@ip地址/共享的打印机名称**

客户机驱动：
选择对应打印机品牌与型号的驱动

## 2.2 **Linux / openthos 作为服务器**（**打印成功**）
需要在Linux/Openthos下建立开启共享功能的打印机    
客户机URL地址：
>使用ipp连接，具体格式：**ipp://服务器ip地址:631/printers/打印机名称**

客户机驱动：
驱动选择根据情况不同选择不同，具体见下

### 2.2.1 Linux作为服务器

建立共享打印机方法：
>打开网页浏览器输入```localhost:631```，依次点击“Administrator”、“Add Printer”，选择具体的USB打印机url，根据需要填写打印机名称（必填）、描述、位置并勾上“Share This Printer”一栏，在“Make”一栏中选择正确的品牌，之后选择正确的驱动，点击“Add Printer”，添加成功。

客户机驱动：
#### 2.2.1.1 服务器Cups版本为2.0之前（测试系统Ubuntu Cups版本1.7.4）
选择```Generic```栏中```Generic PostScript Printer```驱动   

测试结果：
+ Pantum P2500W    
打印测试页部分边框消失，原因是整个页面向右上角偏移

+ HP P1108    
打印正常

#### 2.2.1.2 服务器Cups版本为2.0之后（测试系统ArchLinux Cups版本2.1.4）
选择```Generic```栏中```Generic IPP Everywhere Printer```驱动  

测试结果：
正常打印

测试机型：
Pantum P2500W

###2.2.2 Openthos作为服务器

建立共享打印机方法：
具体请见本文 **1 openthos作为打印服务器（接收打印任务）** 中的具体操作方法

客户机驱动：
选择```Generic```栏中```Generic IPP Everywhere Printer```驱动 

测试结果：
正常打印

测试机型：
HP P1108、Pantum P2500W

## 2.3 **自带网络功能的打印机**（**成功打印**）
测试机型：嘉华龙马、奔图P2500W、HP P2015dn（失败）
目前测试嘉华龙马打印机网络功能能够成功打印、奔图P2500W也成功打印，但是对于HP P2015dn却无法连接，应该不是打印服务的问题，因为Windows上也无法连接它的网络打印功能，判断可能操作有问题或者打印机本身问题。
>使用socket协议，socket://ip地址
其中ip地址是指自带网络功能的打印机自身提供的ip地址，根据机型和品牌的不同查看方式也不一样。

成功打印的 嘉华龙马打印机 以及 奔图 P2500W 的连接方式记录如下：
### 2.3.1 **嘉华龙马打印机**
将其用网线与交换机or路由器相连接，打开开关，自动开启网络打印功能，打印机的小屏幕上会显示出打印机当前网络打印的ip地址，将此地址按照socket协议在Openthos中添加并选择正确品牌以及驱动（品牌：SecuSoft；驱动：ras2jbig 1.0.3）即可。

### 2.3.2 **奔图 P2500W**
此款打印机自身没有网线接口，但是自带WIFI打印功能，开启打印机后，按下WIFI按钮，看到WIFI标志的指示灯闪烁后，将PC连接至打印机发出的无线网上（名称为Pantum-AP-3DCF35），之后查看此网络的网关即可获得具体的ip地址，将此地址按照socket协议在Openthos中添加并选择正确品牌以及驱动即可。
