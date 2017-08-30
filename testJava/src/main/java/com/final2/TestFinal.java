package com.final2;

public class TestFinal {
	
	public static void main(String[] args) {
		
		A a = new A("tom", "usa");
		
		a.setAddress("china");
		
	}

}


final class A{
	
	private String name;
	
	private String address;

	public A(String name,String address) {
		
		this.name=name;
		this.address=address;
		
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
}

