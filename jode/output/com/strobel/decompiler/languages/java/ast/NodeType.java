/* NodeType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class NodeType extends Enum
{
    public static final NodeType UNKNOWN = new NodeType("UNKNOWN", 0);
    public static final NodeType TYPE_REFERENCE
	= new NodeType("TYPE_REFERENCE", 1);
    public static final NodeType TYPE_DECLARATION
	= new NodeType("TYPE_DECLARATION", 2);
    public static final NodeType MEMBER = new NodeType("MEMBER", 3);
    public static final NodeType STATEMENT = new NodeType("STATEMENT", 4);
    public static final NodeType EXPRESSION = new NodeType("EXPRESSION", 5);
    public static final NodeType TOKEN = new NodeType("TOKEN", 6);
    public static final NodeType WHITESPACE = new NodeType("WHITESPACE", 7);
    public static final NodeType PATTERN = new NodeType("PATTERN", 8);
    /*synthetic*/ private static final NodeType[] $VALUES
		      = { UNKNOWN, TYPE_REFERENCE, TYPE_DECLARATION, MEMBER,
			  STATEMENT, EXPRESSION, TOKEN, WHITESPACE, PATTERN };
    
    public static NodeType[] values() {
	return (NodeType[]) $VALUES.clone();
    }
    
    public static NodeType valueOf(String name) {
	return (NodeType) Enum.valueOf(NodeType.class, name);
    }
    
    private NodeType(String string, int i) {
	super(string, i);
    }
}
