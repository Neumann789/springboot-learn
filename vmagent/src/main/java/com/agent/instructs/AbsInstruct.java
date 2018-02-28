package com.agent.instructs;

/**
 * 
 * ClassName: AbsInstruct <br/>
 * Function: TODO (描述这个类的作用). <br/>
 * Date: 2018年2月28日 下午5:22:49 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public abstract class AbsInstruct {
	
	/**
	 * 
	 * checkFormat:检查指令格式. <br/>
	 *
	 * @param splitInstructs
	 */
	public abstract void checkFormat(String[] splitInstructs);
	
	/**
	 * 
	 * excute:执行指令. <br/>
	 *
	 * @param splitInstructs
	 */
	public abstract String excute(String[] splitInstructs);
	
}
