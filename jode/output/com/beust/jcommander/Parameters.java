/* Parameters - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander;
import java.lang.annotation.Annotation;

public interface Parameters extends Annotation
{
    public static final String DEFAULT_OPTION_PREFIXES = "-";
    
    public String resourceBundle();
    
    public String separators();
    
    public String optionPrefixes();
    
    public String commandDescription();
    
    public String commandDescriptionKey();
    
    public String[] commandNames();
}
