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
    }
    
    private void readAliasesFromFile(File aliasFile) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(aliasFile));
	try {
	    String line;
	    while ((line = br.readLine()) != null) {
		if (line.startsWith("alias")) {
		    try {
			parseAlias(line);
		    } catch (Exception exception) {
			/* empty */
		    }
		}
	    }
	} catch (Object object) {
	    br.close();
	    throw object;
	}
	br.close();
    }
    
    public void persist() {
	/* empty */
    }
    
    public void addAlias(String name, String value) {
	Alias alias = new Alias(name, value);
	if (aliases.contains(alias))
	    aliases.remove(alias);
	aliases.add(alias);
    }
    
    public String printAllAliases() {
	StringBuilder sb = new StringBuilder();
	Collections.sort(aliases);
	Iterator iterator = aliases.iterator();
	while (iterator.hasNext()) {
	    Alias a = (Alias) iterator.next();
	    sb.append("alias ").append(a.toString())
		.append(Config.getLineSeparator());
	}
	return sb.toString();
    }
    
    public Alias getAlias(String name) {
	int index = aliases.indexOf(new Alias(name, null));
	if (index > -1)
	    return (Alias) aliases.get(index);
	return null;
    }
    
    public List findAllMatchingNames(String name) {
	List names = new ArrayList();
	Iterator iterator = aliases.iterator();
	while (iterator.hasNext()) {
	    Alias a = (Alias) iterator.next();
	    if (a.getName().startsWith(name))
		names.add(a.getName());
	}
	return names;
    }
    
    public List getAllNames() {
	List names = new ArrayList();
	Iterator iterator = aliases.iterator();
	while (iterator.hasNext()) {
	    Alias a = (Alias) iterator.next();
	    names.add(a.getName());
	}
	return names;
    }
    
    public String removeAlias(String buffer) {
	if (buffer.trim().equals("unalias"))
	    return new StringBuilder().append
		       ("unalias: usage: unalias name [name ...]").append
		       (Config.getLineSeparator()).toString();
	buffer = buffer.substring("unalias".length()).trim();
	String[] strings = buffer.split(" ");
	int i = strings.length;
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    String s = strings[i_0_];
	    if (s != null) {
		Alias a = getAlias(s.trim());
		if (a != null)
		    aliases.remove(a);
		else
		    return new StringBuilder().append
			       (Settings.getInstance().getName()).append
			       (": unalias: ").append
			       (s).append
			       (": not found").append
			       (Config.getLineSeparator()).toString();
	    }
	}
	return null;
    }
    
    public String parseAlias(String buffer) {
	if (buffer.trim().equals("alias"))
	    return printAllAliases();
	Matcher aliasMatcher = aliasPattern.matcher(buffer);
	if (aliasMatcher.matches()) {
	    String name = aliasMatcher.group(2);
	    String value = aliasMatcher.group(3);
	    if (value.startsWith("'")) {
		if (value.endsWith("'"))
		    value = value.substring(1, value.length() - 1);
		else
		    return "alias: usage: alias [name[=value] ... ]";
	    } else if (value.startsWith("\"")) {
		if (value.endsWith("\""))
		    value = value.substring(1, value.length() - 1);
		else
		    return "alias: usage: alias [name[=value] ... ]";
	    }
	    if (name.contains(" "))
		return "alias: usage: alias [name[=value] ... ]";
	    addAlias(name, value);
	    return null;
	}
	Matcher listMatcher = listAliasPattern.matcher(buffer);
	if (listMatcher.matches()) {
	    StringBuilder sb = new StringBuilder();
	    String[] strings = listMatcher.group(2).trim().split(" ");
	    int i = strings.length;
	    for (int i_1_ = 0; i_1_ < i; i_1_++) {
		String s = strings[i_1_];
		if (s != null) {
		    Alias a = getAlias(s.trim());
		    if (a != null)
			sb.append("alias ").append(a.getName()).append
			    ("='").append
			    (a.getValue()).append
			    ("'").append(Config.getLineSeparator());
		    else
			sb.append(Settings.getInstance().getName()).append
			    (": alias: ").append
			    (s).append
			    (" : not found").append(Config.getLineSeparator());
		}
	    }
	    return sb.toString();
	}
	return null;
    }
}
