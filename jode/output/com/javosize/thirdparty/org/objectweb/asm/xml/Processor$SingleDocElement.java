/* Processor$SingleDocElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.io.IOException;
import java.io.OutputStream;

final class Processor$SingleDocElement implements Processor$EntryElement
{
    private final OutputStream os;
    
    Processor$SingleDocElement(OutputStream outputstream) {
	os = outputstream;
    }
    
    public OutputStream openEntry(String string) throws IOException {
	return os;
    }
    
    public void closeEntry() throws IOException {
	os.flush();
    }
}
