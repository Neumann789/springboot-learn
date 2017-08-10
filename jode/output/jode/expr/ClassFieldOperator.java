/* ClassFieldOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class ClassFieldOperator extends NoArgOperator
{
    Type classType;
    
    public ClassFieldOperator(Type type) {
	super(Type.tJavaLangClass);
	classType = type;
    }
    
    public int getPriority() {
	return 950;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.printType(classType);
	tabbedprintwriter.print(".class");
    }
}
