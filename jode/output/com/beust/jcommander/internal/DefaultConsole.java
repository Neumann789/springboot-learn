/* DefaultConsole - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.internal;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.beust.jcommander.ParameterException;

public class DefaultConsole implements Console
{
    public void print(String msg) {
	System.out.print(msg);
    }
    
    public void println(String msg) {
	System.out.println(msg);
    }
    
    public char[] readPassword(boolean echoInput) {
	try {
	    InputStreamReader isr = new InputStreamReader(System.in);
	    BufferedReader in = new BufferedReader(isr);
	    String result = in.readLine();
	    in.close();
	    isr.close();
	    return result.toCharArray();
	} catch (java.io.IOException PUSH) {
	    java.io.IOException e = POP;
	    throw new ParameterException(e);
	}
    }
}
