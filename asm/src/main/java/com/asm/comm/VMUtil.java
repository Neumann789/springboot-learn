package com.asm.comm;

import com.sun.tools.attach.VirtualMachine;

/**
 * vm相关工具类
 * @author Administrator
 *
 */
public class VMUtil {

	public static void main(String[] args) throws Throwable {
		
/*		List<VirtualMachineDescriptor> list = VirtualMachine.list();
		
		for(VirtualMachineDescriptor vmd : list){
			
			System.out.println(vmd.displayName()+"========"+vmd.id());
			if(vmd.displayName().endsWith("VMUtil")){
				//
				//injectAgent(vmd.id(), "C:\\Users\\Administrator\\Desktop\\vmagent.jar");
				injectAgent(vmd.id(), "E:\\tech\\jvm\\javaagent\\test\\vmagent.jar");
			}
			
		}*/
		
		injectAgent("22472", "C:\\Users\\Administrator\\Desktop\\vmagent.jar");
		
		System.in.read();
		
	}
	
	
	
	public static void injectAgent(String vmId,String agentJarPath) throws Throwable{
		VirtualMachine virtualMachine = VirtualMachine.attach(vmId);
		virtualMachine.loadAgent(agentJarPath, "argument for agent");
		virtualMachine.detach();
	}
}
