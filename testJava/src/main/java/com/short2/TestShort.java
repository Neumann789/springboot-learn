package com.short2;

public class TestShort {
	
	
	public static void main(String[] args) {
		
		short v=-9541;
		//v=128;
		
		printInfo(v);
		
		System.out.println((byte)v);
		System.out.println((byte)(v >>> 8));
		
		
	}
	
    /**
     * 输出一个int的二进制数
     * @param num
     */
    public static void printInfo(int num){
        System.out.println(Integer.toBinaryString(num));
    }

}
