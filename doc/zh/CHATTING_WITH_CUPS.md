# 简介

CUPS交流接口描述了CUPS程序与APP上层交互的方式。
在打印应用中，APP上层接收到用户的指令。应用在JAVA层面调用命令行发出命令，将命令传递给CUPS程序，实现打印相关操作。
为了完成命令的传送，我们整理了所有与打印相关的操作，并且称之为CUPS交流接口。

# 分类

* A cups管理
* B 打印机管理
* C 打印任务管理

# 详细列表

| 编号 | 简介/具体命令 | 任务所在类/备注 |
|---|---|---|
|A1|检测打印机是否运行|CommandTask|
||sh proot.sh /usr/bin/lpstat -r|判断输出的字符串中是否包含“scheduler is running”字段
|A2|启动CUPS|CommandTask
||sh proot.sh /usr/sbin/cupsd -f|
|A3|关闭cups|CommandTask|
|B1|查询可用打印机驱动|SearchModelsTask
||sh proot.sh lpinfo -m|会将所有PPD信息输出
|B2|添加打印机|AddPrinterTask
||sh proot.sh lpadmin -p name -v url -m model -o printer-is-shared=false -E
|B3|查询已添加打印机|ListAddedTask
||sh proot.sh lpstat -v
|B4|删除打印机|DeletePrinterTask
||sh proot.sh lpadmin -x printerName
|B5|修改打印机高级配置|UpdatePrinterCupsOptionsTask
||sh proot.sh lpoptions -p printerName [-o optionName=optionsVaule]...
|B6|查询打印机状态和配置|StateTask
||sh proot.sh lpoptions -p printerName -l
|B7|设置默认打印机|
|||暂时可以不用
|B8|查询可添加打印机|SearchPrintersTask
||sh proot.sh lpinfo -v -l
|B9|查询打印机高级设置|QueryPrinterCupsOptonsTask
||sh proot.sh lpoptions -p printerName -l|查询CUPS所有可用的设置
|B10|查询打印机设置|QueryPrinterOptonsTask
||sh proot.sh sh printerquery.sh printerName|查询安卓打印直接相关的参数，printerquery.sh里的命令如下：lpoptions -p $1 && lpoptions -p $1 -l
|B11|修改打印机设置|UpdatePrinterOptonsTask
||sh proot.sh lpoptions -p printerName -o MediaSizeName=mediaSizeValue -o ColorModeName=colorModeValue
|C1|打印|PrintTask
||sh proot.sh lp -d printerName fileName -o media=mediaSize [-o Resolution=resolution] [-o landscape] -t label -n copies -P ranges [-o fit-o-page]|中括号为可选参数
|C2|查询打印任务|JobQueryTask
||sh proot.sh sh /jobquery.sh | jobquery.sh里的命令如下：lpq -a && lpstat -l -o
|C3|取消打印任务|JobCancelTask
||sh proot.sh cancel jobId
|C4|暂停打印任务|JobPauseTask
||shproot.sh ipptool http://localhost:CUPS_PORT/jobs -d job-id=JobId hold-job.test|hold-job.test文件内为ipp命令内容
|C5|恢复打印任务|JobResumeTask
||sh proot.sh ipptool http://localhostCUPS_PORT/jobs -d job-id=JobId release-job.test|release-job.test文件内为ipp命令内容
|C6|取消所有打印任务|JobCancelAllTask|sh proot.sh cancel -a
|C7|暂停所有打印任务|JobPauseAllTask
||sh proot.sh sh hold_release.sh jobId1 jobId2 ... hold|参考 C4 调用ipptool发送命令，详见数据包里的hold_release.sh
|C8|恢复所有打印任务|JobResumeAllTask
||sh proot.sh sh hold_release.sh jobId1 jobId2 ... release|参考 C5 调用ipptool发送命令，详见数据包里的hold_release.sh

# 其他

由于使用`Runtime.getRuntime().exec(cmd, null, file);`函数执行命令，一次只能执行一条命令，所以需要多条命令时，将命令放入一个脚本文件里执行。
