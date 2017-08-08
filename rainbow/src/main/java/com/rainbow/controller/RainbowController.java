package com.rainbow.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rainbow.comm.exception.BaseException;
import com.rainbow.comm.model.RspCode;
import com.rainbow.comm.util.BtraceUtil;
import com.rainbow.comm.util.DateUtil;
import com.rainbow.comm.util.VmUtil;
import com.rainbow.entity.MachineInfo;

@RestController
@RequestMapping("/rainbow")
@SuppressWarnings("all")
public class RainbowController {
	
	@RequestMapping(value="/heartBeat")
	public String heartBeat(){
		return "hello,I am rainbow";
	}
	
	@RequestMapping(value="/machineInfo")
	public MachineInfo machineInfo(){
		
		MachineInfo machineInfo=VmUtil.getMachineInfo();
		
		machineInfo.setJvmInfoList(VmUtil.getJvmInfoList());
		
		return machineInfo;
	}
	
	@RequestMapping(value="/pushScript")
	public Map pushScript(Map map){
		String pid=(String)map.get("pid");
		String script=(String)map.get("script");
		//生成脚本文件，返回脚本地址
		String scriptPath=genScriptFile(script);
		BtraceUtil.injectJVMScript(pid, scriptPath);
		return null;
	}
	
	
	public static String genScriptFile(String scriptContext){
		
		String scriptFilePath="";

		PrintWriter pw = null;
		
		try {
			
			File scriptFile = new File("SCRPT"+DateUtil.getTimeStamp());
			
			pw = new PrintWriter(new FileWriter(scriptFile), true);
			
			pw.print(scriptContext);
			
			scriptFilePath=scriptFile.getAbsolutePath();
			
		} catch (Exception e) {
			
			throw new BaseException(RspCode.VM_SCRPT_GEN);
			
		}finally {
			
			pw.close();
			
		}
		
		return scriptFilePath;
		
	}
}
