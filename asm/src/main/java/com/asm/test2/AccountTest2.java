package com.asm.test2;

/**
 * 将动态生成类改造成原始类 Account 的子类
 * @author fanghuabao
 *执行结果如下：
 *com.asm.test2.Account$EnhancedByASM@372a6e85
SecurityChecker.checkSecurity
Account.operation()
 */
public class AccountTest2 {
	
	public static void main(String[] args) throws Throwable {
		Account acc=SecureAccountGenerator.generateSecureAccount();
		
		Class clazz=SecureAccountGenerator.classLoader.loadClass("com.asm.test2.Account$EnhancedByASM");
		System.out.println("clazz==>> "+clazz);
		System.out.println(acc);
		acc.operation();
	}

}
