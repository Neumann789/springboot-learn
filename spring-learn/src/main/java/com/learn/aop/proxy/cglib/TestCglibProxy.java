package com.learn.aop.proxy.cglib;

import com.learn.aop.ForumServiceImpl;

public class TestCglibProxy {
	
	public static void main(String[] args) {
		
		CglibProxy proxy=new CglibProxy();
		
		ForumServiceImpl forumServiceImpl=(ForumServiceImpl)proxy.getProxy(ForumServiceImpl.class);
		
		forumServiceImpl.removeForum(10);
		
		forumServiceImpl.removeTopic(20);
	}
}
