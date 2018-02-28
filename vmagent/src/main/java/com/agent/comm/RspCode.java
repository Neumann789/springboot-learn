package com.agent.comm;

/**
 * 
 * ClassName: RspCode <br/>
 * Function: 此类通用返回码,各模块的返回码，各自定义. <br/>
 * Date: 2017年9月14日 下午12:00:16 <br/>
 * 
 * 0000 处理成功
 * 9999 系统异常
 * 以V开头 代表校验类响应码
 * 以S开头 代表系统类响应码
 * 以B开头 代表业务类响应码
 * 
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public enum RspCode {
	
	
	/** 处理成功 0000 */
	SUCCESS("0000","处理成功"),
	
	/** 系统异常 9999 */
	ERROR("9999","系统异常"),
	
	/** 处理中 */
	IN_HANDLE("3T05","处理中"),
	
	INSTRUCT_MSG_FORMAT_ERR("F001","instrut format is err"),
	
	INSTRUCT_UNSUPPORT("F002","%s,this instruct not supported !"),
	
	
	

	;
	
	/** 返回码 */
	private String rspCode;

	/** 返回码描述 */
	private String rspMsg;
	
	
	RspCode (){}
	
	RspCode(String rspCode,String rspMsg){
	
			this.rspCode = rspCode;
			
			this.rspMsg = rspMsg;
	}

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspMsg() {
		return rspMsg;
	}

	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}
	
	public String getRspMsg(Object... args) {
		return String.format(this.rspMsg, args);
	}
	
}
