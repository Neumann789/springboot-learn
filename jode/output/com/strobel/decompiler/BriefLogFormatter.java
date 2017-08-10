/* BriefLogFormatter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

final class BriefLogFormatter extends Formatter
{
    private static final DateFormat format = new SimpleDateFormat("h:mm:ss");
    private static final String lineSep = System.getProperty("line.separator");
    
    public String format(LogRecord record) {
	String loggerName;
    label_1449:
	{
	    loggerName = record.getLoggerName();
	    if (loggerName == null)
		loggerName = "root";
	    break label_1449;
	}
	return (format.format(new Date(record.getMillis())) + " ["
		+ record.getLevel() + "] " + loggerName + ": "
		+ record.getMessage() + ' ' + lineSep);
    }
}
