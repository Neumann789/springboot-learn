package com.learn.cow;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author fanghuabao
 *http://www.cnblogs.com/dolphin0520/p/3938914.html
 *Copy-On-Write简称COW，是一种用于程序设计中的优化策略。其基本思路是，从一开始大家都在共享同一个内容，
 *当某个人想要修改这个内容的时候，才会真正把内容Copy出去形成一个新的内容然后再改，这是一种延时懒惰策略。
 *从JDK1.5开始Java并发包里提供了两个使用CopyOnWrite机制实现的并发容器,它们是CopyOnWriteArrayList和CopyOnWriteArraySet。
 *CopyOnWrite容器非常有用，可以在非常多的并发场景中使用到
 *
 *问题：
 *　　内存占用问题。
 *因为CopyOnWrite的写时复制机制，所以在进行写操作的时候，内存里会同时驻扎两个对象的内存，旧的对象和新写入的对象
 *（注意:在复制的时候只是复制容器里的引用，只是在写的时候会创建新对象添加到新容器里，而旧容器的对象还在使用，所以有两份对象内存）。
 *如果这些对象占用的内存比较大，比如说200M左右，那么再写入100M数据进去，内存就会占用300M，那么这个时候很有可能造成频繁的Yong GC和Full GC。
 *之前我们系统中使用了一个服务由于每晚使用CopyOnWrite机制更新大对象，造成了每晚15秒的Full GC，应用响应时间也随之变长。
针对内存占用问题，可以通过压缩容器中的元素的方法来减少大对象的内存消耗，比如，如果元素全是10进制的数字，可以考虑把它压缩成36进制或64进制。或者不使用CopyOnWrite容器，
而使用其他的并发容器，如ConcurrentHashMap。

	数据一致性问题。
	CopyOnWrite容器只能保证数据的最终一致性，不能保证数据的实时一致性。所以如果你希望写入的的数据，马上能读到，请不要使用CopyOnWrite容器。
 */
public class CopyOnWriteArrayListTest {
	public static void main(String[] args) {
		
		CopyOnWriteArrayList<String> cowList=new CopyOnWriteArrayList<>();
		
		cowList.add("1");
		
	}
}
