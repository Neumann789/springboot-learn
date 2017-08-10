/* Error - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.io;
import com.strobel.util.ContractUtils;

final class Error
{
    private Error() {
	throw ContractUtils.unreachable();
    }
    
    static IllegalArgumentException invalidPathCharacters() {
	return (new IllegalArgumentException
		("Path contains invalid characters."));
    }
    
    static IllegalArgumentException illegalPath() {
	return (new IllegalArgumentException
		("Specified capacity must not be less than the current capacity."));
    }
    
    static IllegalArgumentException pathUriFormatNotSupported() {
	return new IllegalArgumentException("URI formats are not supported.");
    }
    
    static IllegalArgumentException illegalUncPath() {
	return (new IllegalArgumentException
		("The UNC path should be of the form \\\\server\\share."));
    }
    
    static IllegalArgumentException pathTooLong() {
	return (new IllegalArgumentException
		("The specified path, file name, or both are too long. The fully qualified file name must be less than 260 characters, and the directory name must be less than 248 characters."));
    }
    
    static IllegalArgumentException canonicalizationError(Throwable t) {
	return new IllegalArgumentException(t.getMessage());
    }
}
