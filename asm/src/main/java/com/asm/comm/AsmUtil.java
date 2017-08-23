package com.asm.comm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.asm.test2.Account;


public class AsmUtil {
	
	public static final String SUFFIX_CLASS=".class";
	
	public static void createClassFile(Class clazz) throws Throwable{
		
        ClassReader cr = new ClassReader(clazz.getName()); 
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); 
        cr.accept(cw, ClassReader.SKIP_DEBUG); 
        byte[] data = cw.toByteArray(); 
        File file = new File("F:\\Account.class"); 
        if(!file.exists()){
        	file.createNewFile();
        }
        FileOutputStream fout = new FileOutputStream(file); 
        fout.write(data); 
        fout.close(); 
		
	}
	
	public static void getClassFile(Class clazz,String classFileRootDir) throws Throwable{
		
        ClassReader cr = new ClassReader(clazz.getName()); 
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); 
        cr.accept(cw, ClassReader.SKIP_DEBUG); 
        byte[] data = cw.toByteArray(); 
        
        classFileRootDir=classFileRootDir.endsWith("/")?classFileRootDir:classFileRootDir+"/";
        String classPath=clazz.getName().replace(".", "/");
        String classFilePath=classFileRootDir+classPath+SUFFIX_CLASS;
        File file = FileUtil.createFile(classFilePath);
        FileOutputStream fout = new FileOutputStream(file); 
        fout.write(data); 
        fout.close(); 
		
	}
	
	public static byte[] class2ByteCode(Class clazz) throws IOException {
		
        ClassReader cr = new ClassReader(clazz.getName()); 
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); 
        cr.accept(cw, ClassReader.SKIP_DEBUG); 
        return  cw.toByteArray(); 
	}
	
	public static ClassLoader listClassLoaders(){
		
		return null;
	}
	
	public static void main(String[] args) throws Throwable {
		/*System.out.println(Account.class.getName());
		createClassFile(Account.class);
		*/
		
		getClassFile(Account.class, "F:/TEST2");
		System.out.println("excute success!");
	}

}
