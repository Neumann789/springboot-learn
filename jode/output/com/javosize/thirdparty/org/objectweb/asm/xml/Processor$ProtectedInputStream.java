/* Processor$ProtectedInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.io.IOException;
import java.io.InputStream;

final class Processor$ProtectedInputStream extends InputStream
{
    private final InputStream is;
    
    Processor$ProtectedInputStream(InputStream inputstream) {
	is = inputstream;
    }
    
    public final void close() throws IOException {
	/* empty */
    }
    
    public final int read() throws IOException {
	return is.read();
    }
    
    public final int read(byte[] is, int i, int i_0_) throws IOException {
	return this.is.read(is, i, i_0_);
    }
    
    public final int available() throws IOException {
	return is.available();
    }
}
