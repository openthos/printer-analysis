目录
* package [android.printservice](#android.printservice) 
* public abstract class [PrintService](#PrintService) 
* public abstract class [PrinterDiscoverySession](#PrinterDiscoverySession) 
* public final class [PrinterInfo](#PrinterInfo)
* public final class [PrinterId](#PrinterId)
* public final class [PrintJob](#PrintJob)
* public final class [PrintJobInfo](#PrintJobInfo)
* public final class [PrinterCapabilitiesInfo](#PrinterCapabilitiesInfo)
* public final class [PrintDocument](#PrintDocument)
* public final class [PrintDocumentInfo](#PrintDocumentInfo)


<!--more-->


***
<p id="android.printservice"></p>

原链接：https://developer.android.com/reference/android/printservice/package-summary.html

package

# android.printservice

提供打印服务实现的类。打印服务是一种插件（ plug-in components），它能够通过一些标准协议和打印机通讯。这些服务像一座桥，处于系统和打印机之间。因此，打印机和打印协议的具体实现从系统中分离的，能够独立开发和更新。

一个打印服务实现应该基于``PrintService``类，并且实现它的抽象方法（abstract methods）。另外，打印服务必须要按约定来管理``PrintJob``（打印任务）类。

系统负责启动和停止一个打印服务，这个操作取决于该打印服务管理的打印机是否有激活的打印任务（active print jobs）。打印服务还要及时执行打印机发现操作，来保证好的用户体验。在打印机发现过程中系统和该打印服务的交互封装在``PrinterDiscoverySession``类的实例中，由打印服务在系统需要的时候创建。

## 类 （Classes）

### PrintDocument

从一个打印服务的角度来说，这个类代表一个待打印的文件（document）。

### PrinterDiscoverySession

这个类封装了打印机发现过程中一个打印服务和系统的交互操作。

### PrintJob

对一个打印服务来说，这个类代表一个打印任务（print job）。

### PrintService

这个基础类用来实现打印服务。


***
<p id="PrintService"></p>

原链接：https://developer.android.com/reference/android/printservice/PrintService.html

public abstract class

# PrintService

extends Service

```
java.lang.Object
   ↳	android.content.Context
 	   ↳	android.content.ContextWrapper
 	 	   ↳	android.app.Service
 	 	 	   ↳	android.printservice.PrintService
```

## 类综述 Class Overview

这是打印服务（print services）实现的基础类。一个打印服务知道如何去发现打印机，并且通过一个或多个协议和一个或多个打印机交流。

## 打印机发现 Printer discovery

一个打印服务负责发现打印机，添加发现的打印机，移除添加的打印机和更新添加的打印机。当系统需要你的服务所管理的打印机时，系统会调用``onCreatePrinterDiscoverySession()``,在这个函数里你必须返回一个``PrinterDiscoverySession``对象实例（instance）。这个返回的session（会话）封装了系统和你的服务交互的内容，包含在打印机发现阶段的操作。这个交互的更多描述，参考``PrinterDiscoverySession``文档。

对每个打印发现会话（session）来说，所有的打印机必须要被添加，因为系统在会话过后不会保留。因此，打印服务检测到的每个打印机都应该被添加，并且在一个发现会话中仅添加一次。只有已经添加的打印机才能被移除（removed）或者更新（updated）。移除的打印机也能被再次添加。

## 打印工作 Print jobs

当一个新的打印工作（print jobs）指派到该打印服务所管理的打印机上，打印工作被放入队列（queued）。也就是，准备好由打印服务处理。你会收到一个``onPrintJobQueued(PrintJob)``调用。该打印服务可能会立即处理这个打印工作或者放入计划中过会调度。该打印服务的所有活动的打印工作在一个列表里，这个列表通过调用``getActivePrintJobs()``可以获得。活动的打印工作（Active print jobs）就是在队列中或已开始的那些。

当一个打印服务在处理一个打印工作时，打印服务负责设置该打印工作为适当的状态。首先，一个打印工作在队列中，也就是``PrintJob.isQueued()``返回true，这意味着要打印的文档已被系统安排(spooled)，该打印服务能够随时处理它。你可以通过调用``PrintJob.getDocument() ``获得要打印的文档，这里面的数据可以通过``PrintDocument.getData()``读取。在打印服务开始打印数据，该打印工作（print job）状态应该被设置为已开始（started），通过调用``start()``函数设置。设置之后，``PrintJob.isStarted()``应该会返回true。在工作成功完成后，该打印工作应该被标记为已完成（completed），通过调用``PrintJob.complete()``设置。设置之后，``PrintJob.isCompleted()``应该会返回true。失败的话，该打印工作应该被标记为失败（failed）,通过调用``PrintJob.fail( String)``设置。设置后，``PrintJob.isFailed()``应该返回true。

如果一个打印工作（print job）处于队列（queued）或已开始（started），这时用户请求取消它，该打印服务会收到一个``onRequestCancelPrintJob(PrintJob)``调用。服务里的这个请求希望尽最大努力取消该工作（job）。若该工作被成功取消，它的状态需要通过``PrintJob.cancel()``被标记取消。标记后，``PrintJob.isCacnelled()``应该返回true。

## 生命周期 Lifecycle

一个打印服务的生命周期只由系统管理并且按规定的生命周期活动。另外，开始或者停止一个打印服务只由一个特定的用户行为触发，就是在设备设置里启用（enabling）或者禁用（disabling）服务。在系统绑定（binds）了一个打印服务后，系统会调用``onConnected()``。这个方法可以被客户端（clients）重写来执行绑定（binding）有关的操作。而且在系统解绑（unbinds）一个打印服务后，系统会调用``onDisconnected()``。这个方法可以被客户端（clients）重写来执行解绑（unbinding）有关的清理操作。你的任何工作都不应该在系统和你的打印服务断开连接之后做，因为这个服务在回收内存时随时会被杀死。当该打印服务管理的打印机有活动的打印工作时，系统不会与之断开连接。

## 声明 Declaration

一个打印服务和其他任何服务一样，需要在AndroidManifest.xml里声明。但是它还必须处理action为``android.printservice.PrintService``的Intent。这个intent声明失败会导致系统忽略该打印服务。另外，一个打印服务必须请求``android.permission.BIND_PRINT_SERVICE``权限，来保证只有系统能绑定（bind）它。声明这个失败会导致系统忽略这个打印服务。下面是一个声明的例子：
```xml
 <service android:name=".MyPrintService"
         android:permission="android.permission.BIND_PRINT_SERVICE">
     <intent-filter>
         <action android:name="android.printservice.PrintService" />
     </intent-filter>
     . . .
 </service>
```

## 配置 Configuration

一个打印服务可通过自定义设置页面（setting activity）进行配置，该activity提供自定义设置功能。一个添加打印机的activity可以手动添加打印机，供应商名称等等。系统负责在适当的时候启动设置和添加打印机的activities。

一个打印服务在声明的时候，要在mainfest里提供一条``meta-data``，这是指定上述activities的方式。一个服务的``meta-data``标签（tag）声明如下所示：
```xml
 <service android:name=".MyPrintService"
         android:permission="android.permission.BIND_PRINT_SERVICE">
     <intent-filter>
         <action android:name="android.printservice.PrintService" />
     </intent-filter>
     <meta-data android:name="android.printservice" android:resource="@xml/printservice" />
 </service>
```
关于通过meta-data配置你的打印服务的更多细节，可以参考``SERVICE_META_DATA``和``<print-service>``。

**提示：**这个类里的所有回调函数（callbacks）都在程序的主线程里执行。你也应该在程序的主线程里调用（invoke）这个类里的方法。

***
<p id="PrinterDiscoverySession"></p>

原链接： https://developer.android.com/reference/android/printservice/PrinterDiscoverySession.html

public abstract class
# PrinterDiscoverySession


extends Object

```
java.lang.Object
   ↳	android.printservice.PrinterDiscoverySession
```

## 类综述 Class Overview

这个类封装了一个打印服务和系统在打印机寻找（printer discovery）过程中的交互内容。在打印机寻找过程中，你（指这个类）负责添加发现的打印机，移除之前添加但无效的打印机，更新已经添加的打印机。

在这个会话（session）的一生中，你可能会被多次请求开始和停止寻找打印机。你会被调用``onStartPrinterDiscovery(List)``来开始寻找打印机，然后被调用``onStopPrinterDiscovery()``来停止寻找打印机。当系统不再需要这个会话（session）来寻找打印机，你会收到``onDestroy()``调用。在这时，系统将不会再调用这个会话``session``，整个会话里的方法都不再有用。

被发现的打印机通过调用``addPrinters(List)``方法添加。添加好的打印机通过调用``removePrinters(List)``来移除。添加的打印机的属性和功能（properties or capabilities ）通过调用``addPrinters(List)``来更新。这些被添加的打印机能通过``getPrinters()``获取，在该方法里返回的打印机是你上报的打印机的一个最新快照（snapshot）。这些打印机在会话（session）后**不会保留**。

如果你（猜测指用户，之前指这个类）需要更新一些打印机，系统会调用``onValidatePrinters(List)``。你可能会只添加一个打印机而不配置它的功能特性（capabilities）。这个机制使得你避免为了配置打印机的功能而查询所有打印机，而是只在必要的时候查询一个打印机的功能即可。例如，一个打印机被用户选择使用，系统会请求你更新这个打印机。验证打印机时你不需要提供打印机的功能特性，但可以这样做。

如果系统要持续监测打印机的最新状态，你会收到一个``onStartPrinterStateTracking(PrinterId)``调用。收到之后，你必须尽可能地保持系统得到打印机最新的状态和功能特性。当你之前添加打印机的时候没有提供它的功能特性，你在这时**必须**更新这些内容。要不然打印机就会被忽略。当系统不再需要获取打印机的最新状态时，你会收到一个``onStopPrinterStateTracking(PrinterId)``调用。

**提示：**这个类里的所有回调函数都在程序的主线程执行。你自己用的时候也要在主线程调用这些函数。

***
<p id="PrinterInfo"></p>

public final class
# PrinterInfo

extends Object
implements Parcelable

```

java.lang.Object
   ↳	android.print.PrinterInfo
```

## 类综述 Class Overview

这个类是一个打印机的描述。这个类的实例由打印服务创建，把它们管理的打印机报告给系统。这个类里面的信息有两大部分。第一部分，打印机属性，列如name（名称）,id（编号）,status（状态），description（描述）。第二部分，printer capabilities（打印机能力）。printer capabilities描述了打印机支持的各种打印模式，例如media sizes（大概是纸张尺寸），margins（留白大小）等等。

***
<p id="PrinterId"></p>

public final class
# PrinterId

extends Object
implements Parcelable

```
java.lang.Object
   ↳	android.print.PrinterId
```

## 类综述 Class Overview

这个类代表一个打印机独一无二的编号

***
<p id="PrintJob"></p>

public final class
# PrintJob

extends Object

```

java.lang.Object
   ↳	android.printservice.PrintJob
```

## 类综述 Class Overview

从一个打印服务的角度来看，这个类代表一个打印任务（print job）。它提供了一些API来观察打印任务的状态和在打印任务上执行操作。

**提示：**类里面的所有方法都必须在应用主线程里执行。

***
<p id="PrintJobInfo"></p>

public final class
# PrintJobInfo

extends Object
implements Parcelable

```

java.lang.Object
   ↳	android.print.PrintJobInfo
```

## 类综述 Class Overview

这个类代表了一个打印任务的详细描述。打印机任务的状态包括一些配置，例如：它的id（编号）、打印属性，这用来生成一些内容等等。注意，打印任务状态可能会随着时间改变。这个类只代表了状态的一个时刻（snapshot）。

***
<p id="PrinterCapabilitiesInfo"></p>

public final class
# PrinterCapabilitiesInfo

extends Object
implements Parcelable

```
java.lang.Object
   ↳	android.print.PrinterCapabilitiesInfo
```
## 类综述 Class Overview

这个类代表了一个打印机的能力（capabilities）。这个类的实例由一个打印服务创建，用来报告它所管理的打印机的能力。一个打印机的能力指出了它如何打印内容。例如：打印机支持什么纸张大小（media sizes），打印机设计的最小留白（the minimal margins），等等。

***
<p id="PrintDocument"></p>

public final class
# PrintDocument

extends Object
```
java.lang.Object
   ↳	android.printservice.PrintDocument
```
## 类综述 Class Overview

对一个打印服务来说，这个类代表一个待打印的文件。它提供一些API来查询文件和其包含的数据。

**提示：**所有的方法必须在程序的主线程里执行。


***
<p id="PrintDocumentInfo"></p>

public final class

# PrintDocumentInfo

extends Object
implements Parcelable

```
java.lang.Object
   ↳	android.print.PrintDocumentInfo
```
## 类综述 Class Overview

这个类封装了关于一个文档跟打印有关的信息。这个``meta-data``（元数据）被平台（platform）和打印服务使用，构成和打印机的交互。例如，这个类包含文档的页数，文档页数展示给使用者是为了使他们能够选择打印的范围。一个打印服务可能会针对内容类型对打印进行优化，例如文档（document）或者照片。

这个类的实例由需要打印的应用创建，在成功排版内容（laying out the content）之后调用``PrintDocumentAdapter.LayoutResultCallback.onLayoutFinished( PrintDocumentInfo, boolean)``回调函数。这个过程执行在``PrintDocumentAdapter.onLayout(PrintAttributes, PrintAttributes, android.os.CancellationSignal, PrintDocumentAdapter.LayoutResultCallback, android.os.Bundle)``里。

一个使用例子如下：
```java
 . . .

 public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
         CancellationSignal cancellationSignal, LayoutResultCallback callback,
         Bundle metadata) {

        // Assume the app defined a LayoutResult class which contains
        // the layout result data and that the content is a document.
        LayoutResult result = doSomeLayoutWork();

        PrintDocumentInfo info = new PrintDocumentInfo
                .Builder("printed_file.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(result.getPageCount())
                .build();

       callback.onLayoutFinished(info, result.getContentChanged());
   }

   . . .
```

