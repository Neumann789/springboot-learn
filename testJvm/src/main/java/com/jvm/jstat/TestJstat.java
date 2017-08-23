package com.jvm.jstat;

import sun.tools.jstat.Jstat;

public class TestJstat {
	
	public static void main(String[] args) {
		
		args=new String[2];
		args[0]="-class";
		args[1]="11644";
		
		Jstat.main(args);
		
		
	}

}
