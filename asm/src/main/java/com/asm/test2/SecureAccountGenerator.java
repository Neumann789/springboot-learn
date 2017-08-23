package com.asm.test2;

import java.io.IOException;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class SecureAccountGenerator { 
	 
    public static AccountGeneratorClassLoader classLoader = 
        new AccountGeneratorClassLoader(); 
     
    private static Class secureAccountClass; 
     
    public static Account generateSecureAccount() throws ClassFormatError, 
        InstantiationException, IllegalAccessException, IOException { 
        if (null == secureAccountClass) {            
            ClassReader cr = new ClassReader("com.asm.test2.Account"); 
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); 
            ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw);
            cr.accept(classAdapter, ClassReader.SKIP_DEBUG); 
            byte[] data = cw.toByteArray(); 
            secureAccountClass = classLoader.defineClassFromClassFile( 
               "com.asm.test2.Account$EnhancedByASM",data); 
        } 
        return (Account) secureAccountClass.newInstance(); 
    } 
     
     static class AccountGeneratorClassLoader extends ClassLoader {
        public Class defineClassFromClassFile(String className, 
            byte[] classFile) throws ClassFormatError { 
            return defineClass(className, classFile, 0, 
            classFile.length);
        } 
    } 
}
