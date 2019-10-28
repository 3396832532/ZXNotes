# Linux文件系统和innode

转载: <https://www.cnblogs.com/zengkefu/p/5603187.html>

如Windows所用的文件系统主要有FAT16、FAT32和NTFS，Linux所用的文件系统主要有ext2、ext3、Ext4和ReiserFS等。

### 文件系统工作原理

文件系统的工作与操作系统的文件数据有关。现在的操作系统的文件数据除了文件实际内容外，通常含有非常多的属性，例如文件权限(rwx)与文件属性(所有者、用户组、时间参数等)。

**文件系统通常会将这两部份的数据分别存放在不同的区块，权限与属性放到inode中，数据则放到block区块中**。另外，还有一个超级区块(super block)会记录整个文件系统的整体信息，包括 inode与block的总量、使用量、剩余量等等等。

每个 inode 与 block 都有编号，至于这三个数据的意义可以简略说明如下：

* 1)、superblock：记录此 filesystem 的整体信息，包括inode/block的总量、使用量、剩余量， 以及文件系统的格式与相关信息等；
* 2)、inode：**记录文件的属性，一个文件占用一个inode，同时记录此文件的数据所在的 block 号码**；
* 3)、block：实际记录文件的内容，若文件太大时，会占用多个 block 。

由于每个 inode 与 block 都有编号，而每个文件都会占用一个 inode ，inode 内则有文件数据放置的 block 号码。 因此，我们可以知道的是，**如果能够找到文件的 inode 的话，那么自然就会知道这个文件所放置数据的 block 号码， 当然也就能够读出该文件的实际数据了**。这是个比较有效率的作法，因为如此一来我们的磁盘就能够在短时间内读取出全部的数据， 读写的效能比较好。

我们将 inode 与 block 区块用图解来说明一下，如下图所示，文件系统先格式化出 inode 与 block 的区块，假设某一个档案的属性与权限数据是放置到 inode 4 号(下图较小方格内)，而这个 inode 记录了档案数据的实际放置点为 2, 7, 13, 15 这四个 block 号码，此时我们的操作系统就能够据此来排列磁盘的阅读顺序，可以一口气将四个 block 内容读出来！ 那么数据的读取就如同下图中的箭头所指定的模样了。

![1555860724895](assets/1555860724895.png)

这种数据存取的方法我们称为索引式文件系统(indexed allocation)。下面我们来看一下windows系统中的FAT，这种格式的文件系统并没有 inode 存在，所以 FAT 没有办法将这个文件的所有 block 在一开始就读取出来。每个 block 号码都记录在前一个 block 当中， 他的读取方式有点像底下这样:

![1555860739978](assets/1555860739978.png)

![1555860683719](assets/1555860683719.png)

### inode的内容

inode 包含文件的元信息，具体来说有以下内容：

```java
* 文件的字节数
* 文件拥有者的 User ID
* 文件的 Group ID
* 文件的读、写、执行权限
* 文件的时间戳，共有三个：ctime：指inode上一次变动的时间，mtime：指文件内容上一次变动的时间，atime：指文件上一次打开的时间。
* 链接数，即有多少文件名指向这个inode
* 文件数据block的位置
```

可以用 `stat` 命令，查看某个文件的 inode 信息：

```shell
$ stat abby.txt
  File: ‘abby.txt’
  Size: 22              Blocks: 8          IO Block: 4096   regular file
Device: fd01h/64769d    Inode: 2106782     Links: 2
Access: (0644/-rw-r--r--)  Uid: (    0/    root)   Gid: (    0/    root)
Access: 2018-07-22 16:37:18.640787898 +0800
Modify: 2018-07-22 16:37:10.678607855 +0800
Change: 2018-07-22 16:37:10.833611360 +0800
 Birth: -
```

总之，除了文件名以外的所有文件信息，都存在inode之中。至于为什么没有文件名，下文会有详细解释。

### inode的大小

<div align="center"><img src="assets/0417_WTD_Linux_F1.gif"></div><br>
inode 也会消耗硬盘空间，所以硬盘格式化的时候，操作系统自动将硬盘分成两个区域。一个是**数据区**，存放文件数据；另一个是 **inode 区**（inode table），存放 inode 所包含的信息。

每个 inode 节点的大小，一般是 128 字节或 256 字节。inode 节点的总数，在格式化时就给定，一般是每 1KB 或每 2KB 就设置一个 inode。假定在一块 1GB 的硬盘中，每个 inode 节点的大小为 128 字节，每 1KB 就设置一个 inode，那么 inode table 的大小就会达到 128 MB，占整块硬盘的 12.8%。

查看每个硬盘分区的 inode 总数和已经使用的数量，可以使用df 命令。

```shell
$ df -i
Filesystem      Inodes  IUsed   IFree IUse% Mounted on
/dev/vda1      3932160 189976 3742184    5% /
devtmpfs        998993    339  998654    1% /dev
tmpfs          1001336      1 1001335    1% /dev/shm
tmpfs          1001336    397 1000939    1% /run
tmpfs          1001336     16 1001320    1% /sys/fs/cgroup
tmpfs          1001336      1 1001335    1% /run/user/0
```

查看每个 inode 节点的大小，可以用如下命令：

```shell
$ sudo dumpe2fs -h /dev/vda1 | grep "Inode size"
dumpe2fs 1.42.9 (28-Dec-2013)
Inode size:               256
```

由于每个文件都必须有一个inode，因此有可能发生inode已经用光，但是硬盘还未存满的情况。这时，就无法在硬盘上创建新文件。

### inode号码

每个 inode 都有一个号码，**操作系统用 inode 号码来识别不同的文件**。

这里值得重复一遍，Unix/Linux 系统内部不使用文件名，而使用 inode 号码来识别文件。对于系统来说，文件名只是 inode 号码便于识别的别称或者绰号。

表面上，用户通过文件名，打开文件。实际上，系统内部这个过程分成三步：首先，系统找到这个文件名对应的 inode 号码；其次，通过 inode 号码，获取 inode 信息；最后，根据 inode 信息，找到文件数据所在的 block，读出数据。

使用 `ls -i` 命令，可以看到文件名对应的 inode 号码：

```shell
$ ls -i test.txt
1712426 test.txt
```

