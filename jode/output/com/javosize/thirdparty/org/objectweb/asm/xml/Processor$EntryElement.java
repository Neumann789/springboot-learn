/* Processor$EntryElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.io.IOException;
import java.io.OutputStream;

interface Processor$EntryElement
{
    public OutputStream openEntry(String string) throws IOException;
    
    public void closeEntry() throws IOException;
}
