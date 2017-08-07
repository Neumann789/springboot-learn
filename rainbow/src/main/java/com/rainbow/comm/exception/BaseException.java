package com.rainbow.comm.exception;

import com.rainbow.comm.model.RspCode;

public class BaseException extends RuntimeException{
	
	private static final long serialVersionUID = 8143444835211041143L;

	private String errorCode;
	
	private String errorMsg;
	
	public BaseException() {
		super();
	}
	
	public BaseException(String msg) {
		super(msg);
		this.errorMsg = msg;
	}
	
	public BaseException(String errorCode,String errorMsg){
		super(errorCode+errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public BaseException(RspCode rspCode){
		
		super(rspCode.getErrorCode()+rspCode.getErrorMsg());
		
	}
	
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

	@Override
	public String toString() {
		return "BaseException [errorCode=" + errorCode + ", errorMsg=" + errorMsg + "]";
	}
	
	
	
}
