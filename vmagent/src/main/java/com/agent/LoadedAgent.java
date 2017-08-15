package com.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class LoadedAgent {
    public static void agentmain(String args, Instrumentation inst){
    	execute(args, inst);
    }
    public static void premain(String args, Instrumentation inst){
    	execute(args, inst);
    }
    
    public static void execute(String args, Instrumentation inst){
    	
       /* Class[] classes = inst.getAllLoadedClasses();
        for(Class cls :classes){
            System.out.println(cls.getName());
        }*/
    	
    	inst.addTransformer(new ClassFileTransformer() {
			
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
					ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
				
				System.out.println("load Class:"+className);
				
				try {
					//String classPath=className+".class";
					//FileUtil.class2File(LoadedAgent.class.getClassLoader(), classPath,FileUtil.createFile("d:/classes/"+className+".class"));
					if(className.endsWith("roxy0")){
						FileUtil.class2File(classfileBuffer, FileUtil.createFile("d:/classes/"+className+".class"));
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				return classfileBuffer;
			}
		});
        
    }
}
