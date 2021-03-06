/* AstCodeHelpers - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;

public final class AstCodeHelpers
{
    public static boolean isLocalStore(AstCode code) {
	if (code != null) {
	    switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCodeHelpers$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[code.ordinal()]) {
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 5:
	    case 6:
	    case 7:
	    case 8:
	    case 9:
	    case 10:
	    case 11:
	    case 12:
	    case 13:
	    case 14:
	    case 15:
	    case 16:
	    case 17:
	    case 18:
	    case 19:
	    case 20:
	    case 21:
	    case 22:
	    case 23:
	    case 24:
	    case 25:
		return true;
	    case 26:
	    case 27:
	    case 28:
	    case 29:
	    case 30:
		return true;
	    case 31:
		return true;
	    default:
		return false;
	    }
	}
	return false;
    }
    
    public static boolean isLocalLoad(AstCode code) {
	if (code != null) {
	    switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCodeHelpers$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[code.ordinal()]) {
	    case 32:
	    case 33:
	    case 34:
	    case 35:
	    case 36:
	    case 37:
	    case 38:
	    case 39:
	    case 40:
	    case 41:
	    case 42:
	    case 43:
	    case 44:
	    case 45:
	    case 46:
	    case 47:
	    case 48:
	    case 49:
	    case 50:
	    case 51:
	    case 52:
	    case 53:
	    case 54:
	    case 55:
	    case 56:
		return true;
	    case 57:
	    case 58:
	    case 59:
	    case 60:
	    case 61:
		return true;
	    case 62:
		return true;
	    default:
		return false;
	    }
	}
	return false;
    }
    
    public static int getLoadStoreMacroArgumentIndex(AstCode code) {
	if (code != null) {
	    switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCodeHelpers$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[code.ordinal()]) {
	    case 6:
	    case 10:
	    case 14:
	    case 18:
	    case 22:
	    case 37:
	    case 41:
	    case 45:
	    case 49:
	    case 53:
		return 0;
	    case 7:
	    case 11:
	    case 15:
	    case 19:
	    case 23:
	    case 38:
	    case 42:
	    case 46:
	    case 50:
	    case 54:
		return 1;
	    case 8:
	    case 12:
	    case 16:
	    case 20:
	    case 24:
	    case 39:
	    case 43:
	    case 47:
	    case 51:
	    case 55:
		return 2;
	    case 9:
	    case 13:
	    case 17:
	    case 21:
	    case 25:
	    case 40:
	    case 44:
	    case 48:
	    case 52:
	    case 56:
		return 3;
	    default:
		return -1;
	    }
	}
	return -1;
    }
}
