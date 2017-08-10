/* CommaParameterSplitter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import java.util.Arrays;
import java.util.List;

public class CommaParameterSplitter implements IParameterSplitter
{
    public List split(String value) {
	return Arrays.asList(value.split(","));
    }
}
