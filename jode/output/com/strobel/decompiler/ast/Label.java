/* Label - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import com.strobel.decompiler.ITextOutput;

public class Label extends Node
{
    private String _name;
    private int _offset = -1;
    
    public Label() {
	/* empty */
    }
    
    public Label(String name) {
	_name = name;
    }
    
    public String getName() {
	return _name;
    }
    
    public void setName(String name) {
	_name = name;
    }
    
    public int getOffset() {
	return _offset;
    }
    
    public void setOffset(int offset) {
	_offset = offset;
    }
    
    public void writeTo(ITextOutput output) {
	output.writeDefinition(getName() + ":", this);
    }
}
