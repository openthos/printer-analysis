# 一 简介

从Android4.4开始，系统加入了打印相关的API，可以通过系统打印服务实现打印。对于需要使用打印功能的APP可参考官方教程接入打印服务。
> Printing Content https://developer.android.com/training/printing/index.html

这不是本文的内容，本文介绍打印机厂商如何使自己的打印机接入android，即编写自己的打印插件接入android打印服务。且仅介绍接入部分，与打印机如何连接不在本文范围之内。

系统打印服务框架代码位于[``android.printservice``][1]包中。系统并没有实现具体打印功能，需要打印机厂商制作插件接入系统打印服务之后，自行实现。本文基于Android API Reference和以下两个github上的开源项目研究而来。两个参考项目如下：

* android-print-plugin-cups
主页： https://github.com/pelya/android-print-plugin-cups

* JfCupsPrintService
主页： https://github.com/mretallack/JfCupsPrintService

通过实验，初步实现了系统打印服务的接入（添加打印机）和模拟打印（将要打印的文件输出）。

# 二 主要类介绍

从[``android.printservice``][2]中，我们可以知道主要有四个类：

* [``PrintDocument``][3] 表示待打印文件，里面存放有文件的大小等信息和文件内容。

* [``PrinterDiscoverySession``][4]    用于发现打印机，整个发现打印机和打印机状态更新的过程在该类里进行。

* [``PrintJob``][5]    代表一个打印任务。

* [``PrintService``][6]    接入系统打印的关键Service。

PrinterDiscoverySession 由 PrintService 创建，通过 onCreatePrinterDiscoverySession() 函数返回给系统。
PrintJob 由需要打印的APP创建，发送给 PrintService 。
PrintDocument 存放在 PrintJob 里面，被一同发过来。

和打印相关的类的更多详细参考见：[Android_Print_API_部分翻译.md][7] 。

# 三 打印服务插件的工作流程

## 1 打印机发现过程

当用户在设置里开启你的打印服务插件和进入系统打印服务界面时，系统会调用 PrinterDiscoverySession 里的 onStartPrinterDiscovery(List<PrinterId> priorityList) 函数，通知你的插件查找打印机。具体查找方式需要自己实现，可能是查找USB接口，可能是搜索网络。系统只管结果，你通过调用其父类的 addPrinters() 方法将打印机添加进去。打印机是放在List<PrinterId>数组里传入。

当用户离开上述打印插件的界面时，系统会调用 onStopPrinterDiscovery() 函数，表示插件可以停止寻找打印机了。

另外，在自定义的 addPrintersActivity 中，系统不会自动触发打印机寻找过程，需要自行处理。

## 2 打印机选择过程

当用户通过一些有打印功能的APP调用系统打印服务时，如果选择了你的插件的打印机，那么系统会调用 PrinterDiscoverySession 里的 onStartPrinterStateTracking(PrinterId printerId) 方法。这里系统主要希望得到打印机的 [``PrinterCapabilitiesInfo``][8] 和状态，里面包括打印机支持的纸张大小，以及色彩等详细功能参数。

比如：如果没有addMediaSize(PrintAttributes.MediaSize.ISO_A4, false)，那么用户就不能选择A4大小进行打印。后面的false表示是否设为默认值。

打印机有STATUS_BUSY、STATUS_IDLE、STATUS_UNAVAILABLE三种状态，只有打印机处于STATUS_IDLE时，系统才允许使用该打印机。

打印机参数直接体现在系统打印服务界面，只可以选择支持的参数，比如选择纸张的大小为A4。

同样，当用户离开该界面或者选择其他打印机时，系统会调用 onStopPrinterStateTracking(PrinterId printerId) 函数，来告诉插件不用再提供打印机的信息了。

## 3 打印过程

当用户在刚刚的系统打印服务界面点击右上角的打印按钮时，系统会调用打印机所属的 PrintService 里的 onPrintJobQueued(PrintJob printJob) 方法，插件需要处理该 PrintJob 。首先需要通过 PrintJob.isQueued() 判断，该PrintJob是否准备好打印，返回true代表可以打印。然后可以通过 PrintJob.getDocument() 获得要打印的文档，这里面的数据可以通过 PrintDocument.getData() 读取。开始打印的时候，调用PrintJob.start()标记开始状态。当打印成功时，调用 PrintJob.complete() 标记打印成功。或者打印失败时，调用 PrintJob.fail( String) 标记失败。

**注意**：一定要对PrintJob进行状态标记，包括开始或者成功失败。如果什么都不标记，系统会一直在任务栏提示该任务打印中，并且该打印机不可打印其他任务，处于准备中。如果任务结束不标记成功或者失败，一段时间之后，系统会自动将该任务标记为失败，并且打印机状态自动变为不可用。


# 四 系统打印服务输出的数据

通过编写DEMO测试，发现android系统打印服务输出的数据是pdf 1.4的格式，无论文件内容是照片还是文档，都会统一转换为pdf 1.4。

# 五 打印服务插件初步编写


## 1 打印服务插件的声明

一个打印服务和其他任何服务一样，需要在AndroidManifest.xml里声明。但是它还必须处理action为android.printservice.PrintService的Intent。这个intent声明失败会导致系统忽略该打印服务。另外，一个打印服务必须请求android.permission.BIND_PRINT_SERVICE权限，来保证只有系统能绑定（bind）它。声明这个失败会导致系统忽略这个打印服务。

一个打印服务可通过自定义设置页面（setting activity）进行配置，该activity提供自定义设置功能。还有一个添加打印机的activity可以手动添加打印机，供应商名称等等。系统负责在适当的时候启动设置和添加打印机的activities。

一个打印服务在声明的时候，要在mainfest里提供一条 android:name="android.printservice" 的 meta-data，这是指定上述activities的方式。

AndroidManifest.xml文件如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.github.openthos.printer.testprintservice">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.START_PRINT_SERVICE_CONFIG_ACTIVITY" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <service
            android:name=".MyPrintService"
            android:permission="android.permission.BIND_PRINT_SERVICE">
            <intent-filter>
                <action android:name="android.printservice.PrintService" />
            </intent-filter>

            <meta-data
                android:name="android.printservice"
                android:resource="@xml/printservice" />
        </service>

        <activity
            android:name=".SettingsActivity"
            android:exported="true"
            android:label="@string/settings_activity_label" />
        <activity
            android:name=".AddPrintersActivity"
            android:exported="true"
            android:label="@string/add_pritners_activity_label">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity
            android:name=".AdvancedPrintOptionsActivity"
            android:label="@string/advanced_print_options_activity_label"
            android:exported="true"></activity>
    </application>

</manifest>
```

``android:resource="@xml/printservice"``对应的文件为printservice.xml。

这里面指定的settingsActivity在打印插件开启界面右上角的菜单里，用于配置插件。

addPrintersActivity除了在打印插件开启界面的菜单里，在打印文件时添加打印机里也会被触发，这个activity用来自定义添加打印机。

advancedPrintOptionsActivity则是在打印文件的界面上点击更多箭头里出现的MORE OPTIONS选项触发，这个activity用配置打印机的跟多信息。当然这是可选的操作，也可以没有这个activity。

printservice.xml文件内容如下所示：

```xml
<?xml version="1.0" encoding="utf-8"?>
<print-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:vendor="openthos"
    android:settingsActivity="com.github.openthos.printer.testprintservice.SettingsActivity"
    android:addPrintersActivity="com.github.openthos.printer.testprintservice.AddPrintersActivity"
     android:advancedPrintOptionsActivity="com.github.openthos.printer.testprintservice.AdvancedPrintOptionsActivity"
    >
</print-service>
```

## 2 PrintService实现类编写

在这里的 onPrintJobQueued 方法中，直接将需要打印的数据输出为文件。存放在APP根目录里的files文件夹。

```java
package com.github.openthos.printer.testprintservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.print.PrintJobInfo;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MyPrintService extends PrintService {

    private static final String TAG = "MyPrintService";

    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        Log.d(TAG, "onCreatePrinterDiscoverySession()");
        return new MyPrintDiscoverySession(this);
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {
        Log.d(TAG, "onRequestCancelPrintJob()");
        printJob.cancel();
    }

    @Override
    protected void onPrintJobQueued(PrintJob printJob) {
        Log.d(TAG, "onPrintJobQueued()");
        PrintJobInfo printjobinfo = printJob.getInfo();
        PrintDocument printdocument = printJob.getDocument();
        if (printJob.isQueued()) {
            return;
        }
        printJob.start();

        String filename = "docu.pdf";
        File outfile = new File(this.getFilesDir(), filename);
        outfile.delete();
        FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(printdocument.getData());
        //创建一个长度为1024的内存空间
        byte[] bbuf = new byte[1024];
        //用于保存实际读取的字节数
        int hasRead = 0;
        //使用循环来重复读取数据
        try {
            FileOutputStream outStream = new FileOutputStream(outfile);
            while ((hasRead = file.read(bbuf)) > 0) {
                //将字节数组转换为字符串输出
                //System.out.print(new String(bbuf, 0, hasRead));
                outStream.write(bbuf);
            }
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭文件输出流，放在finally块里更安全
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        printJob.complete();
    }
}

```

## 3 PrinterDiscoverySession实现类编写

```java
package com.github.openthos.printer.testprintservice;

import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboxh on 2016/3/14.
 */
public class MyPrintDiscoverySession extends PrinterDiscoverySession {
    private static final String TAG = "MyPrintDiscoverySession";
    private final MyPrintService myPrintService;

    public MyPrintDiscoverySession(MyPrintService myPrintService) {
        Log.d(TAG, "MyPrintDiscoverySession()");
        this.myPrintService = myPrintService;
    }

    @Override
    public void onStartPrinterDiscovery(List<PrinterId> priorityList) {
        Log.d(TAG, "onStartPrinterDiscovery()");
        List<PrinterInfo> printers = this.getPrinters();
        String name = "printer1";
        PrinterInfo myprinter = new PrinterInfo
                .Builder(myPrintService.generatePrinterId(name), name, PrinterInfo.STATUS_IDLE)
                .build();
        printers.add(myprinter);
        addPrinters(printers);
    }

    @Override
    public void onStopPrinterDiscovery() {
        Log.d(TAG, "onStopPrinterDiscovery()");
    }

    /**
     * 确定这些打印机存在
     * @param printerIds
     */
    @Override
    public void onValidatePrinters(List<PrinterId> printerIds) {
        Log.d(TAG, "onValidatePrinters()");
    }

    /**
     * 选择打印机时调用该方法更新打印机的状态，能力
     * @param printerId
     */
    @Override
    public void onStartPrinterStateTracking(PrinterId printerId) {
        Log.d(TAG, "onStartPrinterStateTracking()");
        PrinterInfo printer = findPrinterInfo(printerId);
        if (printer != null) {
            PrinterCapabilitiesInfo capabilities =
                    new PrinterCapabilitiesInfo.Builder(printerId)
                            .setMinMargins(new PrintAttributes.Margins(200, 200, 200, 200))
                            .addMediaSize(PrintAttributes.MediaSize.ISO_A4, true)
                            //.addMediaSize(PrintAttributes.MediaSize.ISO_A5, false)
                            .addResolution(new PrintAttributes.Resolution("R1", "200x200", 200, 200), false)
                            .addResolution(new PrintAttributes.Resolution("R2", "300x300", 300, 300), true)
                            .setColorModes(PrintAttributes.COLOR_MODE_COLOR
                                            | PrintAttributes.COLOR_MODE_MONOCHROME,
                                    PrintAttributes.COLOR_MODE_MONOCHROME)
                            .build();

            printer = new PrinterInfo.Builder(printer)
                    .setCapabilities(capabilities)
                    .setStatus(PrinterInfo.STATUS_IDLE)
            //        .setDescription("fake print 1!")
                    .build();
            List<PrinterInfo> printers = new ArrayList<PrinterInfo>();

            printers.add(printer);
            addPrinters(printers);
        }
    }

    @Override
    public void onStopPrinterStateTracking(PrinterId printerId) {
        Log.d(TAG, "onStopPrinterStateTracking()");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
    }

    private PrinterInfo findPrinterInfo(PrinterId printerId) {
        List<PrinterInfo> printers = getPrinters();
        final int printerCount = getPrinters().size();
        for (int i = 0; i < printerCount; i++) {
            PrinterInfo printer = printers.get(i);
            if (printer.getId().equals(printerId)) {
                return printer;
            }
        }
        return null;
    }

}
```

# 六 总结

学习了该部分知识之后，已经可以初步从系统打印服务接入打印机，并取得要打印的文件。之后根据使用情况，适时地跟进细节即可。


  [1]: https://developer.android.com/reference/android/printservice/package-summary.html
  [2]: https://developer.android.com/reference/android/printservice/package-summary.html
  [3]: https://developer.android.com/reference/android/printservice/PrintDocument.html
  [4]: https://developer.android.com/reference/android/printservice/PrinterDiscoverySession.html
  [5]: https://developer.android.com/reference/android/printservice/PrintJob.html
  [6]: https://developer.android.com/reference/android/printservice/PrintService.html
  [7]: https://github.com/openthos/printer-analysis/blob/master/APP%2FAndroid_Print_API_%E9%83%A8%E5%88%86%E7%BF%BB%E8%AF%91.md
  [8]: https://developer.android.com/reference/android/print/PrinterCapabilitiesInfo.html
