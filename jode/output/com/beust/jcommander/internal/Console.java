/* Console - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.internal;

public interface Console
{
    public void print(String string);
    
    public void println(String string);
    
    public char[] readPassword(boolean bool);
}
