/* CheckMethodAdapter$1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.analysis.Analyzer;
import com.javosize.thirdparty.org.objectweb.asm.tree.analysis.BasicVerifier;

class CheckMethodAdapter$1 extends MethodNode
{
    /*synthetic*/ final MethodVisitor val$cmv;
    
    CheckMethodAdapter$1(int i, int i_0_, String string, String string_1_,
			 String string_2_, String[] strings,
			 MethodVisitor methodvisitor) {
	val$cmv = methodvisitor;
	super(i, i_0_, string, string_1_, string_2_, strings);
    }
    
    public void visitEnd() {
	Analyzer analyzer = new Analyzer(new BasicVerifier());
	try {
	    analyzer.analyze("dummy", this);
	    accept(val$cmv);
	} catch (Exception PUSH) {
	    Exception exception = POP;
	    if (!(exception instanceof IndexOutOfBoundsException)
		|| maxLocals != 0 || maxStack != 0) {
		exception.printStackTrace();
		StringWriter stringwriter = new StringWriter();
		PrintWriter printwriter = new PrintWriter(stringwriter, true);
		CheckClassAdapter.printAnalyzerResult(this, analyzer,
						      printwriter);
		printwriter.close();
		throw new RuntimeException(exception.getMessage() + ' '
					   + stringwriter.toString());
	    }
	    throw new RuntimeException
		      ("Data flow checking option requires valid, non zero maxLocals and maxStack values.");
	}
    }
}
