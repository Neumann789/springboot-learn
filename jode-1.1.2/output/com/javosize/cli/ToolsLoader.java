/* ToolsLoader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli;

public class ToolsLoader
{
    public static void main(String[] args) {
	String javaHome = System.getProperty("java.home");
	System.out.println(new StringBuilder().append("Java home: ").append
			       (javaHome).toString());
    }
}
