/* ParseException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;

public class ParseException extends Exception
{
    public ParseException(int i, String string) {
	super("line " + i + ": " + string);
    }
}
