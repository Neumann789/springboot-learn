package com.exception;

public class TestException {
	
	public static void main(String[] args) {
		
		testExc();
		
	}
	
	
	public static void testExc(){
		
		try {
			System.out.println(1/0);
		} catch (Exception e) {
			
			System.out.println(" catch (Exception e) ");
			int i=1/0;
			
		} catch (Throwable e) {
			
			System.out.println("catch (Exception e)");
			
		}
		
		System.out.println("end");
		
	}

}
