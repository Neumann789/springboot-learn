package com.package0;

public class TestPackage {
	
	public static void main(String[] args) {
		
		System.out.println("String=="+getVersion(String.class));
		
		System.out.println("jode.bytecode.BinaryInfo=="+getVersion(jode.bytecode.BinaryInfo.class));
		
	}
	
	
	
	public static String getVersion(Class clazz){
		
		return clazz.getPackage().getImplementationVersion();
		
	}

}
