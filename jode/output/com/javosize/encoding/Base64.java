/* Base64 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.encoding;
import javax.xml.bind.DatatypeConverter;

public class Base64
{
    public static byte[] decodeBytesFromString(String text) {
	return DatatypeConverter.parseBase64Binary(text);
    }
    
    public static String encodeBytesToString(byte[] bytes) {
	return DatatypeConverter.printBase64Binary(bytes);
    }
}
