/* JniClass - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;
import java.lang.annotation.Annotation;

public interface JniClass extends Annotation
{
    public ClassFlag[] flags();
    
    public String conditional();
    
    public String name();
}
