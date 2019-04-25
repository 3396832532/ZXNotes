[linux内核](https://www.baidu.com/s?wd=linux%E5%86%85%E6%A0%B8&tn=SE_PcZhidaonwhc_ngpagmjz&rsv_dl=gh_pc_zhidao)的三种主要调度策略： 

1，SCHED_OTHER 分时调度策略，

 2，SCHED_FIFO实时调度策略，先到先服务

 3，SCHED_RR实时调度策略



[时间片轮转](https://www.baidu.com/s?wd=%E6%97%B6%E9%97%B4%E7%89%87%E8%BD%AE%E8%BD%AC&tn=SE_PcZhidaonwhc_ngpagmjz&rsv_dl=gh_pc_zhidao)  实时进程将得到优先调用，实时进程根据实时优先级决定调度权值。

分时进程则通过nice和counter值决定权值，nice越小，counter越大，被调度的概率越大，也就是曾经使用了cpu最少的进程将会得到优先调度。

 SHCED_RR和SCHED_FIFO的不同： 当采用SHCED_RR策略的进程的时间片用完，系统将重新分配时间片，并置于就绪队列尾。

s放在队列尾保证了所有具有相同优先级的RR任务的调度公平。

 SCHED_FIFO一旦占用cpu则一直运行。一直运行直到有更高优先级任务到达或自己放弃。

 如果有相同优先级的实时进程（根据优先级计算的调度权值是一样的）已经准备好，FIFO时必须等待该进程主动放弃后才可以运行这个优先级相同的任务。而RR可以让每个任务都执行一段时间。 

 相同点： **RR和FIFO都只用于实时任务**。 创建时优先级大于0(1-99)。 按照可抢占优先级调度算法进行。 就绪态的实时任务立即抢占非实时任务。   