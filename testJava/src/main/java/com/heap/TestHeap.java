package com.heap;

import java.util.ArrayList;
import java.util.List;

public class TestHeap {
	
	public static void main(String[] args) {
		
		int num = 40;
		List<byte[]> list = new ArrayList<>();
		for(int i=0;i<num;i++){
			list.add(new byte[1024*1024]);
		}
		
		System.out.println("创建"+num+"Mb");
		
	}

}
