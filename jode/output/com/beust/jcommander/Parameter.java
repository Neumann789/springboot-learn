/* Parameter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander;
import java.lang.annotation.Annotation;

public interface Parameter extends Annotation
{
    public String[] names();
    
    public String description();
    
    public boolean required();
    
    public String descriptionKey();
    
    public int arity();
    
    public boolean password();
    
    public Class converter();
    
    public Class listConverter();
    
    public boolean hidden();
    
    public Class validateWith();
    
    public Class validateValueWith();
    
    public boolean variableArity();
    
    public Class splitter();
    
    public boolean echoInput();
    
    public boolean help();
}
