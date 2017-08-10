/* JniArg - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;
import java.lang.annotation.Annotation;

public interface JniArg extends Annotation
{
    public ArgFlag[] flags();
    
    public String cast();
}
