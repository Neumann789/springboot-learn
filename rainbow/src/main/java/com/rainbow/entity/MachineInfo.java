package com.rainbow.entity;

import java.util.List;

/**
 * 
 * ClassName: MachineInfo <br/>
 * Function: 保存机器相关信息. <br/>
 * Date: 2017年8月7日 上午11:17:19 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class MachineInfo {
	
	private String ip;
	
	private String machineName;
	
	private boolean isAlive;
	
	private List<JvmInfo> jvmInfoList;
	

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public List<JvmInfo> getJvmInfoList() {
		return jvmInfoList;
	}

	public void setJvmInfoList(List<JvmInfo> jvmInfoList) {
		this.jvmInfoList = jvmInfoList;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	
	

}
