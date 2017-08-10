/* DynamicParameter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander;
import java.lang.annotation.Annotation;

public interface DynamicParameter extends Annotation
{
    public String[] names();
    
    public boolean required();
    
    public String description();
    
    public String descriptionKey();
    
    public boolean hidden();
    
    public Class validateWith();
    
    public String assignment();
    
    public Class validateValueWith();
}
