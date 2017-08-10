/* AliasManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.console.alias;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.jreadline.console.Config;
import org.jboss.jreadline.console.settings.Settings;

public class AliasManager
{
    private List aliases;
    private Pattern aliasPattern
	= Pattern.compile("^(alias)\\s+(\\w+)\\s*=\\s*(.*)$");
    private Pattern listAliasPattern
	= Pattern.compile("^(alias)((\\s+\\w+)+)$");
    private static final String ALIAS = "alias";
    private static final String ALIAS_SPACE = "alias ";
    private static final String UNALIAS = "unalias";
    
    public AliasManager(File aliasFile) throws IOException {
	aliases = new ArrayList();
	if (aliasFile != null && aliasFile.isFile())
	    readAliasesFromFile(aliasFile);
	return;
    }
    
    private void readAliasesFromFile(File aliasFile) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(aliasFile));
	for (;;) {
	    try {
		String line;
		if ((line = br.readLine()) != null) {
		    if (line.startsWith("alias")) {
			try {
			    parseAlias(line);
			} catch (Exception PUSH) {
			    Object object = POP;
			}
		    }
		}
	    } finally {
		Object object = POP;
		br.close();
		throw object;
	    }
	    br.close();
	}
    }
    
    public void persist() {
	/* empty */
    }
    
    public void addAlias(String name, String value) {
	Alias alias;
    label_233:
	{
	    alias = new Alias(name, value);
	    if (aliases.contains(alias))
		aliases.remove(alias);
	    break label_233;
	}
	aliases.add(alias);
    }
    
    public String printAllAliases() {
	StringBuilder sb = new StringBuilder();
	Collections.sort(aliases);
	Iterator iterator = aliases.iterator();
	for (;;) {
	    if (!iterator.hasNext())
		return sb.toString();
	    Alias a = (Alias) iterator.next();
	    sb.append("alias ").append(a.toString())
		.append(Config.getLineSeparator());
	}
    }
    
    public Alias getAlias(String name) {
	int index = aliases.indexOf(new Alias(name, null));
	if (index <= -1)
	    return null;
	return (Alias) aliases.get(index);
    }
    
    public List findAllMatchingNames(String name) {
	List names = new ArrayList();
	Iterator iterator = aliases.iterator();
	for (;;) {
	    if (!iterator.hasNext())
		return names;
	    Alias a = (Alias) iterator.next();
	    if (a.getName().startsWith(name))
		names.add(a.getName());
	    continue;
	}
    }
    
    public List getAllNames() {
	List names = new ArrayList();
	Iterator iterator = aliases.iterator();
	for (;;) {
	    if (!iterator.hasNext())
		return names;
	    Alias a = (Alias) iterator.next();
	    names.add(a.getName());
	}
    }
    
    public String removeAlias(String buffer) {
	if (!buffer.trim().equals("unalias")) {
	    buffer = buffer.substring("unalias".length()).trim();
	    String[] strings = buffer.split(" ");
	    int i = strings.length;
	    int i_0_ = 0;
	    for (;;) {
		if (i_0_ >= i)
		    return null;
	    label_234:
		{
		    String s = strings[i_0_];
		    if (s != null) {
			Alias a = getAlias(s.trim());
			if (a == null)
			    return (Settings.getInstance().getName()
				    + ": unalias: " + s + ": not found"
				    + Config.getLineSeparator());
			aliases.remove(a);
		    }
		    break label_234;
		}
		i_0_++;
	    }
	}
	return ("unalias: usage: unalias name [name ...]"
		+ Config.getLineSeparator());
    }
    
    public String parseAlias(String buffer) {
	String name;
	String value;
    label_235:
	{
	    if (!buffer.trim().equals("alias")) {
		Matcher aliasMatcher = aliasPattern.matcher(buffer);
		if (!aliasMatcher.matches()) {
		    Matcher listMatcher = listAliasPattern.matcher(buffer);
		    if (!listMatcher.matches())
			return null;
		    StringBuilder sb = new StringBuilder();
		    String[] strings = listMatcher.group(2).trim().split(" ");
		    int i = strings.length;
		    int i_1_ = 0;
		    for (;;) {
			if (i_1_ >= i)
			    return sb.toString();
		    label_236:
			{
			    String s = strings[i_1_];
			    if (s != null) {
				Alias a = getAlias(s.trim());
				if (a == null)
				    sb.append
					(Settings.getInstance().getName())
					.append
					(": alias: ").append
					(s).append
					(" : not found")
					.append(Config.getLineSeparator());
				else
				    sb.append("alias ").append(a.getName())
					.append
					("='").append
					(a.getValue()).append
					("'")
					.append(Config.getLineSeparator());
			    }
			    break label_236;
			}
			i_1_++;
		    }
		}
		name = aliasMatcher.group(2);
		value = aliasMatcher.group(3);
		if (!value.startsWith("'")) {
		    if (value.startsWith("\"")) {
			if (!value.endsWith("\""))
			    return "alias: usage: alias [name[=value] ... ]";
			value = value.substring(1, value.length() - 1);
		    }
		} else {
		    if (!value.endsWith("'"))
			return "alias: usage: alias [name[=value] ... ]";
		    value = value.substring(1, value.length() - 1);
		}
	    } else
		return printAllAliases();
	}
	if (!name.contains(" ")) {
	    addAlias(name, value);
	    return null;
	}
	return "alias: usage: alias [name[=value] ... ]";
	break label_235;
    }
}
