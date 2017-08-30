package com.jvm.classfile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class ReadClassFile {
	
	public static byte[] IMAGE=new byte[4];
	
	public static byte[] MINOR_VERSION=new byte[2];
	
	public static byte[] MAJOR_VERSION=new byte[2];
	
	public static byte[] CONSTANT_POOL_COUNT=new byte[2];
	
	public static byte[] CONSTANT_POOL=null;//这个值由CONSTANT_POOL_COUNT决定
	
	public static byte[] U1=new byte[1];
	
	public static byte[] U2=new byte[2];
	
	public static byte[] U3=new byte[3];
	
	public static byte[] U4=new byte[4];
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws Throwable {
		
		String testClassPath="F:\\git20170716\\springboot-learn\\testJvm\\target\\classes\\com\\jvm\\classfile\\HelloWorld.class";
		
		InputStream ins=  new FileInputStream(testClassPath);
		
		readMagic(ins);
		
		readMinorVersion(ins);
		
		readMajorVersion(ins);
		
		readConstantPool(ins);
	}
	

	public static void readMagic(InputStream ins) throws Throwable{
		ins.read(IMAGE);
		printTileAnd16("IMAGE",IMAGE);
	}
	
	public static void readMinorVersion(InputStream ins) throws Throwable{
		ins.read(MINOR_VERSION);
		printTileAnd10("MINOR_VERSION",MINOR_VERSION);
	}
	
	public static void readMajorVersion(InputStream ins) throws Throwable{
		ins.read(MAJOR_VERSION);
		printTileAnd10("MAJOR_VERSION",MAJOR_VERSION);
	}
	
	
	
	public static void readConstantPool(InputStream ins) throws Throwable {
		ins.read(CONSTANT_POOL_COUNT);
		printTileAnd10("CONSTANT_POOL_COUNT", CONSTANT_POOL_COUNT);
		int constantPoolCount=Integer.parseInt(binary(CONSTANT_POOL_COUNT, 10));
		CONSTANT_POOL=new byte[constantPoolCount-1];
		ins.read(CONSTANT_POOL);
		
		parseConstantPool();
		
	}
	
	public static void parseConstantPool(){
		
		extractByteArrFromConstantPool(U1);
		String firstTagNum=binary(U1, 10);
		System.out.println(firstTagNum);
		if(ConstantType.CONSTANT_Class.getTag().equals(firstTagNum)){
			extractByteArrFromConstantPool(U2);
			System.out.println(binary(U2, 10));
		}
		
	}
	
	
	
	
	
	public static void extractByteArrFromConstantPool(byte[] buf){
		
		for(int i=0;i<buf.length;i++){
			
			buf[i]=CONSTANT_POOL[i];
			
		}
		
		CONSTANT_POOL=Arrays.copyOfRange(CONSTANT_POOL, buf.length, CONSTANT_POOL.length-1);
		
	}
	
	public static void printTileAnd16(String title,byte[] buf){
		System.out.print(title+":");
		printHexString(buf);
	}
	
	public static void printTileAnd10(String title,byte[] buf){
		System.out.print(title+":");
		printIntString(buf);
	}
	public static void printIntString( byte[] b) {
		System.out.println(binary(b, 10));
	}
	public static void printHexString( byte[] b) {    
		   for (int i = 0; i < b.length; i++) {   
		     String hex = Integer.toHexString(b[i] & 0xFF);   
		     if (hex.length() == 1) {   
		       hex = '0' + hex;   
		     }   
		     System.out.print(hex.toUpperCase() );   
		   }   
		   
		   System.out.println();
		  
	}
	
    /** 
     * 将byte[]转为各种进制的字符串 
     * @param bytes byte[] 
     * @param radix 基数可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制 
     * @return 转换后的字符串 
     */  
    public static String binary(byte[] bytes, int radix){  
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数  
    } 
	
	
}


