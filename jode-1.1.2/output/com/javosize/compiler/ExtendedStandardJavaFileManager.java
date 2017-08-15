/* ExtendedStandardJavaFileManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.compiler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class ExtendedStandardJavaFileManager extends ForwardingJavaFileManager
{
    private final Map buffers = new LinkedHashMap();
    
    protected ExtendedStandardJavaFileManager(JavaFileManager fileManager) {
	super(fileManager);
    }
    
    public JavaFileObject getJavaFileForInput
	(JavaFileManager.Location location, String className,
	 JavaFileObject.Kind kind)
	throws IOException {
	if (location == StandardLocation.CLASS_OUTPUT
	    && buffers.containsKey(className)
	    && kind == JavaFileObject.Kind.CLASS) {
	    byte[] bytes = ((ByteArrayOutputStream) buffers.get(className))
			       .toByteArray();
	    return new SourceCode(URI.create(className), kind, bytes);
	}
	return fileManager.getJavaFileForInput(location, className, kind);
    }
    
    public JavaFileObject getJavaFileForOutput
	(JavaFileManager.Location location, String className,
	 JavaFileObject.Kind kind, FileObject sibling)
	throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	buffers.put(className, baos);
	return new SourceCode(URI.create(className), kind, baos);
    }
    
    public void clearBuffers() {
	buffers.clear();
    }
    
    public Map getAllBuffers() {
	Map ret = new LinkedHashMap(buffers.size() * 2);
	Iterator iterator = buffers.entrySet().iterator();
	while (iterator.hasNext()) {
	    Map.Entry entry = (Map.Entry) iterator.next();
	    ret.put(entry.getKey(),
		    ((ByteArrayOutputStream) entry.getValue()).toByteArray());
	}
	return ret;
    }
}
