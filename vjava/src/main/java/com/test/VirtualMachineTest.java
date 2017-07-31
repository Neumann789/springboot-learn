package com.test;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
/**
 * 
 * http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/com/sun/tools/attach/VirtualMachine.html
 * 
 * @author fanghuabao
 *
 */
public class VirtualMachineTest {
	
	public static void main(String[] args) throws Throwable {
		String pid="14340";
        VirtualMachine vm = VirtualMachine.attach(pid);
        System.out.println(JSON.toJSONString(vm));
        System.out.println("==testLoadAgent(vm)==");
        testLoadAgent(vm);
        
	}
	
	/**
	 * loadAgent
	 * 成功在应用日志输出：
	 * [Ljava.util.HashMap$Entry;
		java.util.zip.ZipFile$ZipFileInputStream
		java.lang.reflect.Array
		sun.misc.URLClassPath$3
		java.io.OutputStream
	 * @param vm
	 * @throws Throwable 
	 * @throws AgentInitializationException 
	 * @throws AgentLoadException 
	 */
	public static void testLoadAgent(VirtualMachine vm) throws AgentLoadException, AgentInitializationException, Throwable{
		String agentPath="E:\\tech\\jvm\\javaagent\\test\\vmagent.jar";
		vm.loadAgent(agentPath);
	}
	
	

}
