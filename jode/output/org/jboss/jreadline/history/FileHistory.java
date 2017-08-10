/* FileHistory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.history;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jboss.jreadline.console.Config;

public class FileHistory extends InMemoryHistory
{
    private String historyFile;
    
    public FileHistory(String fileName, int maxSize) throws IOException {
	super(maxSize);
	historyFile = fileName;
	readFile();
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    {
		super();
	    }
	    
	    public void start() {
		try {
		    FileHistory.this.writeFile();
		} catch (Exception PUSH) {
		    Exception e = POP;
		    e.printStackTrace();
		}
	    }
	});
    }
    
    private void readFile() throws IOException {
	if (new File(historyFile).exists()) {
	    BufferedReader reader
		= new BufferedReader(new FileReader(historyFile));
	    for (;;) {
		String line;
		if ((line = reader.readLine()) == null) {
		    reader.close();
		    return;
		}
		push(line);
	    }
	}
	return;
    }
    
    private void writeFile() throws IOException {
	new File(historyFile).delete();
	FileWriter fw = new FileWriter(historyFile);
	int i = 0;
	for (;;) {
	    if (i >= size()) {
		fw.flush();
		fw.close();
	    }
	    fw.write(get(i) + Config.getLineSeparator());
	    i++;
	}
    }
}
