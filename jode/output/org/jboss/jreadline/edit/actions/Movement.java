/* Movement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public final class Movement extends Enum
{
    public static final Movement PREV = new Movement("PREV", 0);
    public static final Movement NEXT = new Movement("NEXT", 1);
    public static final Movement BEGINNING = new Movement("BEGINNING", 2);
    public static final Movement END = new Movement("END", 3);
    public static final Movement NEXT_WORD = new Movement("NEXT_WORD", 4);
    public static final Movement PREV_WORD = new Movement("PREV_WORD", 5);
    public static final Movement NEXT_BIG_WORD
	= new Movement("NEXT_BIG_WORD", 6);
    public static final Movement PREV_BIG_WORD
	= new Movement("PREV_BIG_WORD", 7);
    public static final Movement ALL = new Movement("ALL", 8);
    /*synthetic*/ private static final Movement[] $VALUES
		      = { PREV, NEXT, BEGINNING, END, NEXT_WORD, PREV_WORD,
			  NEXT_BIG_WORD, PREV_BIG_WORD, ALL };
    
    public static Movement[] values() {
	return (Movement[]) $VALUES.clone();
    }
    
    public static Movement valueOf(String name) {
	return (Movement) Enum.valueOf(Movement.class, name);
    }
    
    private Movement(String string, int i) {
	super(string, i);
    }
}
