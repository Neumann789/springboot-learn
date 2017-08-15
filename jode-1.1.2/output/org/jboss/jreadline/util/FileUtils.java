/* FileUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.javosize.cli.Main;

import org.jboss.jreadline.complete.CompleteOperation;
import org.jboss.jreadline.console.Config;

public class FileUtils
{
    private static final Pattern startsWithParent
	= Pattern.compile("^\\.\\..*");
    private static final Pattern containParent
	= Pattern.compile(new StringBuilder().append("[\\.\\.[").append
			      (Config.getPathSeparatorForRegexExpressions())
			      .append
			      ("]?]+").toString());
    private static final Pattern space = Pattern.compile(".+\\s+.+");
    private static final Pattern startsWithSlash
	= Pattern.compile(new StringBuilder().append("^\\").append
			      (Config.getPathSeparatorForRegexExpressions())
			      .append
			      (".*").toString());
    private static final Pattern endsWithSlash
	= Pattern.compile(new StringBuilder().append(".*\\").append
			      (Config.getPathSeparatorForRegexExpressions())
			      .append
			      ("$").toString());
    
    public static void listMatchingDirectories(CompleteOperation completion,
					       String possibleDir, File cwd) {
	List returnFiles = new ArrayList();
	if (possibleDir.trim().isEmpty()) {
	    List allFiles = listDirectory(cwd);
	    Iterator iterator = allFiles.iterator();
	    while (iterator.hasNext()) {
		String file = (String) iterator.next();
		if (file.startsWith(possibleDir))
		    returnFiles.add(Parser.switchSpacesToEscapedSpacesInWord
				    (file.substring(possibleDir.length())));
	    }
	    completion.addCompletionCandidates(returnFiles);
	} else if (!startsWithSlash.matcher(possibleDir).matches()
		   && new File(new StringBuilder().append
				   (cwd.getAbsolutePath()).append
				   (Config.getPathSeparator()).append
				   (possibleDir).toString()).isDirectory()) {
	    if (!endsWithSlash.matcher(possibleDir).matches()) {
		returnFiles.add("/");
		completion.addCompletionCandidates(returnFiles);
	    } else
		completion.addCompletionCandidates
		    (listDirectory(new File(new StringBuilder().append
						(cwd.getAbsolutePath()).append
						(Config.getPathSeparator())
						.append
						(possibleDir).toString())));
	} else if (new File(new StringBuilder().append
				(cwd.getAbsolutePath()).append
				(Config.getPathSeparator()).append
				(possibleDir).toString()).isFile()) {
	    returnFiles.add(" ");
	    completion.addCompletionCandidates(returnFiles);
	} else if (startsWithSlash.matcher(possibleDir).matches()
		   && new File(possibleDir).isFile()) {
	    returnFiles.add(" ");
	    completion.addCompletionCandidates(returnFiles);
	} else {
	    returnFiles = new ArrayList();
	    if (new File(possibleDir).isDirectory()
		&& !endsWithSlash.matcher(possibleDir).matches()) {
		returnFiles.add(Config.getPathSeparator());
		completion.addCompletionCandidates(returnFiles);
	    } else if (new File(possibleDir).isDirectory()
		       && !endsWithSlash.matcher(possibleDir).matches())
		completion.addCompletionCandidates
		    (listDirectory(new File(possibleDir)));
	    else {
		String lastDir = null;
		String rest = null;
		if (possibleDir.contains(Config.getPathSeparator())) {
		    lastDir
			= possibleDir.substring(0,
						(possibleDir.lastIndexOf
						 (Config.getPathSeparator())));
		    rest = (possibleDir.substring
			    (possibleDir.lastIndexOf(Config.getPathSeparator())
			     + 1));
		} else if (new File(new StringBuilder().append(cwd).append
					(Config.getPathSeparator()).append
					(possibleDir).toString()).exists())
		    lastDir = possibleDir;
		else
		    rest = possibleDir;
		List allFiles;
		if (startsWithSlash.matcher(possibleDir).matches())
		    allFiles
			= listDirectory(new File(new StringBuilder().append
						     (Config
							  .getPathSeparator())
						     .append
						     (lastDir).toString()));
		else if (lastDir != null)
		    allFiles
			= listDirectory(new File(new StringBuilder().append
						     (cwd).append
						     (Config
							  .getPathSeparator())
						     .append
						     (lastDir).toString()));
		else
		    allFiles = listDirectory(cwd);
		if (rest != null && !rest.isEmpty()) {
		    Iterator iterator = allFiles.iterator();
		    while (iterator.hasNext()) {
			String file = (String) iterator.next();
			if (file.startsWith(rest))
			    returnFiles.add
				(Parser.switchSpacesToEscapedSpacesInWord
				 (file.substring(rest.length())));
		    }
		} else {
		    Iterator iterator = allFiles.iterator();
		    while (iterator.hasNext()) {
			String file = (String) iterator.next();
			returnFiles.add
			    (Parser.switchSpacesToEscapedSpacesInWord(file));
		    }
		}
		if (returnFiles.size() > 1) {
		    String startsWith = Parser.findStartsWith(returnFiles);
		    if (startsWith != null && startsWith.length() > 0) {
			returnFiles.clear();
			returnFiles.add(Parser
					    .switchSpacesToEscapedSpacesInWord
					(startsWith));
		    } else {
			returnFiles.clear();
			Iterator iterator = allFiles.iterator();
			while (iterator.hasNext()) {
			    String file = (String) iterator.next();
			    if (file.startsWith(rest))
				returnFiles.add
				    (Parser.switchSpacesToEscapedSpacesInWord
				     (file));
			}
		    }
		}
		completion.addCompletionCandidates(returnFiles);
		if (returnFiles.size() > 1 && rest != null
		    && rest.length() > 0)
		    completion
			.setOffset(completion.getCursor() - rest.length());
	    }
	}
    }
    
    private static List listDirectory(File path) {
	List fileNames = new ArrayList();
	if (path != null && path.isDirectory()) {
	    File[] files = path.listFiles();
	    int i = files.length;
	    for (int i_0_ = 0; i_0_ < i; i_0_++) {
		File file = files[i_0_];
		fileNames.add(file.getName());
	    }
	}
	return fileNames;
    }
    
    public static String getDirectoryName(File path, File home) {
	if (path.getAbsolutePath().startsWith(home.getAbsolutePath()))
	    return new StringBuilder().append("~").append
		       (path.getAbsolutePath()
			    .substring(home.getAbsolutePath().length()))
		       .toString();
	return path.getAbsolutePath();
    }
    
    public static File getFile(String name, String cwd) {
	if (containParent.matcher(name).matches()) {
	    if (startsWithParent.matcher(name).matches()) {
		/* empty */
	    }
	} else if (!name.startsWith("~"))
	    return new File(name);
	return null;
    }
    
    public static void saveFile(File file, String text, boolean append)
	throws IOException {
	if (file.isDirectory())
	    throw new IOException(new StringBuilder().append(file).append
				      (": Is a directory").toString());
	if (file.isFile()) {
	    if (Main.isWindowsTerminal())
		text
		    = text.replace("\n", System.getProperty("line.separator"));
	    FileWriter fileWriter;
	    if (append)
		fileWriter = new FileWriter(file, true);
	    else
		fileWriter = new FileWriter(file, false);
	    fileWriter.write(text);
	    fileWriter.flush();
	    fileWriter.close();
	} else {
	    FileWriter fileWriter = new FileWriter(file, false);
	    fileWriter.write(text);
	    fileWriter.flush();
	    fileWriter.close();
	}
    }
    
    public static String readFile(File file) throws IOException {
	if (file.isDirectory())
	    throw new IOException(new StringBuilder().append(file).append
				      (": Is a directory").toString());
	if (file.isFile()) {
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    String string;
	    try {
		StringBuilder sb = new StringBuilder();
		for (String line = br.readLine(); line != null;
		     line = br.readLine())
		    sb.append(line).append(Config.getLineSeparator());
		string = sb.toString();
	    } catch (Object object) {
		br.close();
		throw object;
	    }
	    br.close();
	    return string;
	}
	throw new IOException(new StringBuilder().append(file).append
				  (": File unknown").toString());
    }
}
