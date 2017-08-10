/* Main - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Random;

import jode.GlobalOptions;
import jode.util.Option;
import jode.util.ProcessOptions;

public class Main
{
    public static boolean swapOrder = false;
    public static final int OPTION_STRONGOVERLOAD = 1;
    public static final int OPTION_PRESERVESERIAL = 2;
    public static int options = 2;
    private static final Option[] longOptions
	= { new Option("cp", 1, 99), new Option("classpath", 1, 99),
	    new Option("destpath", 1, 100), new Option("help", 0, 104),
	    new Option("version", 0, 86), new Option("verbose", 2, 118),
	    new Option("debug", 2, 68) };
    public static final String[] stripNames
	= { "unreach", "inner", "lvt", "lnt", "source" };
    public static final int STRIP_UNREACH = 1;
    public static final int STRIP_INNERINFO = 2;
    public static final int STRIP_LVT = 4;
    public static final int STRIP_LNT = 8;
    public static final int STRIP_SOURCE = 16;
    public static int stripping = 0;
    public static Random rand = new Random(123456L);
    private static ClassBundle bundle;
    
    public static void usage() {
	PrintWriter printwriter = GlobalOptions.err;
	printwriter.println("usage: jode.Obfuscator flags* script");
	printwriter.println("  -h, --help           show this information.");
	printwriter.println
	    ("  -V, --version        output version information and exit.");
	printwriter.println
	    ("  -v, --verbose        be verbose (multiple times means more verbose).");
	printwriter.println
	    ("  -c, --classpath <path> search for classes in specified classpath.");
	printwriter.println
	    ("                       The directories should be separated by ','.");
	printwriter.println
	    ("  -d, --dest <dir>     write decompiled files to disk into directory destdir.");
	printwriter.println
	    ("  -D, --debug=...      use --debug=help for more information.");
    }
    
    public static ClassBundle getClassBundle() {
	return bundle;
    }
    
    public static void main(String[] strings) {
    label_1021:
	{
	label_1022:
	    {
		String string;
		String string_0_;
	    label_1020:
		{
		    String string_1_;
		label_1019:
		    {
			if (strings.length != 0) {
			    string = null;
			    string_0_ = null;
			    GlobalOptions.err.println
				("Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>");
			    bundle = new ClassBundle();
			    boolean bool = false;
			    ProcessOptions processoptions
				= new ProcessOptions(strings, longOptions);
			    int i = processoptions.getOption();
			    for (;;) {
				if (i == -1) {
				    if (!bool) {
					if (processoptions.getPosition()
					    == strings.length - 1) {
					    try {
						string_1_
						    = (strings
						       [processoptions
							    .getPosition()]);
						PUSH new ScriptParser;
						DUP
						if (!string_1_.equals("-"))
						    break label_1019;
					    } catch (java.io.IOException PUSH) {
						break label_1021;
					    } catch (ParseException PUSH) {
						break label_1022;
					    }
					    try {
						PUSH (new InputStreamReader
						      (System.in));
					    } catch (java.io.IOException PUSH) {
						break;
					    } catch (ParseException PUSH) {
						break label_1022;
					    }
					} else {
					    GlobalOptions.err.println
						("You must specify exactly one script.");
					    return;
					}
					break label_1020;
				    }
				    return;
				}
				switch (i) {
				case 104:
				    usage();
				    bool = true;
				    break;
				case 86:
				    GlobalOptions.err.println("1.1.2-pre1");
				    break;
				case 99:
				    string
					= processoptions.getOptionArgument();
				    break;
				case 100:
				    string_0_
					= processoptions.getOptionArgument();
				    break;
				case 118: {
				    String string_2_
					= processoptions.getOptionArgument();
				    if (string_2_ != null) {
					try {
					    GlobalOptions.verboseLevel
						= Integer.parseInt(string_2_);
					} catch (NumberFormatException PUSH) {
					    Object object = POP;
					    GlobalOptions.err.println
						("jode.obfuscator.Main: Argument `"
						 + string_2_
						 + "' to --verbose must be numeric:");
					    bool = true;
					}
				    } else
					GlobalOptions.verboseLevel++;
				    break;
				}
				case 68: {
				    String string_3_;
				label_1017:
				    {
					string_3_ = processoptions
							.getOptionArgument();
					if (string_3_ == null)
					    string_3_ = "help";
					break label_1017;
				    }
				label_1018:
				    {
					PUSH bool;
					if (GlobalOptions
						.setDebugging(string_3_))
					    PUSH false;
					else
					    PUSH true;
					break label_1018;
				    }
				    bool = POP | POP;
				    break;
				}
				default:
				    bool = true;
				    break;
				case 0:
				}
				i = processoptions.getOption();
			    }
			    break label_1021;
			    break label_1020;
			} else {
			    usage();
			    return;
			}
		    }
		    try {
			PUSH new FileReader(string_1_);
		    } catch (java.io.IOException PUSH) {
			break label_1021;
		    } catch (ParseException PUSH) {
			break label_1022;
		    }
		}
	    label_1024:
		{
		label_1023:
		    {
			try {
			    ((UNCONSTRUCTED)POP).ScriptParser(POP);
			    ScriptParser scriptparser = POP;
			    scriptparser.parseOptions(bundle);
			} catch (java.io.IOException PUSH) {
			    /* empty */
			} catch (ParseException PUSH) {
			    /* empty */
			}
			if (string != null)
			    bundle.setOption("classpath",
					     Collections.singleton(string));
			break label_1023;
		    }
		    if (string_0_ != null)
			bundle.setOption("dest",
					 Collections.singleton(string_0_));
		    break label_1024;
		}
		bundle.run();
		return;
	    }
	    ParseException parseexception = POP;
	    GlobalOptions.err.println("Syntax error in script file: ");
	    GlobalOptions.err.println(parseexception.getMessage());
	    if (GlobalOptions.verboseLevel > 5)
		parseexception.printStackTrace(GlobalOptions.err);
	    return;
	}
	java.io.IOException ioexception = POP;
	GlobalOptions.err.println("IOException while reading script file.");
	ioexception.printStackTrace(GlobalOptions.err);
	break label_1019;
    }
}
