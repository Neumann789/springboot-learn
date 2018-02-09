package com.nonefly.vesion1;

/**
 * 
 * ClassName: Hello <br/>
 * Function: 测试类. <br/>
 * Date: 2018年2月9日 下午3:30:27 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class Hello extends Thread implements Runnable{
	
	private String name2;
	
	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	private int age;
	
	private static String address;
	
	static{
		
		address = "china";
		
	}
	
	public static void main(String[] args) {
		
		int a = 0;
		String b = "b";
		try {
			sayHello("jack");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String sayHello(String name) throws Exception{
		return "hello:"+name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	

}
