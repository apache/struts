/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ClassPathFinderTest extends XWorkTestCase {

	public void testFinder() {
		ClassPathFinder finder = new ClassPathFinder();
		finder.setPattern("**/xwork-test-wildcard-*.xml");
		Vector<String> found = finder.findMatches();
		assertEquals(found.contains("com/opensymphony/xwork2/config/providers/xwork-test-wildcard-1.xml"), true );
		assertEquals(found.contains("com/opensymphony/xwork2/config/providers/xwork-test-wildcard-2.xml"), true );
		assertEquals(found.contains("com/opensymphony/xwork2/config/providers/xwork-test-wildcard-include.xml"), true );
		assertEquals(found.contains("com/opensymphony/xwork2/config/providers/xwork-test-results.xml"), false);
		
		ClassPathFinder finder2 = new ClassPathFinder();
		finder2.setPattern("com/*/xwork2/config/providers/xwork-test-wildcard-1.xml");
		Vector<String> found2 = finder2.findMatches();
		assertEquals(found2.contains("com/opensymphony/xwork2/config/providers/xwork-test-wildcard-1.xml"), true);
		assertEquals(found2.contains("com/opensymphony/xwork2/config/providers/xwork-test-wildcard-2.xml"), false);
		
		ClassPathFinder finder3 = new ClassPathFinder();
		finder3.setPattern("com/opensymphony/xwork2/config/providers/xwork-test-wildcard-1.xml");
		Vector<String> found3 = finder3.findMatches();
		assertEquals(found3.contains("com/opensymphony/xwork2/config/providers/xwork-test-wildcard-1.xml"), true);
		assertEquals(found3.contains("com/opensymphony/xwork2/config/providers/xwork-test-wildcard-2.xml"), false);
		
		ClassPathFinder finder4 = new ClassPathFinder();
		finder4.setPattern("no/matches/*");
		Vector<String> found4 = finder4.findMatches();
		assertEquals(found4.isEmpty(), true);
		
	}

    public void testFinderNotURLClassLoader() throws Exception {
        NotURLClassLoader loader = new NotURLClassLoader(Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(loader);

        try {
            Class<?> clazz = loader.loadClass(ClassPathFinderTest.class.getName());
            Object test = clazz.getConstructor().newInstance();

            clazz.getMethod("testFinder").invoke(test);
        } finally {
            Thread.currentThread().setContextClassLoader(loader.parentClassLoader);
        }
    }


	private class NotURLClassLoader extends ClassLoader {
        private Map<String, Class<?>> loadedClasses = new HashMap<>();
        private ClassLoader parentClassLoader;

        NotURLClassLoader(ClassLoader parentClassLoader) {
            super(null);
            this.parentClassLoader = parentClassLoader;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (!loadedClasses.containsKey(name)) {
                try {
                    byte[] classBits = IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream(
                            name.replace('.', '/') + ".class"));
                    loadedClasses.put(name, defineClass(name, classBits, 0, classBits.length));
                } catch (IOException e) {
                    throw new ClassNotFoundException("class " + name + " is not findable", e);
                } catch (Exception e) {
                    loadedClasses.put(name, parentClassLoader.loadClass(name));
                }
            }

            return loadedClasses.get(name);
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            return parentClassLoader.getResources(name);
        }
    }
}
