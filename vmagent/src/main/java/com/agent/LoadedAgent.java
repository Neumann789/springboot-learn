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
				return classfileBuffer;
			}
		});
        
    }
}
