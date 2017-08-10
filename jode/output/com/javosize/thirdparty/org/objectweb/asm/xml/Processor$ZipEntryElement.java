/* Processor$ZipEntryElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class Processor$ZipEntryElement implements Processor$EntryElement
{
    private ZipOutputStream zos;
    
    Processor$ZipEntryElement(ZipOutputStream zipoutputstream) {
	zos = zipoutputstream;
    }
    
    public OutputStream openEntry(String string) throws IOException {
	ZipEntry zipentry = new ZipEntry(string);
	zos.putNextEntry(zipentry);
	return zos;
    }
    
    public void closeEntry() throws IOException {
	zos.flush();
	zos.closeEntry();
    }
}
