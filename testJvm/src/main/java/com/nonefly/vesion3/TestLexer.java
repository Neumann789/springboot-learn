package com.nonefly.vesion3;
/**
 * 词法分析
 * 关键字，运算符一符一类  
 * 标识符，常数，分隔符各自一类
 * 运算符未处理组合运算 ++、--、+= 等
 * @author zhangyu
 * @version 3.0
 */
public class TestLexer extends TypeUtil{
	private StringBuffer buffer = new StringBuffer(); // 缓冲区
	private int i = 0;
	private char ch; // 字符变量，存放最新读进的源程序字符
	private String strToken; // 字符数组，存放构成单词符号的字符串
	
	public TestLexer() {
	}
	/**
	 * 读取指定路径文件
	 * @param fileSrc 读取文件路径
	 */
	public TestLexer(String fileSrc) {
		FileUtil.readFile(buffer, fileSrc);
	}

	/**
	 * 词法分析
	 */
	public void analyse() {
		strToken = ""; // 置strToken为空串
		FileUtil.clearFile();//清空文件
		while (i < buffer.length()) {
			getChar();
			getBC();
			if (isLetter(ch)) { // 如果ch为字母
				while (isLetter(ch) || isDigit(ch)) {
					concat();
					getChar();
				}
				retract(); // 回调
				if (isKeyWord(strToken)) { 
					writeFile(strToken,strToken);//strToken为关键字
				} else { 
					writeFile("id",strToken);//strToken为标识符
				}
				strToken = "";
			} else if (isDigit(ch)) { 
				while (isDigit(ch)) {//ch为数字
					concat();
					getChar();
				}
				if(!isLetter(ch)){//不能数字+字母
					retract(); // 回调
					writeFile("digit",strToken); // 是整形
				}else writeFile("error",strToken); // 非法
				strToken = "";
			} else if (isOperator(ch)) { //运算符
				if(ch == '/'){
					getChar();
					if(ch == '*') {//为/*注释
						while(true){
							getChar();
							if(ch == '*'){// 为多行注释结束
								getChar();
								if(ch == '/') {
									getChar();
									break;
								}
							}
						}
					}
					if(ch == '/'){//为//单行注释
						while(ch != 9){
							//System.out.println(ch+"   "+(int)ch);
							getChar();
						}
					}
					retract();
				}
				//System.out.println(ch+"   "+(int)ch);
				switch (ch) {
					case '+': writeFile("plus",ch+""); break;
					case '-': writeFile("min",ch+""); break;
					case '*': writeFile("mul",ch+""); break;
					case '/': writeFile("div",ch+""); break;
					case '>': writeFile("gt",ch+""); break;
					case '<': writeFile("lt",ch+""); break;
					case '=': writeFile("eq",ch+""); break;
					case '&': writeFile("and",ch+""); break;
					case '|': writeFile("or",ch+""); break;
					case '~': writeFile("not",ch+""); break;
					default:  break;
				}
			} else if (isSeparators(ch)) { // 界符
				writeFile("separators",ch+"");
			} else writeFile("error",ch+"");
		}
	}

	/**
	 * 将下一个输入字符读到ch中，搜索指示器前移一个字符
	 */
	public void getChar() {
		ch = buffer.charAt(i);
		i++;
	}
	/** 检查ch中的字符是否为空白，若是则调用getChar()直至ch中进入一个非空白字符*/
	public void getBC() {
		//isSpaceChar(char ch) 确定指定字符是否为 Unicode 空白字符。
		//上述方法不能识别换行符
		while (Character.isWhitespace(ch))//确定指定字符依据 Java 标准是否为空白字符。
			getChar();
	}

	/**将ch连接到strToken之后*/
	public void concat() {
		strToken += ch;
	}
	/** 将搜索指示器回调一个字符位置，将ch值为空白字 */
	public void retract() {
		i--;
		ch = ' ';
	}
	/**
	 * 按照二元式规则写入文件
	 * @param file 字符类型
	 * @param s	当前字符
	 */
	public void writeFile(String file,String s) {
		int temp = getType(file.toUpperCase());
		System.out.println("("+file+", "+s+")");
		file = "("+temp+", "+s+")"+"\r\n";
		FileUtil.writeFile(file);
	}
}