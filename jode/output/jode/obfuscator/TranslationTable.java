/* TranslationTable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class TranslationTable extends TreeMap
{
    public void load(InputStream inputstream) throws IOException {
	BufferedReader bufferedreader
	    = new BufferedReader(new InputStreamReader(inputstream));
	for (;;) {
	    String string;
	    IF ((string = bufferedreader.readLine()) == null)
		/* empty */
	    if (string.charAt(0) != '#') {
		int i = string.indexOf('=');
		String string_0_ = string.substring(0, i);
		String string_1_ = string.substring(i + 1);
		put(string_0_, string_1_);
	    }
	    continue;
	}
    }
    
    public void store(OutputStream outputstream) throws IOException {
	PrintWriter printwriter = new PrintWriter(outputstream);
	Iterator iterator = entrySet().iterator();
	for (;;) {
	    if (!iterator.hasNext())
		printwriter.flush();
	    Map.Entry entry = (Map.Entry) iterator.next();
	    printwriter.println(entry.getKey() + "=" + entry.getValue());
	}
    }
}
