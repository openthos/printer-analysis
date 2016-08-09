# 1 features

完成 | 简介| 详细
--- | --- | --- |
√ | 制作CUPS运行环境，移植CUPS程序 | 包含CUPS，cups-filter，ghostscript
√ | 移植foo2zjs驱动 |
√ | 移植奔图驱动
√ | 移植EPSON驱动
√ | 移植hpcups && hplip plugin驱动
√ | 移植嘉华龙马驱动
√ | 接入安卓打印
√ | 打印任务管理
√ | 打印机管理
√ | 完善打印参数（颜色、页码选择等）
√ | 打印机高级设置（CUPS中包含的详细设置）
√ | 在系统通知栏实现打印状态区
√ | 添加打印机时，自动选择匹配驱动
√ | 插入新打印机自动提示是否添加
√ | 第一次启动系统自动初始化数据包
√ | 与windows系统的网络打印
√ | 与Linux类系统的网络打印
√ | 连接自带网络功能的打印机进行网络打印

# 2 bugs

解决 | 简介| 详细
--- | --- | --- |
√ | 打印任务状态显示unknown
√ | 打印任务从暂停状态恢复失败
√ | 解决gs调用错误
√ | 修复tar解压数据包部分文件权限丢失
√ | 去除奔图ppd文件名中的空格，解决解析问题
√ | 修复打印管理界面偶尔错乱BUG
√ | 添加本地/网络打印机字段为空判断（也不能有空格）
√ | 修改CUPSD进程位置，解决后台不运行问题
√ | 把数据包里busybox的文件全部换成软连接，减少体积
√ | 存在打印任务不及时更新 | https://dev.openthos.org/zentao/zentao/bug-view-195.html
√ | 打印机调整配置页面点击确定后打印服务崩溃 | https://dev.openthos.org/zentao/zentao/bug-view-197.html
√ | 缺少部分纸张大小参数MediaSize对应项 | https://dev.openthos.org/zentao/zentao/bug-view-198.html
√ | 已删除的打印机，仍显示在打印处理服务-所有打印机列表中。需更新。 | https://dev.openthos.org/zentao/zentao/bug-view-227.html
√ | HP pro400网络打印：点打印，弹出“打印处理服务”已停止。无法打印。 | https://dev.openthos.org/zentao/zentao/bug-view-228.html
 | 已添加设备 -名称有误，点进去后显示名称和打印测试页是正确的。 | https://dev.openthos.org/zentao/zentao/bug-view-229.html
