/* ControlOperatorParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.console.operator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.jreadline.console.ConsoleOperation;

public class ControlOperatorParser
{
    private static Pattern controlOperatorPattern
	= (Pattern.compile
	   ("(2>&1)|(2>>)|(2>)|(>>)|(?!<%.*)([^%]>)(?!.*%>)|(?!<%.*)(<)(?!.*%>)|(\\|&)|(?!<%.*)(\\|)(?!.*%>)|(;)|(&&)|(&)"));
    private static Pattern redirectionNoPipelinePattern
	= Pattern.compile("(>)|(<)");
    private static Pattern pipelinePattern
	= Pattern.compile("(?!<%.*)(\\|)(?!.*%>)");
    
    public static boolean doStringContainRedirectionNoPipeline(String buffer) {
	return redirectionNoPipelinePattern.matcher(buffer).find();
    }
    
    public static boolean doStringContainPipeline(String buffer) {
	return pipelinePattern.matcher(buffer).find();
    }
    
    public static int getPositionOfFirstRedirection(String buffer) {
	Matcher matcher = redirectionNoPipelinePattern.matcher(buffer);
	if (matcher.find())
	    return matcher.end();
	return 0;
    }
    
    public static int findLastPipelinePositionBeforeCursor(String buffer,
							   int cursor) {
	return (findLastRedirectionOrPipelinePositionBeforeCursor
		(pipelinePattern, buffer, cursor));
    }
    
    public static int findLastRedirectionPositionBeforeCursor(String buffer,
							      int cursor) {
	return (findLastRedirectionOrPipelinePositionBeforeCursor
		(redirectionNoPipelinePattern, buffer, cursor));
    }
    
    private static int findLastRedirectionOrPipelinePositionBeforeCursor
	(Pattern pattern, String buffer, int cursor) {
	Matcher matcher = pattern.matcher(buffer);
	if (cursor > buffer.length())
	    cursor = buffer.length();
	int end = 0;
	while (matcher.find()) {
	    if (matcher.start() > cursor)
		return end;
	    end = matcher.end();
	}
	return end;
    }
    
    public static List findAllControlOperators(String buffer) {
	Matcher matcher = controlOperatorPattern.matcher(buffer);
	List reOpList = new ArrayList();
	while (matcher.find()) {
	    if (matcher.group(5) != null) {
		reOpList.add(new ConsoleOperation
			     (ControlOperator.OVERWRITE_OUT,
			      (removeJavOSizeEscapeCharacters
			       (buffer.substring(0, matcher.start(5))))));
		buffer = buffer.substring(matcher.end(5));
		matcher = controlOperatorPattern.matcher(buffer);
	    } else if (matcher.group(6) != null) {
		reOpList.add(new ConsoleOperation
			     (ControlOperator.OVERWRITE_IN,
			      (removeJavOSizeEscapeCharacters
			       (buffer.substring(0, matcher.start(6))))));
		buffer = buffer.substring(matcher.end(6));
		matcher = controlOperatorPattern.matcher(buffer);
	    } else if (matcher.group(8) != null) {
		reOpList.add(new ConsoleOperation
			     (ControlOperator.PIPE,
			      (removeJavOSizeEscapeCharacters
			       (buffer.substring(0, matcher.start(8))))));
		buffer = buffer.substring(matcher.end(8));
		matcher = controlOperatorPattern.matcher(buffer);
	    }
	}
	if (reOpList.size() == 0)
	    reOpList.add
		(new ConsoleOperation(ControlOperator.NONE,
				      removeJavOSizeEscapeCharacters(buffer)));
	if (buffer.trim().length() > 0)
	    reOpList.add
		(new ConsoleOperation(ControlOperator.NONE,
				      removeJavOSizeEscapeCharacters(buffer)));
	return reOpList;
    }
    
    private static String removeJavOSizeEscapeCharacters(String buffer) {
	if (buffer.contains("<%") && buffer.contains("%>"))
	    buffer = buffer.replace("<%", "").replace("%>", "");
	return buffer;
    }
}
