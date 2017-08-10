/* DefiniteAssignmentStatus - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class DefiniteAssignmentStatus extends Enum
{
    public static final DefiniteAssignmentStatus DEFINITELY_NOT_ASSIGNED
	= new DefiniteAssignmentStatus("DEFINITELY_NOT_ASSIGNED", 0);
    public static final DefiniteAssignmentStatus POTENTIALLY_ASSIGNED
	= new DefiniteAssignmentStatus("POTENTIALLY_ASSIGNED", 1);
    public static final DefiniteAssignmentStatus DEFINITELY_ASSIGNED
	= new DefiniteAssignmentStatus("DEFINITELY_ASSIGNED", 2);
    public static final DefiniteAssignmentStatus ASSIGNED_AFTER_TRUE_EXPRESSION
	= new DefiniteAssignmentStatus("ASSIGNED_AFTER_TRUE_EXPRESSION", 3);
    public static final DefiniteAssignmentStatus ASSIGNED_AFTER_FALSE_EXPRESSION
	= new DefiniteAssignmentStatus("ASSIGNED_AFTER_FALSE_EXPRESSION", 4);
    public static final DefiniteAssignmentStatus CODE_UNREACHABLE
	= new DefiniteAssignmentStatus("CODE_UNREACHABLE", 5);
    /*synthetic*/ private static final DefiniteAssignmentStatus[] $VALUES
		      = { DEFINITELY_NOT_ASSIGNED, POTENTIALLY_ASSIGNED,
			  DEFINITELY_ASSIGNED, ASSIGNED_AFTER_TRUE_EXPRESSION,
			  ASSIGNED_AFTER_FALSE_EXPRESSION, CODE_UNREACHABLE };
    
    public static DefiniteAssignmentStatus[] values() {
	return (DefiniteAssignmentStatus[]) $VALUES.clone();
    }
    
    public static DefiniteAssignmentStatus valueOf(String name) {
	return ((DefiniteAssignmentStatus)
		Enum.valueOf(DefiniteAssignmentStatus.class, name));
    }
    
    private DefiniteAssignmentStatus(String string, int i) {
	super(string, i);
    }
}
