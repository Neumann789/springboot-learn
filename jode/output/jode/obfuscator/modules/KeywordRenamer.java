/* KeywordRenamer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;
import java.util.Iterator;

import jode.obfuscator.Identifier;
import jode.obfuscator.OptionHandler;
import jode.obfuscator.Renamer;

public class KeywordRenamer implements Renamer, OptionHandler
{
    String[] keywords
	= { "if", "else", "for", "while", "throw", "return", "class",
	    "interface", "implements", "extends", "instanceof", "new", "int",
	    "boolean", "long", "float", "double", "short", "public",
	    "protected", "private", "static", "synchronized", "strict",
	    "transient", "abstract", "volatile", "final", "Object", "String",
	    "Thread", "Runnable", "StringBuffer", "Vector" };
    Renamer backup = new StrongRenamer();
    
    public void setOption(String string, Collection collection) {
	if (!string.startsWith("keywords")) {
	    if (!string.startsWith("backup"))
		throw new IllegalArgumentException("Invalid option `" + string
						   + "'");
	    if (collection.size() == 1)
		backup = (Renamer) collection.iterator().next();
	    else
		throw new IllegalArgumentException
			  ("Only one backup is allowed");
	} else
	    keywords
		= (String[]) collection.toArray(new String[collection.size()]);
	return;
    }
    
    public Iterator generateNames(final Identifier ident) {
	return new Iterator() {
	    int pos;
	    Iterator backing;
	    
	    {
		super();
		pos = 0;
		backing = null;
	    }
	    
	    public boolean hasNext() {
		return true;
	    }
	    
	    public Object next() {
	    label_1050:
		{
		    if (pos >= keywords.length) {
			if (backing == null)
			    backing = backup.generateNames(ident);
		    } else
			return keywords[pos++];
		}
		return backing.next();
		break label_1050;
	    }
	    
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
}
