/* CommentType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class CommentType extends Enum
{
    public static final CommentType SingleLine
	= new CommentType("SingleLine", 0);
    public static final CommentType MultiLine
	= new CommentType("MultiLine", 1);
    public static final CommentType Documentation
	= new CommentType("Documentation", 2);
    /*synthetic*/ private static final CommentType[] $VALUES
		      = { SingleLine, MultiLine, Documentation };
    
    public static CommentType[] values() {
	return (CommentType[]) $VALUES.clone();
    }
    
    public static CommentType valueOf(String name) {
	return (CommentType) Enum.valueOf(CommentType.class, name);
    }
    
    private CommentType(String string, int i) {
	super(string, i);
    }
}
