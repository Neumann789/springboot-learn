/* LocalVarEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import jode.type.Type;

public class LocalVarEntry
{
    String name;
    Type type;
    int startAddr;
    int endAddr;
    LocalVarEntry next;
    
    public LocalVarEntry(int i, int i_0_, String string, Type type) {
	startAddr = i;
	endAddr = i_0_;
	name = string;
	this.type = type;
	next = null;
    }
    
    public String getName() {
	return name;
    }
    
    public Type getType() {
	return type;
    }
}
