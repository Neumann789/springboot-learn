/* FileConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import java.io.File;

import com.beust.jcommander.IStringConverter;

public class FileConverter implements IStringConverter
{
    public File convert(String value) {
	return new File(value);
    }
}
