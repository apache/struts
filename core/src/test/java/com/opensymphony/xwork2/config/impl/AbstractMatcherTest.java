package com.opensymphony.xwork2.config.impl;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class AbstractMatcherTest extends TestCase {
	@SuppressWarnings({ "serial", "rawtypes" })
	private class AbstractMatcherImpl extends AbstractMatcher {
		@SuppressWarnings({ "unchecked" })
		public AbstractMatcherImpl() {
			super(null, false);
		}

		@Override
		protected Object convert(String path, Object orig, Map vars) {
			return null;
		}
	}

	public void testConvertParam() {
		AbstractMatcher<?> matcher = new AbstractMatcherImpl();
		Map<String, String> replacements = new HashMap<>();
		replacements.put("x", "something");
		replacements.put("y", "else");

		assertEquals("should return the original input", "blablablabla",
				matcher.convertParam("blablablabla", replacements));
		assertEquals("should replace x", "blasomethingblablabla",
				matcher.convertParam("bla{x}blablabla", replacements));
		assertEquals("should replace unknown values with empty string", "blablablabla",
				matcher.convertParam("bla{z}blablabla", replacements));
		assertEquals("should replace all occurrences, no mapping", "blasomethingblasomethingblabla",
				matcher.convertParam("bla{x}bla{x}blabla", replacements));
		assertEquals("should work for multiple different replacements", "blasomethingblaelseblabla",
				matcher.convertParam("bla{x}bla{y}bla{z}bla", replacements));
	}
}
