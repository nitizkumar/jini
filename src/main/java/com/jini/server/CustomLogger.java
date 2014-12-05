package com.jini.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CustomLogger {

	private static CustomLogger instance = new CustomLogger();

	private File logfile;

	private BufferedWriter writer;

	private CustomLogger() {

	}

	public static CustomLogger getInstance() {
		return instance;
	}

	public void setLogFile(File logfile) {
		try {
			this.logfile = logfile;
			if (!this.logfile.exists())
				this.logfile.getParentFile().mkdirs();
			writer = new BufferedWriter(new FileWriter(this.logfile,true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void log(String message) {
		try {
			this.writer.write(message);
			this.writer.write("\n");
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
