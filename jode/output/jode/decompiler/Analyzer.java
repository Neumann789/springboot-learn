/* Analyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.IOException;

public interface Analyzer
{
    public void analyze();
    
    public void dumpSource(TabbedPrintWriter tabbedprintwriter)
	throws IOException;
}
