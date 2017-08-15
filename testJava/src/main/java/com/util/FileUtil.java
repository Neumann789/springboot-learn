package com.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

public class FileUtil {
	
	
	public static Enumeration<java.net.URL> searchFileFromApplication(String fileName){
		
		//String fileName="META-INF/spring.handlers";
        Enumeration<java.net.URL> urls=null;
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        try {

        if (classLoader != null) {
            urls = classLoader.getResources(fileName);
        } else {
            urls = ClassLoader.getSystemResources(fileName);
        }
        } catch (Exception e) {
			e.printStackTrace();
		}
        
/*        while(urls.hasMoreElements()){
        	System.out.println(urls.nextElement().toString());
        }*/
        
        return urls;
	}
	
	public static void loadUrl2File(java.net.URL url,File file){
		
		InputStream is=null;
		OutputStream os=null;
		try {
			
			is=url.openStream();
			
			os=new FileOutputStream(file);
			
			byte[] buf=new byte[1024];
			int len=0;
			while((len=is.read(buf))!=-1){
				os.write(buf, 0, len);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	/**
	 * 
	 * createFile:TODO(这里用一句话描述这个方法的作用). <br/>
	 *
	 * @param filePath: 要创建文件的全路径
	 */
	public static File createFile(String filePath){
		
		int i=filePath.lastIndexOf("/")!=-1?filePath.lastIndexOf("/"):filePath.lastIndexOf("\\");
		String dirPath=filePath.substring(0,i);
		File dirFile=new File(dirPath);
		if(!dirFile.exists()||!dirFile.isDirectory()){
			if(!dirFile.mkdir()){
				throw new RuntimeException("创建文件目录异常:"+dirPath);
			}
			
		}
		
		File newFile=new File(filePath);
		
		if(!newFile.exists()){
			try {
				boolean f=newFile.createNewFile();
				if(!f){
					throw new RuntimeException("创建文件异常："+filePath);
				}
			} catch (IOException e) {
				throw new RuntimeException("创建文件异常："+filePath, e);
			}
		}
		
		return newFile;
		
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
	
	public static void class2File(Class clazz,File file){
		OutputStream os=null;
		try {
			byte[] bytecode=getClassBytes(clazz);
			
			os=new FileOutputStream(file);
			
			os.write(bytecode);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	
	public static void main(String[] args) {
		/*String fileName="META-INF/spring.handlers";
		fileName="java/lang/String.class";
		Enumeration<java.net.URL> urls=searchFileFromApplication(fileName);
		
		while(urls.hasMoreElements()){
			URL url=urls.nextElement();
			loadUrl2File(url, createFile("d:/classes/String.class"));
	    	//System.out.println(url.toString());
	    }*/
		
		class2File(String.class, createFile("d:/classes/String.class"));
	}
}
