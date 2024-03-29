package com.rainbow.comm.model;

public enum RspCode {
	
	VM_AGENT("vm01","添加代理类异常:%s"),
	VM_SCRPT_GEN("vm02","监控脚本落地异常:%s"),
	VM_SCRPT_READ("vm03","读取脚本文件异常:%s");
	
	
	
	
	
	
	
	
	
	
	
	
	private RspCode(String errorCode,String errorMsg) {
		
		this.errorCode = errorCode;
		
		this.errorMsg = errorMsg;
	}
	
	private String errorCode;
	
	private String errorMsg;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	

}
