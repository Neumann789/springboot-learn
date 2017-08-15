/* Section - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.print;

public class Section
{
    private String title;
    private String[] content;
    
    public Section(String title, String[] content) {
	this.title = title;
	this.content = content;
    }
    
    public String getTitle() {
	return title;
    }
    
    public void setTitle(String title) {
	this.title = title;
    }
    
    public String[] getContent() {
	return content;
    }
    
    public void setContent(String[] content) {
	this.content = content;
    }
}
