# 交流接口简介

CUPS交流接口描述了CUPS程序与APP上层交互的方式。

在打印应用中，APP上层接收到用户的指令。应用在JAVA层面调用命令行发出命令，将命令传递给CUPS程序，实现打印相关操作。

为了完成命令的传送，我们整理了所有与打印相关的操作，并且称之为CUPS交流接口。

## 分类

* A cups管理
* B 打印机管理
* C 打印任务管理

## 详细列表

| 编号 | 类名/简介 | 具体命令/备注 |
|---|---|---|
|A1| CommandTask|sh proot.sh /usr/bin/lpstat -r|
||检测打印机是否运行|判断输出的字符串中是否包含“scheduler is running”字段
|A2| CommandTask|sh proot.sh /usr/sbin/cupsd -f|
||启动CUPS||
|A3| CommandTask|
||关闭cups|未编写，实际通过`kill pid`即可正常关闭cups。注意不要kill -9 pid，这会强行关闭cups。`pid`为进程号。
|B1| SearchModelsTask|sh proot.sh lpinfo -m|
||查询可用打印机驱动|会将所有PPD信息输出
|B2| AddPrinterTask|sh proot.sh lpadmin -p name -v url -m model -o printer-is-shared=false -E|
||添加打印机|
|B3| ListAddedTask|sh proot.sh lpstat -v|
||查询已添加打印机|
|B4| DeletePrinterTask|sh proot.sh lpadmin -x printerName|
||删除打印机|
|B5| UpdatePrinterCupsOptionsTask|sh proot.sh lpoptions -p printerName [-o optionName=optionsVaule]...|
||修改打印机高级配置|
|B6| StateTask|sh proot.sh lpoptions -p printerName -l
||查询打印机状态和配置|
|B7| |
||设置默认打印机|暂时可以不用
|B8| SearchPrintersTask|sh proot.sh lpinfo -v -l|
||查询可添加打印机|
|B9| QueryPrinterCupsOptonsTask|sh proot.sh lpoptions -p printerName -l|
||查询打印机高级设置|查询CUPS所有可用的设置
|B10|QueryPrinterOptonsTask|sh proot.sh sh printerquery.sh printerName
||查询打印机设置|查询安卓打印直接相关的参数，[printerquery.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/printerquery.sh)里的命令如下：lpoptions -p $1 && lpoptions -p $1 -l
|B11|UpdatePrinterOptonsTask|sh proot.sh lpoptions -p printerName -o MediaSizeName=mediaSizeValue -o ColorModeName=colorModeValue
||修改打印机设置|
|B12|ResumePrinterTask|sh proot.sh cupsenable PrinterName|
||恢复打印机|打印机有时遇到问题会被自动暂停
|B13|RepairPdfTask|sh proot.sh gs -o repairedFileName -sDEVICE=pdfwrite fileName|
||修复损坏的PDF文件|WPS新建文档进行打印时，WPS转换出的PDF文件就存在这个问题
|C1|PrintTask|sh proot.sh lp -d printerName fileName -o media=mediaSize [-o Resolution=resolution] [-o landscape] -t label -n copies -P ranges [-o fit-o-page]|
||打印|中括号为可选参数
|C2|JobQueryTask|sh proot.sh sh /jobquery.sh |
||查询打印任务| [jobquery.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/jobquery.sh)里的命令如下：lpq -a && lpstat -l -o
|C3|JobCancelTask|sh proot.sh cancel jobId
||取消打印任务|
|C4|JobPauseTask|shproot.sh ipptool http://localhost:CUPS_PORT/jobs -d job-id=JobId hold-job.test
||暂停打印任务|[hold-job.test](https://github.com/openthos/printer-analysis/blob/dev/shell/hold-job.test)文件内为ipp命令内容
|C5|JobResumeTask|sh proot.sh ipptool http://localhostCUPS_PORT/jobs -d job-id=JobId release-job.test
||恢复打印任务|[release-job.test](https://github.com/openthos/printer-analysis/blob/dev/shell/release-job.test)文件内为ipp命令内容
|C6|JobCancelAllTask|sh proot.sh cancel -a
||取消所有打印任务
|C7|JobPauseAllTask|sh proot.sh sh hold_release.sh jobId1 jobId2 ... hold
||暂停所有打印任务|参考 C4 调用ipptool发送命令，详见数据包里的[hold_release.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/hold_release.sh)
|C8|JobResumeAllTask|sh proot.sh sh hold_release.sh jobId1 jobId2 ... release
||恢复所有打印任务|参考 C5 调用ipptool发送命令，详见数据包里的[hold_release.sh](https://github.com/openthos/printer-analysis/blob/dev/shell/hold_release.sh)

## 注意

由于使用`Runtime.getRuntime().exec(cmd, null, file);`函数执行命令，以及proot工具包装程序，一次只能执行一条命令，所以需要多条命令时，将命令放入一个脚本文件里执行。

# 颜色对应表

在CUPS中打印机支持的打印色彩并不统一，根据打印机驱动的不同表示名称也不同。在Android中打印色彩只有两种`COLOR_MODE_MONOCHROME`和`COLOR_MODE_COLOR`，分别代表黑白和彩色。

|驱动名|PPD名|颜色键名|MONOCHROME对应项|COLOR对应项
|---|---|---|---|---
|Epson官方驱动||Color|Grayscale|Color
|foo2zjs||ColorMode|Monochrome|ICM
|hpcups|HP Color LaserJet 3000 pcl3, hpcups 3.14.3|ColorModel|Gray|RGB
|奔图||无|无|无
||HP Color LaserJet 3000 Postscript|ColorModel|Gray|CMYK

# 纸张尺寸对应表

本表尽可能的收集能够相互对应的尺寸列表。

|CUPS中的尺寸名|PPD文件中的值|Android中的尺寸名|Android中的值
|---|---|---|---
|A2|420x594mm|ISO_A2|420mm x 594mm (16.54" x 23.39")
|A3|297x420mm|ISO_A3|297mm x 420mm (11.69" x 16.54")
|A4|210x297mm|ISO_A4|210mm x 297mm (8.27" x 11.69")
|A5|148x210mm|ISO_A5|148mm x 210mm (5.83" x 8.27")
|A6|105x148mm|ISO_A6|105mm x 148mm (4.13" x 5.83")
|B5|176x250mm|ISO_B5|176mm x 250mm (6.93" x 9.84")
|B6|125x176mm|ISO_B6|125mm x 176mm (4.92" x 6.93")
|B7|88x125mm|ISO_B7|88mm x 125mm (3.46" x 4.92")
|C4|229x324mm|ISO_C4|229mm x 324mm (9.02" x 12.76")
|C5|162x229mm|ISO_C5|162mm x 229mm (6.38" x 9.02")
|Letter|8.5x11in|NA_LETTER|8.5" x 11" (279mm x 216mm)
|Executive|7.25x10.5in|NA_MONARCH|7.25" x 10.5" (184mm x 267mm)
|8k|10.75x15.5in|ROC_8K|270mm x 390mm (10.629" x 15.3543")
|16k|7.75x10.75in|ROC_16K|195mm x 270mm (7.677" x 10.629")
|Legal|8.5x14in|NA_LEGAL|8.5" x 14" (216mm x 356mm)
|Ledger|11x17in|NA_LEDGER|17" x 11" (432mm × 279mm)
|B|11x17in|NA_TABLOID|11" x 17" (279mm × 432mm)
|Card3x5|3x5in|NA_INDEX_3X5|3" x 5" (76mm x 127mm)
|Photo4x6|4x6in|NA_INDEX_4X6|4" x 6" (102mm x 152mm)
|Card5x8|5x8in|NA_INDEX_5X8|5" x 8" (127mm x 203mm)
|Hagaki|100x148mm|JPN_HAGAKI|100mm x 148mm (3.937" x 5.827")
|Oufuku|148x200mm|JPN_OUFUKU|148mm x 200mm (5.827" x 7.874")
|JB5|182x257mm|JIS_B5|182mm x 257mm (7.165" x 10.118")
|JB7|91x128mm|JIS_B7|91mm x 128mm (3.583" x 5.049")
|ExecutiveJIS|8.5x12.986in|JIS_EXEC|216mm x 330mm (8.504" x 12.992")
|EnvA2|4.37x5.75in|JPN_CHOU2|111.1mm x 146mm (4.374" x 5.748")
|EnvChou3|120x235mm|JPN_CHOU3|120mm x 235mm (4.724" x 9.252")
|EnvChou4|90x205mm|JPN_CHOU4|90mm x 205mm (3.543" x 8.071")
|EnvDL|110x220mm|PRC_5|110mm x 220mm (4.330" x 8.661")
|Mutsugiri|8x10in|NA_QUARTO|8" x 10" (203mm x 254mm)
|EnvKaku2|240x332mm|JPN_KAKU2|240mm x 332mm (9.449" x 13.071")
|Yougata4|105 x 235mm|JPN_YOU4|105mm x 235mm (4.134" x 9.252")

## 未找到对应尺寸
|CUPS中的尺寸名|PPD文件中的值|
|---|---|
|Photo5x7|5x7in|
|FLSA|8.5x13in|
|EnvC6|114x162mm|
|EnvMonarch|3.875x7.5in|
|Env9|Envelope #9|
|Env10|4.12x9.5in|
|EnvC5|162x229mm|
|EnvB5|176x250mm|
|SuperB|13x19in|
|B4|257x364mm|
|PhotoL|3.5x5in|
|HV|101x180mm|
|Cabinet|120x165mm|
|8x10|8x10in|
|Env6|3.63x6.5in|
|EnvCard|4.4x6in|
|L|89x127mm|
|2L|127x178mm|
|CDDVD80|80mm|
|CDDVD120|120mm|
|FanFoldGermanLegal|8.5 x 13|
|Oficio|216 x 340 mm|
|184x260mm|16K 184 x 260 mm|
|7.75x10.75|7.75x10.75|
|260x368mm|8K 260x368 mm|
|10.75x15.5|8K 273x394 mm|
|DoublePostcardRotated|Postcard Double Long Edge|
|FanFoldGerman|8.5x12|

# 打印状态记录表

这是任务 C2 解析的字段示例，根据这些字段就能够判断该任务当前的状态。

```
等待打印机
HP_LaserJet_Professional_P1108-45 unknown           1024   Tue Jul  5 13:38:01 2016
Status: Waiting for printer to become available.
Alerts: printer-stopped
queued for HP_LaserJet_Professional_P1108

等待打印机
HP_LaserJet_Professional_P1108-45 unknown           1024   Tue Jul  5 13:38:01 2016
Status: Waiting for printer to become available.
Alerts: none
queued for HP_LaserJet_Professional_P1108

等待打印机
HP_LaserJet_Professional_P1108-2​0 anonymous 1024 Sun May 22 16:48:45 2016
Status: Waiting for printer to become available.
Alerts: job-printing
queued for HP_LaserJet_Professional_P1108

失败
HP_LaserJet_Professional_P1108-2​0 deep 410624 Mon May 16 11:01:12 2016
Status: /usr/lib/cups/filter/pdftops failed
Alerts: job-printing
queued for HP_LaserJet_Professional_P1108

失败
HP_LaserJet_Professional_P1108-41 unknown           1024   Tue Jul  5 11:51:51 2016
Status: Filter failed
Alerts: job-hold-until-specified
queued for HP_LaserJet_Professional_P1108

就绪
HP_LaserJet_Professional_P1108-2​1 deep 410624 Mon May 16 11:01:18 2016
Alerts: none
queued for HP_LaserJet_Professional_P1108

暂停
HP_LaserJet_Professional_P1108-2​0 anonymous 1024 Sun May 22 16:48:45 2016
Status:
Alerts: job-hold-until-specified
queued for HP_LaserJet_Professional_P1108

渲染中
HP_LaserJet_Professional_P1108-2​1 anonymous 1024 Sun May 22 23:34:05 2016
Status: Printing page 1, 0% complete...
Alerts: job-printing
queued for HP_LaserJet_Professional_P1108

渲染中
HP_LaserJet_Professional_P1108-2​1 anonymous 1024 Sun May 22 23:34:05 2016
Status: Processing page 2...
Alerts: job-printing
queued for HP_LaserJet_Professional_P1108
```
