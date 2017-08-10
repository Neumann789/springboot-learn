package org.jboss.jreadline.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface Terminal
{
  public abstract void init(InputStream paramInputStream, OutputStream paramOutputStream1, OutputStream paramOutputStream2);
  
  public abstract int[] read(boolean paramBoolean)
    throws IOException;
  
  public abstract void writeToStdOut(String paramString)
    throws IOException;
  
  public abstract void writeToStdOut(char[] paramArrayOfChar)
    throws IOException;
  
  public abstract void writeToStdOut(char paramChar)
    throws IOException;
  
  public abstract void writeToStdErr(String paramString)
    throws IOException;
  
  public abstract void writeToStdErr(char[] paramArrayOfChar)
    throws IOException;
  
  public abstract void writeToStdErr(char paramChar)
    throws IOException;
  
  public abstract int getHeight();
  
  public abstract int getWidth();
  
  public abstract boolean isEchoEnabled();
  
  public abstract void reset()
    throws IOException;
}


