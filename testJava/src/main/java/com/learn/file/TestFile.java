package com.learn.file;

import java.io.File;

public class TestFile {
	
	public static void main(String[] args) throws Throwable {
		
		File file = new File("C:\\Users\\FANGHUABAO\\AppData\\Local\\Temp\\hsperfdata_FANGHUABAO");
		
		while(true){
			
			for(File f:file.listFiles()){
				System.out.print(f.getName()+",");
			}
			System.out.println();
			Thread.sleep(100);
			
		}

	}

}
