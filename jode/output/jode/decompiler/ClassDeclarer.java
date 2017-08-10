/* ClassDeclarer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import jode.bytecode.ClassInfo;

public interface ClassDeclarer
{
    public ClassDeclarer getParent();
    
    public ClassAnalyzer getClassAnalyzer(ClassInfo classinfo);
    
    public void addClassAnalyzer(ClassAnalyzer classanalyzer);
}
