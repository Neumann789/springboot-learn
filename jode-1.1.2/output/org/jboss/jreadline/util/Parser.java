/* Parser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.jreadline.complete.CompleteOperation;
import org.jboss.jreadline.console.Config;

public class Parser
{
    private static final String spaceEscapedMatcher = "\\ ";
    private static final String SPACE = " ";
    private static final char SPACE_CHAR = ' ';
    private static final char SLASH = '\\';
    private static final Pattern spaceEscapedPattern
	= Pattern.compile("\\\\ ");
    private static final Pattern spacePattern = Pattern.compile(" ");
    
    public static String formatDisplayList(String[] displayList,
					   int termHeight, int termWidth) {
	return formatDisplayList(Arrays.asList(displayList), termHeight,
				 termWidth);
    }
    
    public static String formatDisplayList(List displayList, int termHeight,
					   int termWidth) {
	if (displayList == null || displayList.size() < 1)
	    return "";
	int maxLength = 0;
	Iterator iterator = displayList.iterator();
	while (iterator.hasNext()) {
	    String completion = (String) iterator.next();
	    if (completion.length() > maxLength)
		maxLength = completion.length();
	}
	maxLength += 2;
	int numColumns = termWidth / maxLength;
	if (numColumns == 0)
	    numColumns = 1;
	if (numColumns > displayList.size())
	    numColumns = displayList.size();
	int numRows = displayList.size() / numColumns;
	if (numRows * numColumns < displayList.size())
	    numRows++;
	StringBuilder completionOutput = new StringBuilder();
	for (int i = 0; i < numRows; i++) {
	    for (int c = 0; c < numColumns; c++) {
		int fetch = i + c * numRows;
		if (fetch >= displayList.size())
		    break;
		completionOutput.append(padRight(maxLength,
						 ((String)
						  displayList
						      .get(i + c * numRows))));
	    }
	    completionOutput.append(Config.getLineSeparator());
	}
	return completionOutput.toString();
    }
    
    private static String padRight(int n, String s) {
	return String.format(new StringBuilder().append("%1$-").append(n)
				 .append
				 ("s").toString(),
			     new Object[] { s });
    }
    
    public static String findStartsWithOperation(List coList) {
	List tmpList = new ArrayList();
	Iterator iterator = coList.iterator();
	while (iterator.hasNext()) {
	    CompleteOperation co = (CompleteOperation) iterator.next();
	    String s = findStartsWith(co.getFormattedCompletionCandidates());
	    if (s.length() > 0)
		tmpList.add(s);
	    else
		return "";
	}
	return findStartsWith(tmpList);
    }
    
    public static String findStartsWith(List completionList) {
	StringBuilder builder = new StringBuilder();
	Iterator iterator = completionList.iterator();
	while (iterator.hasNext()) {
	    String completion = (String) iterator.next();
	    while (builder.length() < completion.length()
		   && startsWith(completion.substring(0, builder.length() + 1),
				 completionList))
		builder.append(completion.charAt(builder.length()));
	}
	return builder.toString();
    }
    
    private static boolean startsWith(String criteria, List completionList) {
	Iterator iterator = completionList.iterator();
	while (iterator.hasNext()) {
	    String completion = (String) iterator.next();
	    if (!completion.startsWith(criteria))
		return false;
	}
	return true;
    }
    
    public static String findWordClosestToCursor(String text, int cursor) {
	if (text.length() <= cursor + 1) {
	    if (text.contains(" ")) {
		if (doWordContainEscapedSpace(text)) {
		    if (doWordContainOnlyEscapedSpace(text))
			return switchEscapedSpacesToSpacesInWord(text);
		    return (switchEscapedSpacesToSpacesInWord
			    (findEscapedSpaceWordCloseToEnd(text)));
		}
		if (text.lastIndexOf(" ") >= cursor)
		    return text.substring
			       (text.substring(0, cursor).lastIndexOf(" "))
			       .trim();
		return text.substring(text.lastIndexOf(" ")).trim();
	    }
	    return text.trim();
	}
	String rest;
	if (text.length() > cursor + 1)
	    rest = text.substring(0, cursor + 1);
	else
	    rest = text;
	if (doWordContainOnlyEscapedSpace(rest)) {
	    if (cursor > 1 && text.charAt(cursor) == ' '
		&& text.charAt(cursor - 1) == ' ')
		return "";
	    return switchEscapedSpacesToSpacesInWord(rest);
	}
	if (cursor > 1 && text.charAt(cursor) == ' '
	    && text.charAt(cursor - 1) == ' ')
	    return "";
	if (rest.trim().contains(" "))
	    return rest.substring(rest.trim().lastIndexOf(" ")).trim();
	return rest.trim();
    }
    
    public static String findEscapedSpaceWordCloseToEnd(String text) {
	String originalText = text;
    while_11_:
	do {
	    int index;
	    for (;;) {
		if ((index = text.lastIndexOf(" ")) <= -1)
		    break while_11_;
		if (index <= 0 || text.charAt(index - 1) != '\\')
		    break;
		text = text.substring(0, index - 1);
	    }
	    return originalText.substring(index + 1);
	} while (false);
	return originalText;
    }
    
    public static List findAllWords(String text) {
	if (!doWordContainEscapedSpace(text))
	    return Arrays.asList(text.trim().split(" "));
	List textList = new ArrayList();
	Matcher matcher = spacePattern.matcher(text);
	while (matcher.find()) {
	    if (matcher.start() > 0) {
		if (text.charAt(matcher.start() - 1) == '\\') {
		    if (matcher.end() + 1 < text.length()
			&& text.charAt(matcher.end()) == ' ') {
			text = text.substring(matcher.end() + 1);
			matcher = spacePattern.matcher(text);
		    }
		} else {
		    textList.add(text.substring(0, matcher.start()));
		    text = text.substring(matcher.end());
		    matcher = spacePattern.matcher(text);
		}
	    } else {
		text = text.substring(1);
		matcher = spacePattern.matcher(text);
	    }
	}
	if (text.length() > 0)
	    textList.add(text);
	return textList;
    }
    
    public static boolean doWordContainOnlyEscapedSpace(String word) {
	return (findAllOccurrences(word, "\\ ")
		== findAllOccurrences(word, " "));
    }
    
    public static boolean doWordContainEscapedSpace(String word) {
	return spaceEscapedPattern.matcher(word).find();
    }
    
    public static int findAllOccurrences(String word, String pattern) {
	int count = 0;
	for (/**/; word.contains(pattern);
	     word = word.substring(word.indexOf(pattern) + pattern.length()))
	    count++;
	return count;
    }
    
    public static List switchEscapedSpacesToSpacesInList(List list) {
	List newList = new ArrayList(list.size());
	Iterator iterator = list.iterator();
	while (iterator.hasNext()) {
	    String s = (String) iterator.next();
	    newList.add(switchEscapedSpacesToSpacesInWord(s));
	}
	return newList;
    }
    
    public static String switchSpacesToEscapedSpacesInWord(String word) {
	return spacePattern.matcher(word).replaceAll("\\\\ ");
    }
    
    public static String switchEscapedSpacesToSpacesInWord(String word) {
	return spaceEscapedPattern.matcher(word).replaceAll(" ");
    }
    
    public static String trim(String buffer) {
	int count = 0;
	for (int i = 0; i < buffer.length() && buffer.charAt(i) == ' '; i++)
	    count++;
	if (count > 0)
	    buffer = buffer.substring(count);
	count = buffer.length();
	for (int i = buffer.length() - 1;
	     i > 0 && buffer.charAt(i) == ' ' && buffer.charAt(i - 1) != '\\';
	     i--)
	    count--;
	if (count != buffer.length())
	    buffer = buffer.substring(0, count);
	return buffer;
    }
    
    public static String findFirstWord(String buffer) {
	if (buffer.indexOf(' ') < 0)
	    return buffer;
	buffer = trim(buffer);
	int index = buffer.indexOf(' ');
	if (index > 0)
	    return buffer.substring(0, index);
	return buffer;
    }
}
