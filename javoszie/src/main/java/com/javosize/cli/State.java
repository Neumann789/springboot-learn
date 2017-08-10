package com.javosize.cli;

import com.javosize.log.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public enum State {
	apps("/help/apps.txt"), appthreads("/help/appthreads.txt"), breakpoints("/help/breakpoints.txt"), classes(
			"/help/classes.txt"), custommetrics("/help/custommetrics.txt"), interceptor("/help/interceptor.txt"), jmx(
					"/help/jmx.txt"), memory("/help/memory.txt"), perfcounter("/help/perfcounters.txt"), problems(
							"/help/problems.txt"), repository("/help/repository.txt"), root(
									"/help/root.txt"), scheduler("/help/scheduler.txt"), sessions(
											"/help/sessions.txt"), sh("/help/sh.txt"), threads(
													"/help/threads.txt"), users("/help/users.txt");

	private String helpFileName;
	private static Log log = new Log(State.class.getName());

	private State(String helpFileName) {
		this.helpFileName = helpFileName;
	}

	public String getHelpFileName() {
		return this.helpFileName;
	}

	public String getHelpText() {
		String helpText = "";

		InputStream in = null;
		BufferedReader reader = null;
		try {
			char[] chr = new char['က'];

			in = getClass().getResourceAsStream(this.helpFileName);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer buffer = new StringBuffer();
			int len;
			while ((len = reader.read(chr)) > 0) {
				buffer.append(chr, 0, len);
			}
			return buffer.toString();
		} catch (Throwable th) {
			log.error("Error reading help text from " + this.helpFileName + ": " + th, th);
			helpText = "Unable to read\n";
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (Throwable localThrowable5) {
				}
			if (in != null)
				try {
					in.close();
				} catch (Throwable localThrowable6) {
				}
		}
		return helpText;
	}

	public String toString() {
		if (equals(root)) {
			return "";
		}
		return super.toString();
	}
}
