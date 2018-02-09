package 
com.jvm.javac;

import com.sun.tools.javac.Main;

/**
 * TODO
 * ClassName: TestJavac <br/>
 * Function: TODO (描述这个类的作用). <br/>
 * Date: 2018年2月8日 下午3:45:59 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class TestJavac {
	
	public static void main(String[] args) throws Throwable {
		args = new String[5];
		args[0] = "F:\\git20170716\\springboot-learn\\testJvm\\src\\main\\java\\com\\jvm\\javac\\Hello.java";
		args[1] = "-verbose";
		args[2] = "-verbose";
		args[3] = "-verbose";
		args[4] = "-verbose";
		Main.main(args);
	}

}
