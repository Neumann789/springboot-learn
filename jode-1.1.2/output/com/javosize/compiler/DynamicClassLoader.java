/* DynamicClassLoader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.compiler;

public class DynamicClassLoader extends ClassLoader
{
    private CompiledCode cc;
    
    public DynamicClassLoader(ClassLoader parent) {
	super(parent);
    }
    
    public void setCode(CompiledCode cc) {
	this.cc = cc;
    }
    
    public byte[] getByteCode() {
	return cc.getByteCode();
    }
    
    protected Class findClass(String name) throws ClassNotFoundException {
	if (cc == null)
	    return super.findClass(name);
	byte[] byteCode = cc.getByteCode();
	return super.defineClass(name, byteCode, 0, byteCode.length);
    }
}
