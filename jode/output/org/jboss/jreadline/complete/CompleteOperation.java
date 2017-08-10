/* CompleteOperation - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.complete;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.jreadline.util.Parser;

public class CompleteOperation
{
    private String buffer;
    private int cursor;
    private int offset;
    private List completionCandidates;
    
    public CompleteOperation(String buffer, int cursor) {
	setBuffer(buffer);
	setCursor(cursor);
	completionCandidates = new ArrayList();
    }
    
    public String getBuffer() {
	return buffer;
    }
    
    private void setBuffer(String buffer) {
	this.buffer = buffer;
    }
    
    public int getCursor() {
	return cursor;
    }
    
    private void setCursor(int cursor) {
	this.cursor = cursor;
    }
    
    public int getOffset() {
	return offset;
    }
    
    public void setOffset(int offset) {
	this.offset = offset;
    }
    
    public List getCompletionCandidates() {
	return completionCandidates;
    }
    
    public void setCompletionCandidates(List completionCandidates) {
	this.completionCandidates = completionCandidates;
    }
    
    public void addCompletionCandidate(String completionCandidate) {
	completionCandidates.add(completionCandidate);
    }
    
    public void addCompletionCandidates(List completionCandidates) {
	this.completionCandidates.addAll(completionCandidates);
    }
    
    public void removeEscapedSpacesFromCompletionCandidates() {
	setCompletionCandidates(Parser.switchEscapedSpacesToSpacesInList
				(getCompletionCandidates()));
    }
    
    public List getFormattedCompletionCandidates() {
	if (offset >= cursor)
	    return completionCandidates;
	List fixedCandidates = new ArrayList(completionCandidates.size());
	int pos = cursor - offset;
	Iterator iterator = completionCandidates.iterator();
	for (;;) {
	    if (!iterator.hasNext())
		return fixedCandidates;
	    String c = (String) iterator.next();
	    if (c.length() < pos)
		fixedCandidates.add("");
	    else
		fixedCandidates.add(c.substring(pos));
	    continue;
	}
    }
    
    public String getFormattedCompletion(String completion) {
	if (offset >= cursor)
	    return completion;
	int pos = cursor - offset;
	if (completion.length() <= pos)
	    return "";
	return completion.substring(pos);
    }
    
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("Buffer: ").append(buffer).append(", Cursor:").append
	    (cursor).append
	    (", Offset:").append(offset);
	sb.append(", candidates:").append(completionCandidates);
	return sb.toString();
    }
}
