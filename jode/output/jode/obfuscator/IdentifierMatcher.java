/* IdentifierMatcher - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;

public interface IdentifierMatcher
{
    public boolean matches(Identifier identifier);
    
    public boolean matchesSub(Identifier identifier, String string);
    
    public String getNextComponent(Identifier identifier);
}
