/* InternalCommands - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.console;

public final class InternalCommands extends Enum
{
    public static final InternalCommands ALIAS
	= new InternalCommands("ALIAS", 0, "alias");
    public static final InternalCommands UNALIAS
	= new InternalCommands("UNALIAS", 1, "unalias");
    public static final InternalCommands ECHO
	= new InternalCommands("ECHO", 2, "echo");
    private String command;
    /*synthetic*/ private static final InternalCommands[] $VALUES
		      = { ALIAS, UNALIAS, ECHO };
    
    public static InternalCommands[] values() {
	return (InternalCommands[]) $VALUES.clone();
    }
    
    public static InternalCommands valueOf(String name) {
	return (InternalCommands) Enum.valueOf(InternalCommands.class, name);
    }
    
    private InternalCommands(String string, int i, String alias) {
	super(string, i);
	command = alias;
    }
    
    public String getCommand() {
	return command;
    }
}
