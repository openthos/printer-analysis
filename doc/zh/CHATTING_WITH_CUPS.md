# 简介

CUPS交流接口描述了CUPS程序与APP上层交互的方式。

在打印应用中，APP上层接收到用户的指令。应用在JAVA层面调用命令行发出命令，将命令传递给CUPS程序，实现打印相关操作。

为了完成命令的传送，我们整理了所有与打印相关的操作，并且称之为CUPS交流接口。

# 分类

* A cups管理
* B 打印机管理
* C 打印任务管理

# 详细列表

| 编号 | 类名/简介 | 具体命令/备注 |
|---|---|---|
|A1| CommandTask|sh proot.sh /usr/bin/lpstat -r|
||检测打印机是否运行|判断输出的字符串中是否包含“scheduler is running”字段
|A2| CommandTask|sh proot.sh /usr/sbin/cupsd -f|
||启动CUPS||
|A3| CommandTask|
||关闭cups|
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

# 其他

由于使用`Runtime.getRuntime().exec(cmd, null, file);`函数执行命令，一次只能执行一条命令，所以需要多条命令时，将命令放入一个脚本文件里执行。
