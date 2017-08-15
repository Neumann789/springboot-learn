/* History - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.history;
import java.util.List;

public interface History
{
    public void push(String string);
    
    public String find(String string);
    
    public String get(int i);
    
    public int size();
    
    public void setSearchDirection(SearchDirection searchdirection);
    
    public SearchDirection getSearchDirection();
    
    public String getNextFetch();
    
    public String getPreviousFetch();
    
    public String search(String string);
    
    public void setCurrent(String string);
    
    public String getCurrent();
    
    public List getAll();
    
    public void clear();
}
