/* Terminal - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.terminal;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Terminal
{
    public void init(InputStream inputstream, OutputStream outputstream,
		     OutputStream outputstream_0_);
    
    public int[] read(boolean bool) throws IOException;
    
    public void writeToStdOut(String string) throws IOException;
    
    public void writeToStdOut(char[] cs) throws IOException;
    
    public void writeToStdOut(char c) throws IOException;
    
    public void writeToStdErr(String string) throws IOException;
    
    public void writeToStdErr(char[] cs) throws IOException;
    
    public void writeToStdErr(char c) throws IOException;
    
    public int getHeight();
    
    public int getWidth();
    
    public boolean isEchoEnabled();
    
    public void reset() throws IOException;
}
