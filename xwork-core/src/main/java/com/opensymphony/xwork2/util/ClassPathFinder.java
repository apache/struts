/*
 * $Id$
 *
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.XWorkException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class is an utility class that will search through the classpath
 * for files whose names match the given pattern. The filename is tested
 * using the given implementation of {@link com.opensymphony.xwork2.util.PatternMatcher} by default it 
 * uses {@link com.opensymphony.xwork2.util.WildcardHelper}
 *
 * @version $Rev$ $Date$
 */
public class ClassPathFinder {
	
	/**
     * The String pattern to test against.
     */
	private String pattern ;
	
	private int[] compiledPattern ;
	
	/**
     * The PatternMatcher implementation to use
     */
	private PatternMatcher<int[]> patternMatcher = new WildcardHelper();
	
	private Vector<String> compared = new Vector<String>();
	
	/**
	 * retrieves the pattern in use
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * sets the String pattern for comparing filenames
	 * @param pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
     * Builds a {@link java.util.Vector} containing Strings which each name a file
     * who's name matches the pattern set by setPattern(String). The classpath is 
     * searched recursively, so use with caution.
     *
     * @return Vector<String> containing matching filenames
     */
	public Vector<String> findMatches() {
		Vector<String> matches = new Vector<String>();
		URLClassLoader cl = getURLClassLoader();
		if (cl == null ) {
			throw new XWorkException("unable to attain an URLClassLoader") ;
		}
		URL[] parentUrls = cl.getURLs();
		compiledPattern = (int[]) patternMatcher.compilePattern(pattern);
		for (URL url : parentUrls) {
			if (!"file".equals(url.getProtocol())) {
				continue ;
			}
			URI entryURI ;
			try {
				entryURI = url.toURI();
			} catch (URISyntaxException e) {
				continue;
			}
			File entry = new File(entryURI) ;
			Vector<String> results = checkEntries(entry.list(), entry, "");
			if (results != null ) {
				matches.addAll(results);
			}
		}
		return matches;
	}
	
	private Vector<String> checkEntries(String[] entries, File parent, String prefix) {
		
		if (entries == null ) {
			return null;
		}
		
		Vector<String> matches = new Vector<String>();
		for (String listEntry : entries) {
			File tempFile ;
			if (!"".equals(prefix) ) {
				tempFile = new File(parent, prefix + "/" + listEntry);
			}
			else {
				tempFile = new File(parent, listEntry);
			}
			if (tempFile.isDirectory() && 
					!(".".equals(listEntry) || "..".equals(listEntry)) ) {
				if	(!"".equals(prefix) ) {
					matches.addAll(checkEntries(tempFile.list(), parent, prefix + "/" + listEntry));
				}
				else {
					matches.addAll(checkEntries(tempFile.list(), parent, listEntry));
				}
			}
			else {
				
				String entryToCheck ;
				if ("".equals(prefix)) {
					entryToCheck = listEntry ;
				}
				else {
					entryToCheck = prefix + "/" + listEntry ;
				}
				
				if (compared.contains(entryToCheck) ) {
					continue;
				}
				else {
					compared.add(entryToCheck) ;
				}
				
				boolean doesMatch = patternMatcher.match(new HashMap<String,String>(), entryToCheck, compiledPattern);
				if (doesMatch) {
					matches.add(entryToCheck);
				}
			}
		}
		return matches ;
	}

	/**
	 * sets the PatternMatcher implementation to use when comparing filenames
	 * @param patternMatcher
	 */
	public void setPatternMatcher(PatternMatcher<int[]> patternMatcher) {
		this.patternMatcher = patternMatcher;
	}

	private URLClassLoader getURLClassLoader() {
		URLClassLoader ucl = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		if(! (loader instanceof URLClassLoader)) {
			loader = ClassPathFinder.class.getClassLoader();
			if (loader instanceof URLClassLoader) {
				ucl = (URLClassLoader) loader ;
			}
		}
		else {
			ucl = (URLClassLoader) loader;
		}
		
		return ucl ;
	}
}
