/* FileOutputWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

final class FileOutputWriter extends OutputStreamWriter
{
    private final File file;
    
    FileOutputWriter(File file, DecompilerSettings settings)
	throws IOException {
	PUSH this;
    label_1481:
	{
	    PUSH new FileOutputStream(file);
	    if (!settings.isUnicodeOutputEnabled())
		PUSH Charset.defaultCharset();
	    else
		PUSH Charset.forName("UTF-8");
	    break label_1481;
	}
	((UNCONSTRUCTED)POP).OutputStreamWriter(POP, POP);
	this.file = file;
    }
    
    public File getFile() {
	return file;
    }
}
