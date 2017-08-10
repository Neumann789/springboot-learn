/* StreamRedirectThread - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.decompile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

public class StreamRedirectThread extends Thread
{
    private final Reader in;
    private final Writer out;
    private Exception ex;
    
    public StreamRedirectThread(String name, InputStream in, Writer out) {
	super(name);
	this.in = new InputStreamReader(in);
	this.out = out;
	setPriority(9);
    }
    
    public void run() {
	try {
	    char[] cbuf = new char[2048];
	    int count;
	    while ((count = in.read(cbuf, 0, 2048)) >= 0) {
		out.write(cbuf, 0, count);
		out.flush();
	    }
	} catch (IOException exc) {
	    ex = exc;
	}
    }
    
    public Exception getException() {
	return ex;
    }
}
