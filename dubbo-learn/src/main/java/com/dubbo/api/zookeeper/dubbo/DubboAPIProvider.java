package com.dubbo.api.zookeeper.dubbo;
import java.io.IOException;
import java.io.Serializable;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

/**
 * 
 * ClassName: DubboAPIProvider <br/>
 * Function: 注册dubbo服务. <br/>
 * Date: 2017年8月10日 下午3:59:13 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class DubboAPIProvider {
	public static void main(String[] args) throws Throwable {

		startProvider(true);
		
	}
	
	public static void startProvider(boolean isWait) throws Throwable{
		//服务实现
		TestService testService = new TestServiceImpl();
		
		providerExport(TestService.class,testService,28080);
		
		TradeService tradeService=new TradeServiceImpl();
		
		providerExport(TradeService.class, tradeService,28081);
		
		if(isWait){
			System.in.read();
		}
	}

	public static <T> void providerExport(Class inter,T t,int port) throws IOException {
		
		//当前应用位置
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName("TEST-PROVIDER");
		
		//连接注册中心配置
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setProtocol("zookeeper");
		registryConfig.setAddress("192.168.0.65:2181");
		registryConfig.setUsername("aa");//???
		registryConfig.setPassword("bb");//???
		
		//服务提供者协议配置
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName("dubbo");
		//protocolConfig.setName("http");
		protocolConfig.setPort(port);
		protocolConfig.setThreads(200);
		
		//服务提供者暴露服务配置
		//注意：ServiceConfig为重对象,内部封装了与注册中心的连接,以及开启服务端口
		ServiceConfig<T> serviceConfig = new ServiceConfig<>();
		serviceConfig.setApplication(applicationConfig);
		serviceConfig.setRegistry(registryConfig);
		serviceConfig.setProtocol(protocolConfig);
		serviceConfig.setInterface(inter);
		serviceConfig.setRef(t);
		serviceConfig.setVersion("1.0.0");
		
		serviceConfig.export();

		
	
	}
}






 interface TestService {
	
	public void test();
	
	public Response sayHello(Request req);
	
	public Response sayHello2(Request req) throws TestException;
	

}
 
 interface TradeService{
	 
	 public void recharge();
	 
	 public void withdraw();
	 
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
	
}

class TradeServiceImpl implements TradeService{

	@Override
	public void recharge() {
		System.out.println("TradeServiceImpl.recharge()");
	}

	@Override
	public void withdraw() {
		System.out.println("TradeServiceImpl.withdraw()");
	}
	
	
}



