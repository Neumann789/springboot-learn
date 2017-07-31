package com.test;

import java.math.BigDecimal;

public class TestBigdecimal {
	
	public static void main(String[] args) {
		String   f   =   "111231"; 
		BigDecimal   b   =   new   BigDecimal(f); 
		//保留2位小数
		String   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).toString(); 
		System.out.println(f1);
	}

}
