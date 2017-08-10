/* TokenRole - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.assembler.metadata.Flags;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.patterns.Role;

public final class TokenRole extends Role
{
    public static final byte FLAG_KEYWORD = 1;
    public static final byte FLAG_OPERATOR = 2;
    public static final byte FLAG_DELIMITER = 4;
    private final String _token;
    private final int _length;
    private final byte _flags;
    
    public final String getToken() {
	return _token;
    }
    
    public final int getLength() {
	return _length;
    }
    
    public final boolean isKeyword() {
	return Flags.testAny(_flags, 1);
    }
    
    public final boolean isOperator() {
	return Flags.testAny(_flags, 2);
    }
    
    public final boolean isDelimiter() {
	return Flags.testAny(_flags, 4);
    }
    
    public TokenRole(String token) {
	this(token, 0);
    }
    
    public TokenRole(String token, int flags) {
	super(token, JavaTokenNode.class, JavaTokenNode.NULL);
	_token = (String) VerifyArgument.notNull(token, "token");
	_length = token.length();
	_flags = (byte) (flags & 0xff);
    }
}
