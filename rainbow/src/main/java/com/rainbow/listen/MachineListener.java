package com.rainbow.listen;

import com.rainbow.comm.util.LoggerUtil;
import com.rainbow.comm.util.VmUtil;

import sun.jvmstat.perfdata.monitor.protocol.local.LocalVmManager;

/**
 * 
 * ClassName: MachineListener <br/>
 * Function: 此监听器,是为了监听当前机器的所有java进程. <br/>
 * Date: 2017年8月5日 上午11:40:06 <br/>
 * 获取java进程的方式有很多种：
 * 1 ps -ef|grep java 或  tasklist |findstr "java"(win不行，比如eclispe)
 * 2 jps(使用jps，前提是java环境)
 * 3 编写java程序来查
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class MachineListener implements CommListener{

	@Override
	public void listern() {
		
		LoggerUtil.info(VmUtil.getVMPidInfoMap());
		
	}
	
	
	
	public static void main(String[] args) {
		LocalVmManager localVmManager=new LocalVmManager();
		System.out.println(localVmManager.activeVms());
	}

}
