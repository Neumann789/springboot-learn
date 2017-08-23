package com.learn.internationalization;

import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TestMessageFormat{
	public static void main(String[] args) {
		
		//信息格式化
		String pattern1="{0},你好！你于{1}在工商银行存入{2}元。";
		String pattern2="At {1,time,short} On {1,date,long},{0} paid {2,number,currency}.";
		
		//用于动态替换占位符的参数
		Object[] params={"john",new GregorianCalendar().getTime(),1.0E3};
		
		//使用默认本地化对象格式化信息
		String msg1=MessageFormat.format(pattern1, params);
		
		//使用指定的本地化格式化信息
		MessageFormat mf=new MessageFormat(pattern2,Locale.US);
		String msg2=mf.format(params);
		
		System.out.println(msg1);
		System.out.println(msg2);
		
	}
}
