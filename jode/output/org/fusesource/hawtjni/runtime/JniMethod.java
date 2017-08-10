/* JniMethod - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;
import java.lang.annotation.Annotation;

public interface JniMethod extends Annotation
{
    public String cast();
    
    public String accessor();
    
    public MethodFlag[] flags();
    
    public String copy();
    
    public String conditional();
    
    public JniArg[] callbackArgs();
}
