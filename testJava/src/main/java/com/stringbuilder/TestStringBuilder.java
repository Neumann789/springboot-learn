package com.stringbuilder;

public class TestStringBuilder {
	
	public static void main(String[] args) {
		
		StringBuilder sb=new StringBuilder("hello,");
		
		sb.delete(sb.length()-1, sb.length());
		
		System.out.println(sb);
		
		String str="PayRsp com.zb.payment.msd.facade.service.TestService.test(PayReq)";
		
		String[] names=str.split("\\.");
		
		System.out.println(names[names.length-2]+"."+names[names.length-1]);
		
	}

}
