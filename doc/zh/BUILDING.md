# 1 程序组成部分

打印机程序可以分为三个部分说明。操作细节根据编译方式不同，会在编译部分具体介绍。

## 1.1 添加自定义权限

打印程序使用USB打印机，需要直接接触USB文件：`/dev/bus/usb/*/*`，在Android系统中这些usb文件应用程序只能通过Java层申请权限才能在JAVA层间接使用。
可喜的是这些文件属于 USB用户组 ，所以我们只需要把程序加入 USB用户组 即可。

## 1.2 程序

打印程序负责整个打印工作。这个程序需要使用platform签名编译，否则无法使用上一步添加的自定义权限ACCESS_USB_DEVICE。

使用Android studio开发的程序位于 https://github.com/openthos/printer-analysis.git 项目的dev-app分支。需要使用的程序是其中的 localprint 模块，这是一个app。

可直接集成到系统中的程序地址：https://github.com/openthos/oto_packages_apps_Printer

这个程序需要使用platform签名编译，否则无法使用上一步添加的自定义权限ACCESS_USB_DEVICE。

## 1.3 程序额外数据包

程序第一运行时需要导入一个数据包，数据包需要提前存放在系统中`/system/component_printer.tar.gz`。

# 2 编译

## 2.1 在Android源码中编译

### 2.1.1 构建Android源码开发环境

参考openthos构建教程： https://github.com/openthos/openthos/wiki/Download_Build_Run_OTO

### 2.1.2 添加自定义权限

需要在 frameworks/base/data/etc/platform.xml中添加:
``` xml
<permission name="android.permission.ACCESS_USB_DEVICE" >
        <group gid="usb" />
    </permission>
```
比如添加在android.permission.ACCESS_FM_RADIO下面:
``` xml
<!-- Hotword training apps sometimes need a GID to talk with low-level
     hardware; give them audio for now until full HAL support is added. -->
<permission name="android.permission.MANAGE_VOICE_KEYPHRASES">
    <group gid="audio" />
</permission>
 
<permission name="android.permission.ACCESS_FM_RADIO" >
    <group gid="media" />
</permission>
 
<permission name="android.permission.ACCESS_USB_DEVICE" >
    <group gid="usb" />
</permission>
```
这部分只需要修改一次。

### 2.1.3 加入打印程序

这里把程序作为系统程序作为一个模块加入，放入源码目录 packages/apps 里，比如新建 packages/apps/Printer 文件夹。
在源码中编译，需要编写Android.mk、CleanSpec.mk等文件。

或者使用更改好的Printer代码：https://github.com/openthos/oto_packages_apps_Printer

Android.mk文件示例：
``` makefile
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

appcompat_dir := $(LOCAL_PATH)/../../../prebuilts/sdk/current/support/v7/appcompat/res

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v13

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res $(appcompat_dir)

LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat

LOCAL_PACKAGE_NAME := Printer
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

include $(BUILD_PACKAGE)

ifeq (,$(ONE_SHOT_MAKEFILE))
include $(call all-makefiles-under,$(LOCAL_PATH))
endif

```

Printer内文件目录示意：
```
Android.mk  AndroidManifest.xml  CleanSpec.mk  res  src
```

此外把该模块加入编译，可以修改 build/target/product/core.mk 文件，把模块名称加入其中即可。

## 2.2 使用Android Studio

### 2.2.1 导入程序

程序位于 https://github.com/openthos/printer-analysis.git 项目的dev-app分支。
需要使用的程序是其中的 localprint 模块，这是一个app。
程序详细地址： https://github.com/openthos/printer-analysis/tree/dev-app/localprint
Android Studio可以直接导入该项目。

### 2.2.2 编译

在Android中使用platform签名编译，可以借助[keytool-importkeypair](https://github.com/getfatday/keytool-importkeypair) 工具将系统签名转换成Android Studio可以使用的签名文件。

1 首先下载好上述脚本，并拷贝系统源码 build/target/product/security 里的 platform.pk8 platform.x509.pem 文件到脚本目录，执行转换命令。
命令示例：
```
./keytool-importkeypair -k ./demo_platform.keystore -pk8 platform.pk8 -cert platform.x509.pem -passphrase 123456 -alias demo_platform
```

2 拷贝生成的 demo_platform.keystore 文件到项目根目录。

3 在模块 builde.gradle 文件里修改签名策略，示例：
```
release {
    storeFile file("../demo_platform.keystore")
    storePassword '123456'
    keyAlias 'demo_platform'
    keyPassword '123456'
}

debug {
    storeFile file("../demo_platform.keystore")
    storePassword '123456'
    keyAlias 'demo_platform'
    keyPassword '123456'
}
```

这样程序就能够使用之前添加的自定义权限ACCESS_USB_DEVICE。

### 2.2.3 添加自定义权限

添加自定义权限，必须使用2.1.2中的方法自行编译源码。Android Studio只是为了开发更方便。

# 3 运行

## 3.1 在Android源码中

跟随系统运行，记得提前导入数据包。

## 3.2 Android Studio

正常运行，记得提前导入数据包。



