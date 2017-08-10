/* CodePrinter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

public class CodePrinter extends PrintWriter
{
    public CodePrinter(Writer out) {
	super(out, true);
    }
    
    public CodePrinter(Writer out, boolean autoFlush) {
	super(out, autoFlush);
    }
    
    public CodePrinter(OutputStream out) {
	super(out);
    }
    
    public CodePrinter(OutputStream out, boolean autoFlush) {
	super(out, autoFlush);
    }
    
    public CodePrinter(String fileName) throws FileNotFoundException {
	super(fileName);
    }
    
    public CodePrinter(String fileName, String csn)
	throws FileNotFoundException, UnsupportedEncodingException {
	super(fileName, csn);
    }
    
    public CodePrinter(File file) throws FileNotFoundException {
	super(file);
    }
    
    public CodePrinter(File file, String csn)
	throws FileNotFoundException, UnsupportedEncodingException {
	super(file, csn);
    }
    
    public transient CodePrinter printf(String format, Object[] args) {
	return (CodePrinter) super.printf(format, args);
    }
    
    public transient CodePrinter printf(Locale l, String format,
					Object[] args) {
	return (CodePrinter) super.printf(l, format, args);
    }
    
    public transient CodePrinter format(String format, Object[] args) {
	return (CodePrinter) super.format(format, args);
    }
    
    public transient CodePrinter format(Locale l, String format,
					Object[] args) {
	return (CodePrinter) super.format(l, format, args);
    }
    
    public void increaseIndent() {
	/* empty */
    }
    
    public void decreaseIndent() {
	/* empty */
    }
}
