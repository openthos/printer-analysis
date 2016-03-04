1. bin目录
========
###1.1 cancel
　　取消存在的打印工作。

###1.2 cups-config
　　是cups的配置应用程序。由应用开发者根据编译器、链接器、过滤器的安装目录、配置文件以及驱动等因素来确定具体的命令行参数，从而使用此命令进行配置。

###1.3 cupstestdsc
　　测试PostScript文件与Adobe PostScript语言文件结构化约定格式(即Adobe规定的PostScript文件格式)的一致性。测试的结果以及任何输出都被发送至标准输出。使用时可以指定.ps格式的文件名，也可以直接从标准输入读取。
　　其不会验证PostScript代码本身，开发者必须确认他们测试的PostScript内容遵循了Adobe定义的规则，尤其是，所有的页都必须相互独立，除了用于页描述外的代码不会影响图形状态（当前字体、颜色、变换矩阵等）外，特定的设备命令例如setpagedevice(PostScript语法中用于设置一页大小的命令)等不应该被使用。

###1.4 cupstestppd
　　测试描述文件形式说明的一致性，其也可以列举一个PPD文件支持的选项以及可用字体。测试或者其他操作的输出结果传送至标准输出。使用时可以指定.ps格式文件名进行一个或多个文件的测试，也可以测试标准输入中输入的PPD文件。

###1.5 ipptool
　　接收描述一个或多个IPP请求的自由形式的纯文本文件，发送IPP请求至特定的URI（统一资源标识符，Web上可用的每种资源由一个URI进行定位）并且测试或展示结果。每一个已命名文件定义了一个或多个请求，包括预期的相应状态、属性和值。输出在标准输出中，形式为纯文本、格式化文本、CSV格式文件、XML格式文件中的一种，当退出状态不是0时表示一个或多个测试出错。IPP请求文件的格式的具体描述在ipptoolfile中 。
```
    可接受的标准文件（IPP请求文件）： color.jpg
	　　　　　　　　　　　　　　　　　　　create-printer-subscription.test
	　　　　　　　　　　　　　　　　　　　document-a4.pdf
	　　　　　　　　　　　　　　　　　　　document-a4.ps
	　　　　　　　　　　　　　　　　　　　document-letter.pdf
	　　　　　　　　　　　　　　　　　　　document-letter.ps
	　　　　　　　　　　　　　　　　　　　get-completed-jobs.test
	　　　　　　　　　　　　　　　　　　　get-jobs.test
	　　　　　　　　　　　　　　　　　　　get-notifications.test
	　　　　　　　　　　　　　　　　　　　get-printer-attributes.test
	　　　　　　　　　　　　　　　　　　　get-subscriptions.test
	　　　　　　　　　　　　　　　　　　　gray.jpg
	　　　　　　　　　　　　　　　　　　　ipp-1.1.test
	　　　　　　　　　　　　　　　　　　　ipp-2.0.test
	　　　　　　　　　　　　　　　　　　　ipp-2.1.test
	　　　　　　　　　　　　　　　　　　　ipp-2.2.test
	　　　　　　　　　　　　　　　　　　　ipp-everywhere.test
	　　　　　　　　　　　　　　　　　　　onepage-a4.pdf
	　　　　　　　　　　　　　　　　　　　onepage-a4.ps
	　　　　　　　　　　　　　　　　　　　onepage-letter.pdf
	　　　　　　　　　　　　　　　　　　　onepage-letter.ps
	　　　　　　　　　　　　　　　　　　　print-job.test
	　　　　　　　　　　　　　　　　　　　print-job-deflate.test
	　　　　　　　　　　　　　　　　　　　print-job-gzip.test
	　　　　　　　　　　　　　　　　　　　testfile.jpg
	　　　　　　　　　　　　　　　　　　　testfile.pcl
	　　　　　　　　　　　　　　　　　　　testfile.pdf
	　　　　　　　　　　　　　　　　　　　testfile.ps
	　　　　　　　　　　　　　　　　　　　testfile.txt
	　　　　　　　　　　　　　　　　　　　validate-job.test
```
###1.6 lp
    需打印的文件或者改变一个未决工作，使用文件名来通过标准输入指明要打印的文件。
    提供了多种方式设置默认目标，首先会查询```LPDEST```和```PRINTER```环境变量，如果两者都没有设置但是有使用lpoptions命令进行设置就采用此设置，最后采用lpadmin命令的设置作为默认设置。

###1.7lpoptions
　　显示或设置打印机选项以及默认值，当没有任何变量时lpoptions就显示打印机默认值。lpoptions命令通过lp和lpr命令提交工作时来调用，用于设置选项。
　　当以root用户运行其时，其为所有用户在/etc/cups/lpoptions文件中获取、设置选项或实例。
　　配置文件：~/.cups/lpoptions：由除root以外的用户创建的用户默认选项文件
　　　　　　　/etc/cups/lpoptions：由root用户创建的系统默认选项文件

###1.8 lpq
　　显示打印队列中与用户相关的特定或全部工作的状态，命令不带由任何参数则报告当前队列中的任何工作。

###1.9 lpr
　　当设备可以运行时使用一个守护进程打印已命名文件。如果命令中没有文件名，那么认定打印标准输入？
　　（lp和lpr都是用于提交打印工作的命令，两者之间的区别在于参数选项的不同，lp更为复杂）
```
相关文件：/etc/passwd        本地用户数据库
　　　　　/etc/printcap        打印机功能数据库
　　　　　/usr/sbin/lpd*        行打印机守护进程
　　　　　/var/spool/output/*        假脱机目录（应该就是工作队列的目录）
　　　　　/var/spool/output/*/cf*        守护进程控制文件
　　　　　/var/spool/output/*/df*        跟控制文件有关的数据文件？
　　　　　/var/spool/output/*/tf*        控制文件的临时副本
```
        
###1.10 lprm
    打印机的打印队列中删除一个或多个工作。假脱机目录对于用户们来说是被保护的，所以一般来说使用lprm是一个用户能够删除一个打印工作的唯一方法。打印工作的所有者是由执行lpr命令的主机名以及登录名决定的。
```
	相关文件：/etc/printcap     打印机特性文件
		  /var/spool/output/*    假脱机目录
        　　　　　/var/spool/output/*/lock        用于获取当前守护进程的PID以及当前活动的工作的总数的锁文件
		```

###1.11 lpstat
　　显示当前工作、打印机的状态信息。当lpstat命令没有参数时，会根据当前用户列举工作队列。

###1.12 ppdc
　　将PPDC源文件编译成一个或多个PPD文件，已经被弃用并且将在未来的某个发行版本被删除。

###1.13 ppdhtml
　　读取一个驱动信息文件并产生一个HTML摘要页，此摘要页在一个文件中列举所有的驱动程序以及支持的选项。

###1.14 ppdi
　　导入一个或多个PPD文件至一个PPD编译器源文件。同个PPD的多种语言统一为一个简单的打印机定义，以此来加强本地所有变化的精确。
　　使用-o选项可以指定PPD源文件进行更新。如果这个源文件不存在，那么就会创建一个新的源文件。如果这源文件存在，就会在命令行被整合为（一个或多个）新的PPD文件。如果没有指定源文件，就会使用名为"ppdi.drv"的文件。

###1.15 ppdmerge
　　整合两个或更多个PPD文件成为一个简单的、多语言PPD文件。
　　ppdmerge不检查已整合PPD文件是否是同一个设备，要注意整合不同设备的PPD文件会造成不可预料的后果。

###1.16 ppdpo
　　从PPDC源文件摘取用户界面字符串并更新，在GNU gettext格式和苹果OS的字符串格式目录源文件中选择一个用来转换
___
___
___

#2. sbin目录         
###2.1 cupsaddsmb
　　将打印机接至SAMBA软件（2.2.0版本或更高）以供Windows客户端使用。根据samba配置的具体情况，可能需要密码来连接打印机，之后需要Windows系统的打印机驱动文件。

###2.2 cupsd
　　是CUPS的调度程序，实现了一个基于IPP协议（2.1）的打印系统。
　　如果命令行中没有指定选项，那么默认使用/etc/cups/cupsd.conf文件作为配置。
　　运行时默认作为一个后台守护进程，可以用此命令来测试自己编辑的配置文件是否正确并进行使用。

###2.3 cupsaccecpt（cupsdisable/cupsenable/cupsreject/reject）
　　允许将打印请求排队到指定的目标
　　cupsreject：拒绝将打印请求排队到指定的目标， -r可注释拒绝原因，无注释则默认为"Reason Unknown"


###2.4 lpc
　　由系统管理员使用，用来控制行式打印机系统的操作。
　　具体功能：a、启用或禁用一个打印机；
　　　　　　　b、启用或禁用一个打印机的工作队列；
　　　　　　　c、重新排列工作队列中的工作顺序；
　　　　　　　d、找到打印机的状态、相关工作队列以及守护进程。                   
　　/etc/printcap：所有行式打印机信息配置文件

###2.5 lpmove
　　移动指定的或所有工作至目标。工作名可以是 工作ID 或 旧目标-工作ID。
　　例：lpmove 123 newprinter；lpmove oldprinter-123 newprinter

###2.6 cupsctl
　　更新或查询一个服务器的cupsd.conf文件。当执行时配置文件没有任何改变的话，当前配置的具体值以每一行"name=value"的形式显示在标准输出上。不能用cupsctl来设置监听或端口指令集。

###2.7 cupsfilter
　　前端CUPS过滤子系统，允许将文件转换为一个特定的形式。默认情况下，其产生一个PDF文件，转换的文件发送至标准输出。判断其选项，转换时应需要PPD文件，其中应记录了要使用的过滤器。可以通过此程序来使用合适的过滤器进行自己想进行的文件格式转换

###2.8 lpadmin
　　配置由CUPS提供的打印机及类队列（class queues），同样可以用于设置服务器的默认打印机及类。

###2.9 lpinfo
　　列举出CUPS服务器已知的可用的设备或驱动，选项-m列举可用的驱动、-v列举所有可用的设备。
        
		___
		___
		___
#3. 部分相关文件
###3.1 classes.conf
		　　cups的类配置文件，定义了本地可用的打印机类。一般位于/etc/cups目录下，由cupsd程序添加或删除类时自动生成。

###3.2 printers.conf
　　记录所有本地可用的打印机信息，由cups程序添加或删除打印机时自动生成。

###3.3 subscriptions.conf
　　描述了本地所有活跃的subscriptions，当创建、更新或取消subscriptions时由cupsd程序自动生成。

###3.4 cupsd.conf
　　cups的服务器配置文件，其用来配置cups调度器cupsd。
　　注意：可以在cupsd.conf中使用的文件、目录以及用户配置指令现在都存储在cups-files.conf文件中，以防止某些类型的特权升级攻击。

###3.5 snmp.conf
　　使用Net-SNMP库编译的应用程序同城使用一个或多个配置文件来控制不同方面的操作，这些文件（snmp.conf、snmp.local.conf）
　　按照snmp_config管理页上的描述可以被存储在多个地点。
　　注意：/etc/snmp/snmp.conf中的设置由所有用户共享，而~/.snmp/snmp.conf则是用户个人的设置文件。

###3.6 mime.convs
　　描述了所有可用的过滤器（用于将文件转变为另一种形式），标准过滤器支持text、PDF、PostScript、HP-GL/2以及许许多格式的图像文件。
　　额外的过滤器可以增添至mime.convs文件或是（最好）增添至CUPS配置目录。
　　文件中过滤器描述行形式为：```super/type    super/type    cost    filter```，
　　　　　　　　　　　　　例：```applications/postscript  application/vnd.cupps-raster  50  pstoraster```。

###3.7 mime.types
　　记录了已被认知的文件类型。
　　额外的过滤器可以增添至mime.types文件或是（最好）增添至CUPS配置目录并在名称后添加".types"。
　　规则行形式：```super/type    rule    [...ruleN]```
　　首先是类型名称，之后是可选的一系列文件识别规则，用于自动识别打印文件和网页文件。当cups需要确定一个给定文件的类型时，会检查所有在.types文件中定义的每一个MIME类型。当两个类型同时匹配规则时，就根据类型名以及优先级来挑选

___
___
___
#4. 部分相关可执行文件

###4.1 filter
　　使用形式：```filter    job    user    title    num-copies    options    [filename]```
　　cups文件转换过滤器接口。每一个过滤器能够转换一个或多个形式至其他形式，此种形式可以被直接打印或输出至其他过滤器就能够得到可以打印的格式文件。
　　警告：cups过滤器不是给用户直接使用的，除非你是开发者并且知道你在做什么，否则请不要直接运行过滤器。你可以使用cupsfilter程序来使用合适的过滤器进行你需要的文件转换。

###4.2 backend
　　使用形式：```backend    job    user    title    num-copies    options    [filename]```
　　cups后台传输接口。它是一种特殊的过滤器，用于发送数据至或发现系统中的不同设备。
　　警告：此后台传输接口不是设计给用于直接运行的。除非是开发者，否则使用lp或lpr程序发送一个打印工作，或者通过lpinfo程序来查询使用后台传输接口的可用的打印机。（SNMP后台传输接口例外，查询snmpbackend来获取更多信息）

###4.3 cups-deviced
　　使用形式：```cups-deviced    request-id    limit    user-id    options```  
　　轮询在/usr/lib/cups/backend或/usr/libexec/cups/backend(OS X)目录来获取可用设备表，根据被请求的属性查找并对输出进行相应裁剪。当cupsd回应一个CUPS-Get-Devices请求时调用此程序，其输出是一个IPP响应信息。
　　参数解释：```requset-id```变量是原始IPP请求的请求ID号，通常是1；```limit```变量时原始IPP请求的限制值，0代表没有限制；```user-id```变量是原始IPP请求中的请求用户名；```options```变量是一个由空格分隔的属性表。


###4.4 cups-drivered
　　使用形式：```cups-drivered    cat    ppd-name```
　　　　　　　```cups-drivered    list    request_id    limit    options```
　　根据ppd-make以及被请求的属性查找PPD文件并对输出进行相应裁剪，其运行是为了响应CUPS-Add-Modify-Printer或者CUPS-Get-Devices请求。驱动文件可以是/usr/share/cups/model目录下的PPD文件或者在/usr/lib/cups/driver目录下的程序。
　　第一种使用形式输出指定名称的PPD文件至标准输出，输出的形式是一个未压缩的PPD文件；第二种形式根据```options```列举指定可用的PPD文件或者制造商至标准输出，输出形式是一个IPP响应信息。参数解释与```cups-drivered```一致













