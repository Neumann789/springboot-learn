/* AnnotationElementType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.annotations;

public final class AnnotationElementType extends Enum
{
    public static final AnnotationElementType Constant
	= new AnnotationElementType("Constant", 0);
    public static final AnnotationElementType Enum
	= new AnnotationElementType("Enum", 1);
    public static final AnnotationElementType Array
	= new AnnotationElementType("Array", 2);
    public static final AnnotationElementType Class
	= new AnnotationElementType("Class", 3);
    public static final AnnotationElementType Annotation
	= new AnnotationElementType("Annotation", 4);
    /*synthetic*/ private static final AnnotationElementType[] $VALUES
		      = { Constant, Enum, Array, Class, Annotation };
    
    public static AnnotationElementType[] values() {
	return (AnnotationElementType[]) $VALUES.clone();
    }
    
    public static AnnotationElementType valueOf(String name) {
	return ((AnnotationElementType)
		Enum.valueOf(AnnotationElementType.class, name));
    }
    
    private AnnotationElementType(String string, int i) {
	super(string, i);
    }
    
    public static AnnotationElementType forTag(char tag) {
	switch (tag) {
	case 'B':
	case 'C':
	case 'D':
	case 'F':
	case 'I':
	case 'J':
	case 'S':
	case 'Z':
	case 's':
	    return Constant;
	case 'e':
	    return Enum;
	case '[':
	    return Array;
	case 'c':
	    return Class;
	case '@':
	    return Annotation;
	default:
	    throw new IllegalArgumentException
		      ("Invalid annotation element tag: " + tag);
	}
    }
}
