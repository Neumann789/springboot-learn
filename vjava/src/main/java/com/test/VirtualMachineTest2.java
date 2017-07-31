package com.test;

import java.util.List;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class VirtualMachineTest2 {
	public static void main(String[] args) {
		
		List<VirtualMachineDescriptor> list = VirtualMachine.list();
		
		for(VirtualMachineDescriptor vmd:list){
			
			System.out.println(vmd.displayName());
			
		}
		
		
	}
}
