/* Mode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit;

public final class Mode extends Enum
{
    public static final Mode VI = new Mode("VI", 0);
    public static final Mode EMACS = new Mode("EMACS", 1);
    /*synthetic*/ private static final Mode[] $VALUES = { VI, EMACS };
    
    public static Mode[] values() {
	return (Mode[]) $VALUES.clone();
    }
    
    public static Mode valueOf(String name) {
	return (Mode) Enum.valueOf(Mode.class, name);
    }
    
    private Mode(String string, int i) {
	super(string, i);
    }
}
