package com.jode.test;

import net.sf.jode.decompiler.Main;

public class TestJode {
	
	public static void main(String[] args) throws Throwable {
		//testNetJode();
		
		testJode();
		
	}
	


	private static void testNetJode() throws Throwable {
		String[] args;
		args=new String[3];
		args[0]="-d";
		args[1]="output";
		args[2]="F:\\git20170716\\springboot-learn\\testJava\\libs\\javosize-1.1.3.jar";
		Main.main(args);
	}
	
	private static void testJode() throws Throwable {
		String[] args;
		args=new String[3];
		args[0]="-d";
		args[1]="output";
		args[2]="F:\\git20170716\\springboot-learn\\testJava\\libs\\javosize-1.1.3.jar";
		jode.decompiler.Main.main(args);
	}

}
