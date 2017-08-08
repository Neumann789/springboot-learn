package com.rainbow.comm.util;

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rainbow.comm.exception.VmException;
import com.rainbow.comm.model.RspCode;
import com.rainbow.entity.JvmInfo;
import com.rainbow.entity.MachineInfo;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import sun.jvmstat.perfdata.monitor.protocol.local.LocalVmManager;

/**
 * 
 * ClassName: VmUtil <br/>
 * Function: 提供机器相关功能. <br/>
 * Date: 2017年8月5日 下午3:48:53 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class VmUtil {
	
	/**
	 * 
	 * getJavaPids:获取当前机器的所有java进程号. <br/>
	 *
	 * @return
	 */
	public static Set<Integer> getJavaPids(){
		LocalVmManager localVmManager=new LocalVmManager();
		return localVmManager.activeVms();
	}
	
	/**
	 * 
	 * getVMDescList:获取当前机器的所有java虚拟机信息. <br/>
	 *
	 * @return
	 */
	public static List<VirtualMachineDescriptor> getVMDescList(){
		return VirtualMachine.list();
	}
	
	/**
	 * 
	 * getVMPidInfoMap:获取当前机器所有java进程的进程号和进程名. <br/>
	 *
	 * @return
	 */
	public static Map<String,String> getVMPidInfoMap(){
		
		List<VirtualMachineDescriptor> list = getVMDescList();
		
		Map<String,String> pidInfoMap = new HashMap<>();
		
		for(VirtualMachineDescriptor vmd:list){
			
			String dispName=vmd.displayName();
			
			if(dispName==null||dispName.length()==0){
				dispName = "undisplay";
			}
			
			pidInfoMap.put(vmd.id(), dispName);
			
		}
		
		return pidInfoMap;
		
	}
	
	public static List<JvmInfo> getJvmInfoList(){
		
		List<VirtualMachineDescriptor> list = getVMDescList();
		
		List<JvmInfo> jvmInfoList=new ArrayList<>();
		
		for(VirtualMachineDescriptor vmd:list){
			
			JvmInfo jvmInfo = new JvmInfo();
			
			String dispName=vmd.displayName();
			
			if(dispName==null||dispName.length()==0){
				dispName = "undisplay";
			}
			
			jvmInfo.setPid(vmd.id());
			try {
				jvmInfo.setJvmName(new String(dispName.getBytes("gbk"), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				jvmInfo.setJvmName(dispName);
			}
			jvmInfoList.add(jvmInfo);
			
		}
		
		return jvmInfoList;
		
	}
	
	/**
	 * 
	 * getCurrentPid:获取当前java进程号. <br/>
	 *
	 * @return
	 */
	public static String getCurrentPid(){
		
	        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
	        
	        return runtimeMXBean.getName().split("@")[0];

    } 
	
	/**
	 * 
	 * loadAgent:给指定进程添加agent. <br/>
	 *
	 * @param targetPid
	 * @param agentPath
	 */
	public static void loadAgent(String targetPid,String agentPath){
		
		try {
		
        VirtualMachine vm = VirtualMachine.attach(targetPid);
		vm.loadAgent(agentPath);
		
		} catch (Exception e) {
			throw new VmException(RspCode.VM_AGENT,e.getMessage());
		}
	}
	
	
	public static MachineInfo getMachineInfo(){
		
		MachineInfo machineInfo=new MachineInfo();
		InetAddress ia=null;
        try {
            ia=ia.getLocalHost();
            String machineName=ia.getHostName();
            String ip=ia.getHostAddress();
            machineInfo.setIp(ip);
            machineInfo.setMachineName(machineName);
            
        } catch (Exception e) {
        	LoggerUtil.error(e);
        }
        
        return machineInfo;
	}
	
	
	public static void main(String[] args) {
		
		/*List<VirtualMachineDescriptor> list = getVMDescList();
		13408
		for(VirtualMachineDescriptor vmd:list){
			System.out.println(vmd.id()+"=="+vmd.displayName());
		}*/
		
		//System.out.println(getVMPidInfoMap());
		
		//loadAgent("23236", "E:\\tech\\jvm\\javaagent\\test\\vmagent.jar");
		
        
		
	}
	
	
	
	
}
