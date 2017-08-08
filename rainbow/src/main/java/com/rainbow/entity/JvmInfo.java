package com.rainbow.entity;

/**
 * 
 * ClassName: JvmInfo <br/>
 * Function: 保存JVM相关信息. <br/>
 * Date: 2017年8月7日 上午11:17:43 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class JvmInfo {
	
	private String pid;
	
	private String jvmName;
	
	public String getJvmName() {
		return jvmName;
	}

	public void setJvmName(String jvmName) {
		this.jvmName = jvmName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	
	
}
