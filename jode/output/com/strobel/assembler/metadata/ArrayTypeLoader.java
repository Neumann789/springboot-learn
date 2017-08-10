/* ArrayTypeLoader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.strobel.assembler.ir.ConstantPool;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;

public final class ArrayTypeLoader implements ITypeLoader
{
    private static final Logger LOG
	= Logger.getLogger(ArrayTypeLoader.class.getSimpleName());
    private final Buffer _buffer;
    private Throwable _parseError;
    private boolean _parsed;
    private String _className;
    
    public ArrayTypeLoader(byte[] bytes) {
	VerifyArgument.notNull(bytes, "bytes");
	_buffer = new Buffer(Arrays.copyOf(bytes, bytes.length));
    }
    
    public String getClassNameFromArray() {
	ensureParsed(true);
	return _className;
    }
    
    public boolean tryLoadType(String internalName, Buffer buffer) {
	ensureParsed(false);
	if (!StringUtilities.equals(internalName, _className))
	    return false;
	buffer.reset(_buffer.size());
	buffer.putByteArray(_buffer.array(), 0, _buffer.size());
	buffer.position(0);
	return true;
    }
    
    private void ensureParsed(boolean throwOnError) {
    label_1174:
	{
	    if (!_parsed) {
		if (LOG.isLoggable(Level.FINE))
		    LOG.log
			(Level.FINE,
			 "Parsing classfile header from user-provided buffer...");
	    } else {
		if (throwOnError && _parseError != null)
		    throw new IllegalStateException
			      ("Error parsing classfile header.", _parseError);
		return;
	    }
	}
    label_1175:
	{
	label_1178:
	    {
	    label_1176:
		{
		    try {
			_className = getInternalNameFromClassFile(_buffer);
			if (!LOG.isLoggable(Level.FINE))
			    break label_1175;
		    } catch (Throwable PUSH) {
			break label_1176;
		    } finally {
			break label_1178;
		    }
		    try {
			LOG.log(Level.FINE,
				"Parsed header for class: " + _className);
		    } catch (Throwable PUSH) {
			/* empty */
		    } finally {
			break label_1178;
		    }
		}
		Throwable t;
	    label_1177:
		{
		    try {
			t = POP;
			_parseError = t;
			if (!LOG.isLoggable(Level.FINE))
			    break label_1177;
		    } finally {
			break label_1178;
		    }
		    try {
			LOG.log(Level.FINE, "Error parsing classfile header.",
				t);
		    } finally {
			break label_1178;
		    }
		}
		try {
		    if (throwOnError) {
			try {
			    throw new IllegalStateException
				      ("Error parsing classfile header.", t);
			} finally {
			    break label_1178;
			}
		    }
		} finally {
		    break label_1178;
		}
		_parsed = true;
		return;
	    }
	    Object object = POP;
	    _parsed = true;
	    throw object;
	}
	_parsed = true;
	break label_1174;
    }
    
    private static String getInternalNameFromClassFile(Buffer b) {
	long magic = (long) b.readInt() & 0xffffffffL;
	if (magic == 3405691582L) {
	    b.readUnsignedShort();
	    b.readUnsignedShort();
	    ConstantPool constantPool = ConstantPool.read(b);
	    b.readUnsignedShort();
	    ConstantPool.TypeInfoEntry thisClass
		= ((ConstantPool.TypeInfoEntry)
		   constantPool.getEntry(b.readUnsignedShort()));
	    b.position(0);
	    return thisClass.getName();
	}
	throw new IllegalStateException("Bad magic number: 0x"
					+ Long.toHexString(magic));
    }
}
