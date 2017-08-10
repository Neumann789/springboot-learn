 package com.javosize.compiler;
 
 import com.javosize.log.Log;
 import com.javosize.thirdparty.org.objectweb.asm.ClassReader;
 import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
 import com.javosize.thirdparty.org.objectweb.asm.ClassWriter;
 import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
 import java.io.ByteArrayInputStream;
 
 
 public class MethodNameModifier
   extends ClassVisitor
 {
   private static Log log = new Log(MethodNameModifier.class.getName());
   
   public MethodNameModifier(int api, ClassWriter cv) {
     super(api, cv);
   }
   
 
 
 
   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
   {
     String[] nameSplit = name.split("\\$");
     if ((name.startsWith("lambda")) && (nameSplit.length == 3)) {
       log.debug("Replacing method name " + name + " by " + nameSplit[0] + "$" + nameSplit[2]);
       name = nameSplit[0] + "$" + nameSplit[2];
     }
     
     MethodVisitor methodVisitor = this.cv.visitMethod(access, name, desc, signature, exceptions);
     return methodVisitor;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static byte[] refactorLambdaExpressions(byte[] bytecode)
     throws Exception
   {
     ByteArrayInputStream in = new ByteArrayInputStream(bytecode);
     ClassReader classReader = new ClassReader(in);
     ClassWriter cw = new ClassWriter(1);
     
 
     MethodNameModifier mcw = new MethodNameModifier(262144, cw);
     classReader.accept(mcw, 0);
     
 
     return cw.toByteArray();
   }
 }


