package com.learn.bean;

import org.springframework.stereotype.Service;

import com.learn.annotation.bean.TestDao;

public class MyBean {
	
	private TestDao dao;
	
	public void handle(){
		
		System.out.println("MyBean.handle()");
		
		dao.insert();
		
	}

	public TestDao getDao() {
		return dao;
	}

	public void setDao(TestDao dao) {
		this.dao = dao;
	}
	
	
	
}
