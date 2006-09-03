/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts2.quickstart;

import org.mortbay.util.Resource;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 */
public class MultiDirResource extends Resource {
	
	private static final long serialVersionUID = -7571068340424106599L;
	
	MultiWebApplicationContext ctx;
    File[] files;
    String uri;

    public MultiDirResource(MultiWebApplicationContext ctx, String uri, List pathPriority, Map paths) {
        this.ctx = ctx;
        this.uri = uri;
        ArrayList files = new ArrayList();
        for (Iterator iterator = pathPriority.iterator(); iterator.hasNext();) {
            String path = (String) iterator.next();
            List dirs = (List) paths.get(path);

            if (uri.startsWith(path) || (uri.equals("") && path.equals("/"))) {
                for (Iterator iterator1 = dirs.iterator(); iterator1.hasNext();) {
                    String s = (String) iterator1.next();

                    if (uri.startsWith(path)) {
                        // cut off the path from the start of the URI
                        files.add(new File(s, uri.substring(path.length())));
                    } else {
                        files.add(new File(s, uri));
                    }
                }
            }
        }

        this.files = (File[]) files.toArray(new File[files.size()]);
    }

    public void release() {
    }

    public boolean exists() {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return true;
            }
        }

        return false;
    }

    public boolean isDirectory() {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return file.isDirectory();
            }
        }

        return false;
    }

    public long lastModified() {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return file.lastModified();
            }
        }

        return 0;
    }

    public long length() {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return file.length();
            }
        }

        return 0;
    }

    public URL getURL() {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                try {
                    return file.toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public File getFile() throws IOException {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return file;
            }
        }

        return null;
    }

    public String getName() {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return file.getName();
            }
        }

        return null;
    }

    public InputStream getInputStream() throws IOException {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return new FileInputStream(file);
            }
        }

        return null;
    }

    public OutputStream getOutputStream() throws IOException, SecurityException {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return new FileOutputStream(file);
            }
        }

        return null;
    }

    public boolean delete() throws SecurityException {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                return file.delete();
            }
        }

        return false;
    }

    public boolean renameTo(Resource resource) throws SecurityException {
        return false;
    }

    public String[] list() {
        HashSet set = new HashSet();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                String[] files = file.list();
                for (int j = 0; j < files.length; j++) {
                    String s = files[j];
                    set.add(s);
                }
            }
        }

        return (String[]) set.toArray(new String[set.size()]);
    }

    public Resource addPath(String string) throws IOException, MalformedURLException {
        return ctx.newResolver(uri + string);
    }
}
