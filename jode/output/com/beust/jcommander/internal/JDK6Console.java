/* JDK6Console - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.internal;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import com.beust.jcommander.ParameterException;

public class JDK6Console implements Console
{
    private Object console;
    private PrintWriter writer;
    
    public JDK6Console(Object console) throws Exception {
	this.console = console;
	Method writerMethod
	    = console.getClass().getDeclaredMethod("writer", new Class[0]);
	writer = (PrintWriter) writerMethod.invoke(console, new Object[0]);
    }
    
    public void print(String msg) {
	writer.print(msg);
    }
    
    public void println(String msg) {
	writer.println(msg);
    }
    
    public char[] readPassword(boolean echoInput) {
	try {
	    writer.flush();
	    if (!echoInput) {
		Method method
		    = console.getClass().getDeclaredMethod("readPassword",
							   new Class[0]);
		return (char[]) method.invoke(console, new Object[0]);
	    }
	    Method method = console.getClass().getDeclaredMethod("readLine",
								 new Class[0]);
	    return ((String) method.invoke(console, new Object[0]))
		       .toCharArray();
	} catch (Exception PUSH) {
	    Exception e = POP;
	    throw new ParameterException(e);
	}
    }
}
