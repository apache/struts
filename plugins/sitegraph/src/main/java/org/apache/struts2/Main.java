/*
 * $Id$
 *
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

package org.apache.struts2;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Struts comes with various related tools included in the struts-core-VERSION.jar file. You can access these
 * tools by simply unpacking the Struts distribution and running <b>java -jar struts-core-VERSION.jar</b>.
 * Struts will automatically include all jars in the same directory as the struts-core-VERSION.jar file as well as all
 * jars in the <i>lib</i> directory. This means you can invoke these tools either from within the standard directory
 * structure found in the Struts distribution, or from within your WEB-INF/lib directory.
 *
 * <p/> You can access the help information for these tools by simply running the jar without any arguments.
 *
 * <!-- END SNIPPET: javadoc -->
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("  java -jar struts-toolbox.jar [command] (optional command args)");
            System.out.println("");
            System.out.println("Where [command] is one of the following:");
            System.out.println("  sitegraph");
            System.out.println("  sitegraph:xxx");
            System.out.println("");
            System.out.println("Execute the commands for additional usage instructions.");
            System.out.println("Note: the *:xxx commands are just shortcuts for ");
            System.out.println("      running the command on a webapp in the apps dir.");
            return;
        }


        String javaHome = System.getProperty("java.home");
        ArrayList<URL> urls = new ArrayList<URL>();
        try {
            findJars(new File("lib"), urls);

            // use all the jars in the current that aren't the src jar
            File wd = new File(".");
            File[] jars = wd.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar") && !name.contains("-src.");
                }
            });
            for (File jar : jars) {
                urls.add(jar.toURL());
            }

            // ... but there might not be any (ie: we're in development in IDEA), so use this as backup
            urls.add(new File(System.getProperty("struts.classes", "build/java")).toURL());
            urls.add(new File(System.getProperty("xwork.classes", "../xwork/build/java/")).toURL());

            // load tools.jar from JAVA_HOME
            File tools = new File(javaHome, "lib/tools.jar");
            if (!tools.exists()) {
                // hmm, not there, how about java.home?
                tools = new File(javaHome, "../lib/tools.jar");
            }
            if (!tools.exists()) {
                // try the OS X common path
                tools = new File(javaHome, "../Classes/classes.jar");
            }
            if (!tools.exists()) {
                // try the other OS X common path
                tools = new File(javaHome, "../Classes/classes.jar");
            }
            if (!tools.exists()) {
                // did the user specify it by hand?
                String prop = System.getProperty("tools");
                if (prop != null) {
                    tools = new File(prop);
                }
            }
            if (!tools.exists()) {
                System.out.println("Error: Could not find tools.jar! Please do one of the following: ");
                System.out.println("");
                System.out.println("        - Use the JDK's JVM (ie: c:\\jdk1.5.0\\bin\\java)");
                System.out.println("        - Specify JAVA_HOME to point to your JDK 1.5 home");
                System.out.println("        - Specify a direct path to tools.jar via, as shown below:");
                System.out.println("");
                System.out.println("       java -Dtools=/path/to/tools.jar -jar struts.jar ...");
                return;
            }

            // finally, add the verified tools.jar
            urls.add(tools.toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Could not find URLs -- see stack trace.");
        }

        String command = args[0];
        String[] programArgs = new String[args.length - 1];
        System.arraycopy(args, 1, programArgs, 0, programArgs.length);

        if (command.startsWith("sitegraph:")) {
            command = "sitegraph";
            String name = checkWebAppArgs(args);
            programArgs = new String[]{"-config", "apps/" + name + "/src/webapp/WEB-INF/classes",
                    "-views", "apps/" + name + "/src/webapp",
                    "-output", "."};
        }

        if ("sitegraph".equals(command)) {
            launch("org.apache.struts2.sitegraph.SiteGraph", programArgs, urls);
        }
    }

    private static String checkWebAppArgs(String[] args) {
        int colon = args[0].indexOf(':');
        String name = null;
        try {
            name = args[0].substring(colon + 1);
        } catch (Exception e) {
            //this is OK to skip
        }
        if (name == null || name.equals("")) {
            System.out.println("Error: you must specify the webapp you wish");
            System.out.println("       to deploy. The webapp name must be the");
            System.out.println("       name of the directory found in apps/.");
            System.out.println("");
            System.out.println("Example: java -jar struts-core-VERSION.jar quickstart:sandbox");
            System.exit(1);
        }

        return name;
    }

    private static void launch(String program, String[] programArgs, List<URL> urls) {
        Collections.reverse(urls);
        URL[] urlArray = urls.toArray(new URL[urls.size()]);
        URLClassLoader cl = new MainClassLoader(urlArray);
        Thread.currentThread().setContextClassLoader(cl);
        try {
            Class clazz = cl.loadClass(program);
            Method main = clazz.getDeclaredMethod("main", new Class[]{String[].class});
            main.invoke(null, new Object[]{programArgs});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void findJars(File file, ArrayList<URL> urls) throws MalformedURLException {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (f.isDirectory()) {
                findJars(f, urls);
            } else if (f.getName().endsWith(".jar")) {
                if (isValid(f.getName())) {
                    urls.add(f.toURL());
                }
            }
        }
    }

    private static boolean isValid(String name) {
        return !"dom.jar".equals(name);
    }

    /**
     * Reverses the typical order of classloading to defer only to the parent if the current class loader can't be
     * found. This is required to allow for the launcher to be embedded within struts.jar (otherwise the dependencies
     * wouldn't be found by the system ClassLoader when invoking using "java -jar struts-core-VERSION.jar ...").
     */
    public static class MainClassLoader extends URLClassLoader {
        public MainClassLoader(URL[] urls) {
            super(urls);
        }

        public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.xml.") || name.startsWith("org.w3c.")
                    || name.startsWith("java.") || name.startsWith("javax.")
                    || name.startsWith("sun.") || name.startsWith("com.sun.")) {
                return super.loadClass(name, resolve);
            }

            ClassLoader parent = getParent();
            // First, check if the class has already been loaded
            Class c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                } catch (Throwable t) {
                    // If still not found, only then ask the parent
                    c = parent.loadClass(name);
                }
            }
            if (resolve) {
                resolveClass(c);
            }

            return c;
        }
    }
}
