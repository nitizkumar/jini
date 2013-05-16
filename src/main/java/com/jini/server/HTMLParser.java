package com.jini.server;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jini.FileUtils;

public class HTMLParser {

	private File resourceFile;

	public void setResourceFile(File resourceFile) {
		this.resourceFile = resourceFile;
	}

	public String parse() throws Exception {
		String readToString = FileUtils.readToString(resourceFile);
		Pattern pattern = Pattern.compile("(<%=\\s(partial)\\s\"([^%]+)?%>)",
				Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(readToString);
		// System.out.println(readToString);
		while (matcher.find()) {
			String group = matcher.group(3);
			String replaceAll = null;
			if (group.matches("([^/]+)/([^/]+)")) {
				Matcher matcher2 = Pattern.compile("([^/]+)/([^/]+)").matcher(
						group);
				boolean find = matcher2.find();
				replaceAll = group.replaceAll("([^/]+)/([^/]+)\"\\s$",
						"$1/_$2.html");
				// System.out.println(replaceAll);
			} else {
				Matcher matcher2 = Pattern.compile("(^[/]+)").matcher(group);
				boolean find = matcher2.find();
				replaceAll = group.replaceAll("(.*)\"\\s$", "_$1.html");
				// System.out.println(replaceAll);
			}
			String partialContent = FileUtils.readToString(new File(
					resourceFile.getParent(), replaceAll));
			// readToString = matcher.replaceFirst(partialContent);
			readToString = readToString.replace(matcher.group(0),
					partialContent);
			matcher = pattern.matcher(readToString);
			// System.out.println(readToString);
		}
		return readToString;
	}

	public static void main(String[] args) throws Exception {
		HTMLParser htmlParser = new HTMLParser();
		String parse = htmlParser.parse();
		FileUtils
				.writeToFile(
						new File(
								"/media/9C1CA03D1CA01474/projects/solver-middleman/build/index.html"),
						parse);
	}
}
