/* RecipeParam - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.recipes;
import java.io.Serializable;

public class RecipeParam implements Serializable
{
    private static final long serialVersionUID = 3762571087854456934L;
    private int id;
    private String description;
    
    public RecipeParam() {
	/* empty */
    }
    
    public RecipeParam(int id, String description) {
	this.id = id;
	this.description = description;
    }
    
    public int getId() {
	return id;
    }
    
    public void setId(int id) {
	this.id = id;
    }
    
    public String getDescription() {
	return description;
    }
    
    public void setDescription(String description) {
	this.description = description;
    }
}
