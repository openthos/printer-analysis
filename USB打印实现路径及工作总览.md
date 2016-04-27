# 安卓USB打印实现路径

## 参考

>  [20160128_何兴鹏_android_x86打印机开发调研.md][3]

>  [20160224_何兴鹏_android打印机开发进展.md][1]

## 实现路径图：

![path.png][2]

实线为拟实现路径。如上图所示，工作主要分为打印插件制作和CUPS移植两个部分。

## ...

...

# 工作总览


该部分为工作细节，每个细分任务有相对应的issue。



```
+---------------------------+
|                           |
| Android printing plugin   |
|                           |
+------------+--------------+
             |                         +----------> 分析安卓打印子系统 ✔ issue #11
             |                         |
             |                         |
             |                         |
             |                         |
             |                         |
             |                         +
             +------->  连接安卓系统打印机制
             |
             |                         +----------> 分析设计打印机配置界面 ✔ issue #1
             |                         |
             |                         |
             |                         |
             |                         +
             +------->  制作打印相关界面
             |
             |                         +----------> 分析CUPS API
             |                         |
             |                         +----------> APP连接最简驱动 ✔ issue #13
             |                         +
             +------->  连接CUPS打印系统



  +--------------------------+
  |                          |
  |       CUPS porting       |
  |                          |                           +---> 分析CUPS系统由哪些包组成 ✔
  +--------------------------+                           |
                                                         +---> 分析cups功能 ✔ issue #3
              +                                          |
              |                                          +---> 调研优先支持打印机在Linux上的支持情况 ■ issue #9
              |                                          +
              |                     +------> 分析CUPS系统
              |                     |                    +
              |                     |                    +---> CUPS如何发送数据到USB打印机 ■ issue #18
              |                     |                    +
              |                     |                    +---> CUPS如何管理不同打印机驱动 ✔ issue #5
              |                     |
              |                     |
              |                     |
              |                     +
              +------> 移植CUPS系统
                                    +                    +---> 针对HP P1108进行最简移植实验 ✔ issue #4
                                    |                    |
									|                    +---> 移植CUPS包  ■ issue #17
                                    |                    |
                                    |                    +
                                    +------> 移植方案一：静态编译
                                    |
                                    |
                                    |
                                    |
                                    |                    +---> 尝试仅编译CUPS包 ■ issue #2
                                    |                    |
                                    |                    +
                                    +------> 移植方案二：基于Bionic Libc
                                    |
                                    |
                                    |
                                    +------> 封装

                                    
Chart Power By ASCIIflow.com

```



[1]: https://github.com/openthos/printer-analysis/blob/master/report%2F20160224_%E4%BD%95%E5%85%B4%E9%B9%8F_android%E6%89%93%E5%8D%B0%E6%9C%BA%E5%BC%80%E5%8F%91%E8%BF%9B%E5%B1%95.md
[2]: https://github.com/openthos/printer-analysis/raw/master/report/raw/3987526971.png
[3]: https://github.com/openthos/printer-analysis/blob/master/report%2F20160128_%E4%BD%95%E5%85%B4%E9%B9%8F_android_x86%E6%89%93%E5%8D%B0%E6%9C%BA%E5%BC%80%E5%8F%91%E8%B0%83%E7%A0%94.md