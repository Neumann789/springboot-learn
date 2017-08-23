package com.learn.aop;

public class ForumServiceImpl implements ForumService{
	
	public void removeTopic(int topicId){
		System.out.println("删除topic记录"+topicId);
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void removeForum(int forumId){
		System.out.println("删除forum记录"+forumId);
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
