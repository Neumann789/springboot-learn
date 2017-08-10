/* NestedClassesFinder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.classutils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.javosize.cli.Main;
import com.javosize.cli.StateHandler;
import com.javosize.cli.operations.LsCommand;

public class NestedClassesFinder
{
    public static List checkNestedClassesForDecompilation(String className)
	throws Exception {
	if (className.contains("$")) {
	    if (!Main.askForConfirmation
		 ("The requested class is a nested class. We cannot recover just the code of a nested class, do you want recover the enclosing class and all its nested? [y/n]"))
		return null;
	    className
		= new StringBuilder().append
		      (className.substring(0, className.indexOf("$"))).append
		      (".class").toString();
	}
	List classesToDecompile = getListOfClasses(className);
	classesToDecompile.remove(className);
	ArrayList returnList = new ArrayList();
	returnList.add(className);
	returnList.addAll(classesToDecompile);
	return returnList;
    }
    
    public static List getListOfClasses(String prefix) throws Exception {
	if (prefix.endsWith(".class"))
	    prefix = prefix.substring(0, prefix.lastIndexOf("."));
	String[] lsArgs
	    = { "ls",
		new StringBuilder().append(prefix).append("*").toString() };
	LsCommand ls = new LsCommand(lsArgs, false);
	List ids
	    = StateHandler.getAvailableIDs(ls.execute(Main.getStateHandler()),
					   "ls",
					   new StringBuilder().append
					       ("ls ").append
					       (prefix).toString(),
					   false);
	Iterator iterator = ids.iterator();
	while (iterator.hasNext()) {
	    String string = (String) iterator.next();
	    if (string.contains("$$Lambda$"))
		iterator.remove();
	}
	return ids;
    }
}
