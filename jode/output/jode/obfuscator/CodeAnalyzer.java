/* CodeAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import jode.bytecode.BytecodeInfo;

public interface CodeAnalyzer extends CodeTransformer
{
    public void analyzeCode(MethodIdentifier methodidentifier,
			    BytecodeInfo bytecodeinfo);
}
