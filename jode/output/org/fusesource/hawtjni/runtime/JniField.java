/* JniField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;
import java.lang.annotation.Annotation;

public interface JniField extends Annotation
{
    public String cast();
    
    public String accessor();
    
    public String conditional();
    
    public FieldFlag[] flags();
}
