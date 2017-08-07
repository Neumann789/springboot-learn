package com.rainbow.comm.exception;

import com.rainbow.comm.model.RspCode;

public class VmException extends BaseException{
	
	private static final long serialVersionUID = -8668098144796779368L;
	
	public VmException(RspCode rspCode,String ...appends) {
		super(rspCode.getErrorCode(), String.format(rspCode.getErrorMsg(), appends));
	}
	
	public static void main(String[] args) {
		
		System.out.println(new VmException(RspCode.VM_AGENT, "helo "));
		
	}

}
