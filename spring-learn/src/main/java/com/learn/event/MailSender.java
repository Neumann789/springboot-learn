package com.learn.event;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * ClassName: MailSender <br/>
 * Function: 邮件发送者. <br/>
 * Date: 2017年8月23日 下午2:30:25 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class MailSender implements ApplicationContextAware{
	
	private ApplicationContext ctx;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx=applicationContext;
	}
	
	public void sendMail(String to){
		System.out.println("MailSender:模拟发送邮件...");
		MailSendEvent mse=new MailSendEvent(this.ctx, to);
		ctx.publishEvent(mse);
	}

}
