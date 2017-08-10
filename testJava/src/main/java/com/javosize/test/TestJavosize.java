package com.javosize.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.javosize.cli.Main;
import com.javosize.decompile.DecompilationResult;
import com.javosize.decompile.Decompiler;
import com.javosize.encoding.Base64;

public class TestJavosize {
	public static void main(String[] args) throws Exception {
		test1();
	}
	
	
	public static void test1() throws Exception{
		
		byte[] byteCode = getClassBytes(TestJavosize.class);
/*		String s = Base64.encodeBytesToString(byteCode);
		byte[] buf=Base64.decodeBytesFromString(s);*/
		
		Map byteCodeOfClasses = new LinkedHashMap();
		byteCodeOfClasses.put(TestJavosize.class.getName(), byteCode);
		
		String code = Decompiler.decompile(TestJavosize.class.getName(), byteCodeOfClasses);
		
		 System.out.println(code);
		
	}

	private static void test0() throws Exception {
		String[] args;
		args=new String[1];
		args[0]="5524";
		Main.main(args);
	}
	
	public static byte[] getClassBytes(Class clazz){
		 String name = new StringBuilder().append(clazz.getName().replace('.', '/')).append(".class").toString();
		    ClassLoader cl = clazz.getClassLoader();
		    if (cl == null) {
		      cl = ClassLoader.getSystemClassLoader();
		    }
		    InputStream iStream = cl.getResourceAsStream(name);
		    try
		    {
		      ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		      byte[] buff = new byte[1024];
		      int len = 0;
		      while ((len = iStream.read(buff)) != -1) {
		        oStream.write(buff, 0, len);
		      }

		      return oStream.toByteArray();
		    }
		    catch (Exception e)
		    {
		      return null;
		    } finally {
		    	try {
					iStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }

	}
}
