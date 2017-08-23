package com.learn.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

public class MailSendEvent extends ApplicationContextEvent{
	private String to;
	public MailSendEvent(ApplicationContext source,String to) {
		super(source);
		this.to=to;
	}
	
	public String getTo() {
		return to;
	}



	public void setTo(String to) {
		this.to = to;
	}



	private static final long serialVersionUID = 7769090812963667286L;

}
