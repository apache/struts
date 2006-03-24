/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.quickstart;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.ArrayList;

/**
 * Integration with Jetty.
 *
 * @author patrick
 */
public class MultiDirClassLoader extends URLClassLoader {
    private ClassLoader parent;

    public MultiDirClassLoader(String[] dirs, String[] cps, ClassLoader parent) throws MalformedURLException {
        super(getAllURLs(dirs, cps), parent);
        this.parent = parent;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        Class aClass;

        try {
            aClass = parent.loadClass(name);
            if (aClass != null) {
                return aClass;
            }
        } catch (ClassNotFoundException e) {
            // ok, keep trying
        }

        return super.loadClass(name);
    }

    public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null && parent != null) {
            url = parent.getResource(name);
        }

        return url;
    }


    private static URL[] getAllURLs(String[] dirs, String[] cps) throws MalformedURLException {
        ArrayList urls = new ArrayList();

        for (int i = 0; i < cps.length; i++) {
            String cp = cps[i];
            urls.add(new File(cp).toURL());
        }

        for (int i = 0; i < dirs.length; i++) {
            String dir = dirs[i];
            File file = new File(dir);
            findJars(file, urls);
        }

        return (URL[]) urls.toArray(new URL[urls.size()]);
    }

    private static void findJars(File file, ArrayList fileList) throws MalformedURLException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                findJars(f, fileList);
            }
        }
        else if (file.getName().endsWith(".jar")) {
            // Manually exclude the local license file so that it's possible to run
            // clustering.
            if (!file.getName().equals("tangosol-license-local.jar")) {
                fileList.add(file.toURL());
            }
        }
    }
}
