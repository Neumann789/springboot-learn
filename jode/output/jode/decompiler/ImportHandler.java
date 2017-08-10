/* ImportHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.IOException;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;

import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.type.ArrayType;
import jode.type.ClassInterfacesType;
import jode.type.NullType;
import jode.type.Type;

public class ImportHandler
{
    public static final int DEFAULT_PACKAGE_LIMIT = 2147483647;
    public static final int DEFAULT_CLASS_LIMIT = 1;
    SortedMap imports;
    Hashtable cachedClassNames = null;
    ClassAnalyzer main;
    String className;
    String pkg;
    int importPackageLimit;
    int importClassLimit;
    static Comparator comparator
	= new ANONYMOUS CLASS jode.decompiler.ImportHandler$1();
    
    public ImportHandler() {
	this(2147483647, 1);
    }
    
    public ImportHandler(int i, int i_0_) {
	importPackageLimit = i;
	importClassLimit = i_0_;
    }
    
    private boolean conflictsImport(String string) {
    label_852:
	{
	    int i = string.lastIndexOf('.');
	    if (i != -1) {
		String string_1_ = string.substring(0, i);
	    label_851:
		{
		    if (!string_1_.equals(pkg)) {
			string = string.substring(i);
			if (pkg.length() == 0) {
			    if (ClassInfo.exists(string.substring(1)))
				return true;
			} else if (ClassInfo.exists(pkg + string))
			    return true;
		    } else
			return false;
		}
		Iterator iterator = imports.keySet().iterator();
		while (iterator.hasNext()) {
		    String string_2_ = (String) iterator.next();
		    if (!string_2_.endsWith(".*")) {
			if (string_2_.endsWith(string)
			    || string_2_.equals(string.substring(1)))
			    return true;
		    } else {
			string_2_
			    = string_2_.substring(0, string_2_.length() - 2);
			if (!string_2_.equals(string_1_)
			    && ClassInfo.exists(string_2_ + string))
			    return true;
		    }
		    continue;
		}
		break label_851;
	    } else
		break label_852;
	    break label_852;
	}
	return false;
    }
    
    private void cleanUpImports() {
	Integer integer = new Integer(2147483647);
	TreeMap treemap = new TreeMap(comparator);
	LinkedList linkedlist = new LinkedList();
	Iterator iterator = imports.keySet().iterator();
	for (;;) {
	    if (!iterator.hasNext()) {
		imports = treemap;
		cachedClassNames = new Hashtable();
		iterator = linkedlist.iterator();
		for (;;) {
		    IF (!iterator.hasNext())
			/* empty */
		    String string = (String) iterator.next();
		    if (!conflictsImport(string)) {
			imports.put(string, integer);
			String string_3_
			    = string.substring(string.lastIndexOf('.') + 1);
			cachedClassNames.put(string, string_3_);
		    }
		    continue;
		}
	    }
	    String string = (String) iterator.next();
	    Integer integer_4_ = (Integer) imports.get(string);
	    if (string.endsWith(".*")) {
		if (integer_4_.intValue() >= importPackageLimit)
		    treemap.put(string, integer);
	    } else if (integer_4_.intValue() >= importClassLimit) {
		int i = string.lastIndexOf(".");
		if (i == -1) {
		    if (pkg.length() != 0)
			treemap.put(string, integer);
		} else if (!treemap.containsKey(string.substring(0, i) + ".*"))
		    linkedlist.add(string);
	    }
	    continue;
	}
    }
    
    public void dumpHeader(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.println("/* " + className + " - Decompiled by JODE");
	tabbedprintwriter.println(" * Visit http://jode.sourceforge.net/");
    label_853:
	{
	    tabbedprintwriter.println(" */");
	    if (pkg.length() != 0)
		tabbedprintwriter.println("package " + pkg + ";");
	    break label_853;
	}
	cleanUpImports();
	Iterator iterator = imports.keySet().iterator();
	String string = null;
	for (;;) {
	    if (!iterator.hasNext())
		tabbedprintwriter.println("");
	    String string_5_ = (String) iterator.next();
	    if (!string_5_.equals("java.lang.*")) {
	    label_855:
		{
		    int i = string_5_.indexOf('.');
		    if (i != -1) {
			String string_6_;
		    label_854:
			{
			    string_6_ = string_5_.substring(0, i);
			    if (string != null && !string.equals(string_6_))
				tabbedprintwriter.println("");
			    break label_854;
			}
			string = string_6_;
		    }
		    break label_855;
		}
		tabbedprintwriter.println("import " + string_5_ + ";");
	    }
	    continue;
	}
    }
    
    public void error(String string) {
	GlobalOptions.err.println(string);
    }
    
    public void init(String string) {
	imports = new TreeMap(comparator);
	imports.put("java.lang.*", new Integer(2147483647));
	int i = string.lastIndexOf('.');
    label_856:
	{
	    PUSH this;
	    if (i != -1)
		PUSH string.substring(0, i);
	    else
		PUSH "";
	    break label_856;
	}
	((ImportHandler) POP).pkg = POP;
    label_857:
	{
	    PUSH this;
	    if (i != -1)
		PUSH string.substring(i + 1);
	    else
		PUSH string;
	    break label_857;
	}
	((ImportHandler) POP).className = POP;
    }
    
    public void useClass(ClassInfo classinfo) {
	String string;
	Integer integer;
    label_860:
	{
	label_859:
	    {
		String string_7_;
	    label_858:
		{
		    for (;;) {
			InnerClassInfo[] innerclassinfos
			    = classinfo.getOuterClasses();
			if (innerclassinfos == null) {
			    string = classinfo.getName();
			    integer = (Integer) imports.get(string);
			    if (integer != null) {
				if (integer.intValue() >= importClassLimit)
				    break;
				integer = new Integer(integer.intValue() + 1);
			    } else {
				int i = string.lastIndexOf('.');
				if (i != -1) {
				    string_7_ = string.substring(0, i);
				    if (!string_7_.equals(pkg)) {
					Integer integer_8_
					    = ((Integer)
					       imports.get(string_7_ + ".*"));
					if (integer_8_ == null
					    || (integer_8_.intValue()
						< importPackageLimit)) {
					    if (integer_8_ != null)
						PUSH (new Integer
						      (integer_8_.intValue()
						       + 1));
					    else
						PUSH new Integer(1);
					    break label_858;
					}
				    }
				} else
				    break label_859;
				return;
			    }
			    break label_860;
			}
			if (innerclassinfos[0].name == null
			    || innerclassinfos[0].outer == null)
			    break;
			classinfo
			    = ClassInfo.forName(innerclassinfos[0].outer);
		    }
		    return;
		    break label_860;
		    break label_859;
		}
		Object object = POP;
		imports.put(string_7_ + ".*", object);
	    }
	    integer = new Integer(1);
	}
	imports.put(string, integer);
	break label_858;
    }
    
    public final void useType(Type type) {
	if (!(type instanceof ArrayType)) {
	    if (type instanceof ClassInterfacesType)
		useClass(((ClassInterfacesType) type).getClassInfo());
	} else
	    useType(((ArrayType) type).getElementType());
	return;
    }
    
    public String getClassString(ClassInfo classinfo) {
	String string = classinfo.getName();
    label_861:
	{
	    if (cachedClassNames != null) {
		String string_9_ = (String) cachedClassNames.get(string);
		if (string_9_ == null) {
		    int i = string.lastIndexOf('.');
		    if (i != -1) {
			String string_10_ = string.substring(0, i);
			if (string_10_.equals(pkg)
			    || (imports.get(string_10_ + ".*") != null
				&& !conflictsImport(string))) {
			    String string_11_ = string.substring(i + 1);
			    cachedClassNames.put(string, string_11_);
			    return string_11_;
			}
		    }
		} else
		    return string_9_;
	    } else
		return string;
	}
	cachedClassNames.put(string, string);
	return string;
	break label_861;
    }
    
    public String getTypeString(Type type) {
	if (!(type instanceof ArrayType)) {
	    if (!(type instanceof ClassInterfacesType)) {
		if (!(type instanceof NullType))
		    return type.toString();
		return "Object";
	    }
	    return getClassString(((ClassInterfacesType) type).getClassInfo());
	}
	return getTypeString(((ArrayType) type).getElementType()) + "[]";
    }
    
    protected int loadFileFlags() {
	return 1;
    }
}
