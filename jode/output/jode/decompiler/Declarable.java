/* Declarable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.IOException;

public interface Declarable
{
    public String getName();
    
    public void makeNameUnique();
    
    public void dumpDeclaration(TabbedPrintWriter tabbedprintwriter)
	throws IOException;
}
