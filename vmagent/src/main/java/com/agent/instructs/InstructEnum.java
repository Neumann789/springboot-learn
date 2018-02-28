package com.agent.instructs;

public enum InstructEnum {
	
	LS("ls","列出相关的包类名",new LsInstruct()),
	HELP("help","显示相关命令",new HelpInstruct()),
	GET("get","获取类的信息",new GetInstruct());
	
/*	private InstructEnum(String instructName, String instructRemark) {
		this.instructName = instructName;
		this.instructRemark = instructRemark;
	}*/
	
	private InstructEnum(String instructName, String instructRemark,AbsInstruct instruct) {
		this.instructName = instructName;
		this.instructRemark = instructRemark;
		this.instruct = instruct;
	}
	
	private String instructName;
	
	private String instructRemark;
	
	private AbsInstruct instruct;

	public String getInstructName() {
		return instructName;
	}

	public void setInstructName(String instructName) {
		this.instructName = instructName;
	}

	public String getInstructRemark() {
		return instructRemark;
	}

	public void setInstructRemark(String instructRemark) {
		this.instructRemark = instructRemark;
	}
	
	public AbsInstruct getInstruct() {
		return instruct;
	}

	public void setInstruct(AbsInstruct instruct) {
		this.instruct = instruct;
	}

	/**
	 * 
	 * findInstructEnum:获取指令枚举. <br/>
	 *
	 * @param instruct
	 * @return
	 */
	public static InstructEnum findInstructEnum(String instruct){
		
		InstructEnum[] instructs = InstructEnum.values();
		
		for(InstructEnum instructEnum:instructs){
			
			if(instructEnum.getInstructName().equals(instruct)){
				return instructEnum;
			}
			
			
		}
		
		return null;
	}
	

}
