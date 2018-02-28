package com.sun.tools.javac;

/**
 * 
 * ClassName: JavacTest <br/>
 * Function: javac测试类. <br/>
 * Date: 2018年2月11日 下午4:17:04 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class JavacTest {
	public static void main(String[] args) throws Exception {
		
		args = new String[5];
		args[0] = "E:/git/20180213/springboot-learn/testJavac/src/test/java/com/sun/tools/javac/Hello.java";
		args[1] = "-verbose";
		args[2] = "-verbose";
		args[3] = "-verbose";
		args[4] = "-verbose";
		Main.main(args);
		
		
	}
}
