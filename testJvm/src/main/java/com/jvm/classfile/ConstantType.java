package com.jvm.classfile;

public enum ConstantType {
	
	CONSTANT_Utf8("CONSTANT_Utf8","1"),//UTF-8编码的Unicode字符串
	
	CONSTANT_Integer("CONSTANT_Integer","3"),//int类型字面值
	
	CONSTANT_Float("CONSTANT_Float","4"),//float类型字面值
	
	CONSTANT_Long("CONSTANT_Long","5"),//long类型字面值
	
	CONSTANT_Double("CONSTANT_Double","6"),//double类型字面值
	
	CONSTANT_Class("CONSTANT_Class","7"),//对一个类或接口的符号引用
	
	CONSTANT_String("CONSTANT_String","8"),//String类型字面值
	
	CONSTANT_Fieldref("CONSTANT_Fieldref","9"),//对一个字段的符号引用
	
	CONSTANT_Methodref("CONSTANT_Methodref","10"),//对一个类中声明的方法的符号引用
	
	CONSTANT_InterfaceMethodref("CONSTANT_InterfaceMethodref","11"),//对一个接口中声明的方法的符号引用
	
	CONSTANT_NameAndType("CONSTANT_Methodref","12");//对一个字段或方法的部分符号引用
	
	
	
	
	
	
	private String constantName;
	
	private String tag;
	
	ConstantType(String constantName,String tag){
		
		this.constantName=constantName;
		
		this.tag = tag;
		
	}

	public String getConstantName() {
		return constantName;
	}

	public void setConstantName(String constantName) {
		this.constantName = constantName;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	
}
