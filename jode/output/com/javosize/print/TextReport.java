/* TextReport - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.print;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextReport
{
    private static final String BULLET = "*";
    public static final String INDENT = "    ";
    private List sections = new ArrayList();
    
    public void addSection(String title, String[] content) {
	sections.add(new Section(title, content));
    }
    
    public String toString() {
	StringBuffer sb = new StringBuffer();
	Iterator iterator = sections.iterator();
	while (iterator.hasNext()) {
	    Section s = (Section) iterator.next();
	    sb.append(new StringBuilder().append("* ").append(s.getTitle())
			  .append
			  ("\n").toString());
	    String[] content = s.getContent();
	    for (int i = 0; i < content.length; i++) {
		if (content[i] != null)
		    sb.append(new StringBuilder().append("    ").append
				  (content[i].replace("\n", "\n    ")).append
				  ("\n").toString());
	    }
	}
	return sb.toString();
    }
}
