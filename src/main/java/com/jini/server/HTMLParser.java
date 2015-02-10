package com.jini.server;

import com.jini.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class HTMLParser {
	private File resourceFile;

	public void setResourceFile(File resourceFile) {
		this.resourceFile = resourceFile;
	}

	public String parse() throws Exception {
		
		String resolvePartials = resolvePartials();
		Document doc = null;
		boolean partial = false;
		if (this.resourceFile.getName().startsWith("_")) {
			doc = Jsoup.parseBodyFragment(resolvePartials);
			partial = true;
		} else {
			doc = Jsoup.parse(resolvePartials);
			partial = false;
		}
		Elements repeatables = doc.getElementsByAttribute("repeat");
		while (!repeatables.isEmpty()) {
			Element element = repeatables.get(0);
			String countStr = element.attr("repeat");
			int count = Integer.parseInt(countStr);
			element.removeAttr("repeat");
			element.addClass("repeated");
			for (int i = 0; i < count - 1; i++) {
				element.after(element.outerHtml());
			}
			repeatables = doc.getElementsByAttribute("repeat");
		}
		Elements allElements = doc.getAllElements();

		Elements children = doc.body().children();
		for (Element element : children) {
			resolveLayout(doc, element);
		}

		for (Element element : allElements) {
			if (element.hasAttr("style")) {
				String attr = element.attr("style");
				Pattern compile = Pattern.compile("([^:]+):[^;]+");
				Matcher matcher = compile.matcher(attr);
				if (matcher.find()) {
					String prop = matcher.group(1).trim();
					if (!prop.equals("display")) {
						System.err.println("Inline Styling for " + element);
					}
				}
				attr = attr + "border:5px solid black;";
			}
			if (element.tagName().equals("lorem")) {
				if ((!element.hasAttr("type"))
						|| (element.attr("type").equals("text"))) {
					if (element.hasAttr("length")) {
						String attr = element.attr("length");
						int len = Integer.parseInt(attr);
						element.replaceWith(new TextNode(Lorem.getInstance()
								.word(len), ""));
					} else {
						element.replaceWith(new TextNode(Lorem.getInstance()
								.full(), ""));
					}
				}
				if ((element.hasAttr("type"))
						&& (element.attr("type").equals("name"))) {
					element.replaceWith(new TextNode(
							Lorem.getInstance().name(), ""));
				}
				if ((element.hasAttr("type"))
						&& (element.attr("type").equals("firstname"))) {
					element.replaceWith(new TextNode(Lorem.getInstance()
							.firstName(), ""));
				}
				if ((element.hasAttr("type"))
						&& (element.attr("type").equals("lastname"))) {
					element.replaceWith(new TextNode(Lorem.getInstance()
							.lastName(), ""));
				}
				if ((element.hasAttr("type"))
						&& (element.attr("type").equals("image"))) {
					Element imgTag = doc.createElement("img");
					int width = 100;
					if (element.hasAttr("width")) {
						width = Integer.parseInt(element.attr("width"));
					}
					int height = 100;
					if (element.hasAttr("height")) {
						height = Integer.parseInt(element.attr("height"));
					}
					if (element.hasAttr("class")) {
						imgTag.attr("class", element.attr("class"));
					}
					imgTag.attr("width", width + "px");
					imgTag.attr("height", height + "px");
					imgTag.attr("src",
							Lorem.getInstance().getImageURL(width, height));

					element.replaceWith(imgTag);
				}
			}
		}
		if (partial) {
			return doc.body().html();
		}
		return doc.html();
	}

	protected void resolveLayout(Document doc, Element element) {
		if (element.nodeName().equals("vbox")) {
			Element div = doc.createElement("div");
			
			div.insertChildren(0, element.children());
			if (element.hasText()) {
				div.text(element.text());
			}
			Attributes attributes = element.attributes();
			for (Attribute attribute : attributes) {
				div.attr(attribute.getKey(), attribute.getValue());
			}
			div.addClass("clearfix");
			element.replaceWith(div);
			resolveAlignment(div);
			if (!div.children().isEmpty()) {
				for (Element ch : div.children()) {
					resolveLayout(doc, ch);
				}
			}
		} else if (element.nodeName().equals("hbox")) {
			Element div = doc.createElement("div");
			div.insertChildren(0, element.children());
			if (element.hasText()) {
				div.text(element.text());
			}
			Attributes attributes = element.attributes();
			for (Attribute attribute : attributes) {
				div.attr(attribute.getKey(), attribute.getValue());
			}
			div.addClass("clearfix");
			element.replaceWith(div);
			resolveAlignment(div);
			if (!div.children().isEmpty()) {
				for (Element ch : div.children()) {
					resolveLayout(doc, ch);
					ch.addClass("pull-left");
					if(ch.hasAttr("col"))
					{
						String col = ch.attr("col");
						int parseInt = Integer.parseInt(col);
						ch.addClass("col-lg-" + parseInt);
					}
				}
			}
		} else {
			if (!element.children().isEmpty()) {
				for (Element ch : element.children()) {
					resolveLayout(doc, ch);
				}
			}
		}
	}

	protected void resolveAlignment(Element elem){
		if(elem.hasAttr("align")){
			if(elem.attr("align").equals("right")){
				elem.addClass("pull-right");
			}
			if(elem.attr("align").equals("center")){
				elem.addClass("center");
			}
		}
	}
	
	protected String resolvePartials() throws FileNotFoundException,
			IOException, Exception {
		PartialCombiner combiner = new PartialCombiner();
		combiner.setSourceFile(this.resourceFile);
		String resolvePartials = combiner.resolvePartials();

		return resolvePartials;
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
