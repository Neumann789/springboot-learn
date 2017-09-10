package com.dubbo.xml.zookeeper.dubbo;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

interface TestService {
	
	public void test();
	
	public Response sayHello(Request req);
	
	public Response sayHello2(Request req) throws TestException;
	
	public Response sendMsg(Request req,Message msg) throws TestException;
	
	
	
	

}

class Message implements Serializable{
	
	private static final long serialVersionUID = 8643051095643805373L;

	private String msgId;
	
	private int msgLen;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getMsgLen() {
		return msgLen;
	}

	public void setMsgLen(int msgLen) {
		this.msgLen = msgLen;
	}
	
	
	
	
	
}

class Request implements Serializable{
	
	private static final long serialVersionUID = -5700230626691763435L;

	private String name;
	
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
}

class Response implements Serializable{
	
	private static final long serialVersionUID = 3614392381847349150L;

	private String code;
	
	private String msg;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	
}


class TestException extends Exception{
	
	private static final long serialVersionUID = -1125698460533706720L;

	private String errorCode;
	
	private String errorMsg;
	
	public TestException() {
		super();
	}
	
	
	public TestException(String errorCode,String errorMsg) {
		super(errorCode+"--"+errorMsg);
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
	
	
}

class TestServiceImpl implements TestService{

	@Override
	public void test() {
		System.out.println("TestService.test()");
	}

	@Override
	public Response sayHello(Request req) {
		System.out.println("Request:"+req.getAge()+":"+req.getName());
		Response rsp = new Response();
		rsp.setCode("0000");
		rsp.setMsg("success");
		return rsp;
	}

	@Override
	public Response sayHello2(Request req) throws TestException {
		System.out.println("Request:"+req.getAge()+":"+req.getName());
		Response rsp = new Response();
		rsp.setCode("9999");
		rsp.setMsg("失败");
		if("9999".equals(rsp.getCode())){
			throw new TestException("9999", "失败");
		}
		return rsp;
	}

	@Override
	public Response sendMsg(Request req, Message msg) throws TestException {
		System.out.println("Request:"+JSON.toJSONString(req)+" Message:"+JSON.toJSONString(msg));
		Response rsp = new Response();
		rsp.setCode("0000");
		rsp.setMsg("成功");
		return rsp;
	}    
	
}




