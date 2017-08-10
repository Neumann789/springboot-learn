/* UnaryOperatorType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class UnaryOperatorType extends Enum
{
    public static final UnaryOperatorType ANY
	= new UnaryOperatorType("ANY", 0);
    public static final UnaryOperatorType NOT
	= new UnaryOperatorType("NOT", 1);
    public static final UnaryOperatorType BITWISE_NOT
	= new UnaryOperatorType("BITWISE_NOT", 2);
    public static final UnaryOperatorType MINUS
	= new UnaryOperatorType("MINUS", 3);
    public static final UnaryOperatorType PLUS
	= new UnaryOperatorType("PLUS", 4);
    public static final UnaryOperatorType INCREMENT
	= new UnaryOperatorType("INCREMENT", 5);
    public static final UnaryOperatorType DECREMENT
	= new UnaryOperatorType("DECREMENT", 6);
    public static final UnaryOperatorType POST_INCREMENT
	= new UnaryOperatorType("POST_INCREMENT", 7);
    public static final UnaryOperatorType POST_DECREMENT
	= new UnaryOperatorType("POST_DECREMENT", 8);
    /*synthetic*/ private static final UnaryOperatorType[] $VALUES
		      = { ANY, NOT, BITWISE_NOT, MINUS, PLUS, INCREMENT,
			  DECREMENT, POST_INCREMENT, POST_DECREMENT };
    
    public static UnaryOperatorType[] values() {
	return (UnaryOperatorType[]) $VALUES.clone();
    }
    
    public static UnaryOperatorType valueOf(String name) {
	return (UnaryOperatorType) Enum.valueOf(UnaryOperatorType.class, name);
    }
    
    private UnaryOperatorType(String string, int i) {
	super(string, i);
    }
}
