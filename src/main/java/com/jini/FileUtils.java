package com.jini;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jini.server.MessageBox;

public class FileUtils {
	public static String readToString(File file) throws FileNotFoundException,
			IOException {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			StringBuffer buffer = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			return buffer.toString();
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}
	}

	public static void writeToFile(File file, String content) throws Exception {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(content);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	public static List<File> listFiles(File directory, boolean recursive) {
		if (directory.isFile())
			throw new IllegalArgumentException("Invalid Directory");
		List<File> allFiles = new ArrayList<File>();
		File[] listFiles = directory.listFiles();
		for (File file : listFiles) {
			if (file.isFile())
				allFiles.add(file);
			if (file.isDirectory() && recursive) {
				if (file.getName().startsWith("."))
					continue;
				allFiles.addAll(listFiles(file, recursive));
			}
		}
		return allFiles;
	}

	public static void copyFiles(File srcFile, File destFile) throws Exception {
		destFile.getParentFile().mkdirs();
		InputStream fileReader = new FileInputStream(srcFile);
		byte[] buffer = new byte[1024];
		int len = 0;
		FileOutputStream os = new FileOutputStream(destFile);
		try {
			while ((len = fileReader.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
		} finally {
			os.flush();
			os.close();
		}
	}

	public static void main(String[] args) throws Exception {
		copyFiles(new File(
				"/home/nitish/projects/HTML-2.0/images/avatar-img.png"),
				new File("/home/nitish/projects/build/images/avatar-img.png"));
	}
}
