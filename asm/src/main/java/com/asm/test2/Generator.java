package com.asm.test2;

import java.io.File;
import java.io.FileOutputStream;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * 此类的作用：会在修改Account.class中的operation()方法
 * @author fanghuabao
 * 执行完此类前Account类反编译结果:
 * public class Account {
	
	public void operation(){
		
		System.out.println("Account.operation()");
		
	}

}
 *执行完此类后Account类反编译结果:
 *public class Account
{
  public void operation()
  {
    SecurityChecker.checkSecurity();
    System.out.println("Account.operation()");
  }
}
 *
 */
public class Generator{ 
    public static void main(String[] args) throws Exception { 
        ClassReader cr = new ClassReader("com.asm.test.Account"); 
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); 
        ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw); 
        cr.accept(classAdapter, ClassReader.SKIP_DEBUG); 
        byte[] data = cw.toByteArray(); 
        File file = new File("F:\\git20170716\\springboot-learn\\asm\\target\\classes\\com\\asm\\test\\Account.class"); 
        FileOutputStream fout = new FileOutputStream(file); 
        fout.write(data); 
        fout.close(); 
    } 
}
