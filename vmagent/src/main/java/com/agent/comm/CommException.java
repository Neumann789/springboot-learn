package com.agent.comm;

public class CommException extends RuntimeException{
	
	private static final long serialVersionUID = 5318227622381092310L;
	
	private String code;
	private String msg;
	private String status;
	
	
	public CommException() {
		
		super();
		
	}
	
	public CommException(String errorMsg) {
		
		super(errorMsg);
		this.code = RspCode.ERROR.getRspCode();
		this.msg = errorMsg;
		
	}
	
	public CommException(String errorMsg,Throwable t) {
		
		super(errorMsg, t);
		this.code = RspCode.ERROR.getRspCode();
		this.msg = errorMsg;
		
	}
	
	public CommException(String code, String msg,String status){
		super(code+"-"+msg+"-"+status);
		this.code = code;
		this.msg = msg;
		this.status=status;
	}
	
	public CommException(String code, String msg){
		super(code+"-"+msg);
		this.code = code;
		this.msg = msg;
	}
	

	public CommException(RspCode resp, Object... args){
		
		super(getCodeAndMsg(resp, args));
		
		this.code = resp.getRspCode();
		
		if(args.length!=0){
			
			this.msg = resp.getRspMsg(args);
			
		}else{
			
			this.msg = resp.getRspMsg().replaceAll("%s", "");
			
		}
		
	}
	
	public String getCode(){
		return this.code;
	}
	
	public String getStatus(){
		return this.status;
	}
	public String getMsg(){
		return this.msg;
	}
	
	public  String getCodeAndMsg(){
		return this.code+"-"+this.getMsg();
	}
	
	private static String getCodeAndMsg(RspCode resp, Object... args){
		
		String code = resp.getRspCode();
		
		String msg = null;
		
		if(args.length!=0){
			
			msg = resp.getRspMsg(args);
			
		}else{
			
			msg = resp.getRspMsg().replaceAll("%s", "");
			
		}
		return code+"-"+msg;
	}
	
	
}
