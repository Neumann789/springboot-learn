/* CLibrary - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.jansi.internal;
import org.fusesource.hawtjni.runtime.Library;

public class CLibrary
{
    private static final Library LIBRARY
	= new Library("jansi", CLibrary.class);
    public static int STDIN_FILENO;
    public static int STDOUT_FILENO;
    public static int STDERR_FILENO;
    public static boolean HAVE_ISATTY;
    
    private static final native void init();
    
    public static final native int isatty(int i);
    
    static {
	LIBRARY.load();
	init();
    }
}
