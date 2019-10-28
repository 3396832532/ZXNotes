## Linux查看CPU、内存占用的命令

### top

**top命令**可以实时动态地查看系统的整体运行情况，是一个综合了多方信息监测系统性能和运行信息的实用工具。通过top命令所提供的互动式界面，用热键可以管理。

### cat /proc/meminfo

查看RAM使用情况最简单的方法是通过 `/proc/meminfo`。这个动态更新的虚拟文件实际上是许多其他内存相关工具(如：free / ps / top)等的组合显示。`/proc/meminfo` 列出了所有你想了解的内存的使用情况。 

### free

free 命令是一个快速查看内存使用情况的方法，它是对 `/proc/meminfo` 收集到的信息的一个概述。

这个命令用于显示系统当前内存的使用情况，包括已用内存、可用内存和交换内存的情况

默认情况下 free 会以字节为单位输出内存的使用量

```shell
$ free
             total       used       free     shared    buffers     cached
Mem:       3566408    1580220    1986188          0     203988     902960
-/+ buffers/cache:     473272    3093136
Swap:      4000176          0    4000176
```

如果你想以其他单位输出内存的使用量，需要加一个选项，`-g` 为GB，`-m` 为MB，`-k` 为KB，`-b` 为字节

```shell
$ free -g
             total       used       free     shared    buffers     cached
Mem:             3          1          1          0          0          0
-/+ buffers/cache:          0          2
Swap:            3          0          3
```

如果你想查看所有内存的汇总，请使用 -t 选项，使用这个选项会在输出中加一个汇总行

```shell
$ free -t
             total       used       free     shared    buffers     cached
Mem:       3566408    1592148    1974260          0     204260     912556
-/+ buffers/cache:     475332    3091076
Swap:      4000176          0    4000176
Total:     7566584    1592148    5974436
```

