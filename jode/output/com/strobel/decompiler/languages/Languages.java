/* Languages - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages;
import java.util.List;

import com.strobel.core.ArrayUtilities;
import com.strobel.decompiler.languages.java.JavaLanguage;

public final class Languages
{
    private static final List ALL_LANGUAGES;
    private static final List DEBUG_LANGUAGES;
    private static final JavaLanguage JAVA;
    private static final Language BYTECODE_AST_UNOPTIMIZED;
    private static final Language BYTECODE_AST;
    private static final Language BYTECODE;
    
    public static List all() {
	return ALL_LANGUAGES;
    }
    
    public static List debug() {
	return DEBUG_LANGUAGES;
    }
    
    public static JavaLanguage java() {
	return JAVA;
    }
    
    public static Language bytecode() {
	return BYTECODE;
    }
    
    public static Language bytecodeAst() {
	return BYTECODE_AST;
    }
    
    public static Language bytecodeAstUnoptimized() {
	return BYTECODE_AST_UNOPTIMIZED;
    }
    
    static {
	List bytecodeAstLanguages = BytecodeAstLanguage.getDebugLanguages();
	JAVA = new JavaLanguage();
	BYTECODE = new BytecodeLanguage();
	BYTECODE_AST_UNOPTIMIZED = (Language) bytecodeAstLanguages.get(0);
	BYTECODE_AST = new BytecodeAstLanguage();
	Language[] languages = new Language[bytecodeAstLanguages.size()];
	int i = 0;
	for (;;) {
	    if (i >= languages.length) {
		ALL_LANGUAGES
		    = (ArrayUtilities.asUnmodifiableList
		       (new Language[] { JAVA, BYTECODE_AST,
					 BYTECODE_AST_UNOPTIMIZED }));
		DEBUG_LANGUAGES = ArrayUtilities.asUnmodifiableList(languages);
	    }
	    languages[i] = (Language) bytecodeAstLanguages.get(i);
	    i++;
	}
    }
}
