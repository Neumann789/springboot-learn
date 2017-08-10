/* TableSwitchGenerator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.Label;

public interface TableSwitchGenerator
{
    public void generateCase(int i, Label label);
    
    public void generateDefault();
}
