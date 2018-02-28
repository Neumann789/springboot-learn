package com.agent.instructs;

import com.agent.comm.CommException;
import com.agent.comm.RspCode;

/**
 * 
 * ClassName: InstructManager <br/>
 * Function: 指令管理器. <br/>
 * Date: 2018年2月28日 下午4:19:57 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class InstructManager {
	
	/**
	 * 
	 * checkInstructMsg:检查指令格式. <br/>
	 *
	 * @param instructMsg
	 */
	public static String handleInstructMsg(String instructMsg){
		
		String returnMsg = "";
		
		try {
			
			String[] splitInstructs = instructMsg.split("\\s+");
			
			if(splitInstructs.length<1){
				
				throw new CommException(RspCode.INSTRUCT_MSG_FORMAT_ERR);
				
			}
			
			InstructEnum instructEnum = InstructEnum.findInstructEnum(splitInstructs[0]);
			
			if(instructEnum == null){
				
				throw new CommException(RspCode.INSTRUCT_UNSUPPORT.getRspCode(),RspCode.INSTRUCT_UNSUPPORT.getRspMsg(splitInstructs[0]));
			
			}
			
			AbsInstruct instruct = instructEnum.getInstruct();
			
			instruct.checkFormat(splitInstructs);
			
			returnMsg = instruct.excute(splitInstructs);
		
		
		} catch (CommException e) {
			
			returnMsg = e.getCodeAndMsg();
			
		} catch (Exception e) {
			
			returnMsg = e.getMessage();
			
		}
		
		return returnMsg;
		
	}
	
	public static void main(String[] args) {
		String lsMsg = "ls    /hello  /uuuu";
		String[] msgs = lsMsg.split("\\s+");
		for(String m:msgs){
			System.out.println(m);
		}
	}

}
