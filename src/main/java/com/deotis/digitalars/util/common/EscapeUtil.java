package com.deotis.digitalars.util.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public class EscapeUtil {
	
	private final static Pattern escapePattern = Pattern.compile("'");
	private final static Pattern unescapePttern = Pattern.compile("&#39;");
	
	public static String escape(String dirty) {

		String clean = StringEscapeUtils.escapeHtml4(dirty);

		if (clean == null) {
			return null;
		}

		Matcher matcher = escapePattern.matcher(clean);

		if (matcher.find()) {
			return matcher.replaceAll("&#39;");
		}

		return clean;
	}


	public static String unescape(String clean) {

		String str = StringEscapeUtils.unescapeHtml4(clean);

		if (str == null) {
			return null;
		}

		Matcher matcher = unescapePttern.matcher(str);

		if (matcher.find()) {
			return matcher.replaceAll("'");
		}

		return str;
	}
	
	public static String escapeOrigin(String value) {
	    return value == null ? value : StringEscapeUtils.escapeHtml4(value);
	}

}