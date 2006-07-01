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

import com.thoughtworks.xstream.XStream;
import org.apache.commons.collections.MultiHashMap;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Configuration for the QuickStart program.
 */
public class Configuration implements Serializable {

	private static final long serialVersionUID = 9159115401614443449L;

	String ideaConfig;
    String extendsConfig;
    String resolver;
    Integer port;
    String context;
    List libs;
    List classDirs;
    List sources;
    List webDirs;
    Map mappings;
    List pathPriority;

    public String getIdeaConfig() {
        return ideaConfig;
    }

    public void setIdeaConfig(String ideaConfig) {
        this.ideaConfig = ideaConfig;
    }

    public String getExtendsConfig() {
        return extendsConfig;
    }

    public void setExtendsConfig(String extendsConfig) {
        this.extendsConfig = extendsConfig;
    }

    public String getResolver() {
        return resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

    public List getLibs() {
        return libs;
    }

    public void setLibs(List libs) {
        this.libs = libs;
    }

    public List getClassDirs() {
        return classDirs;
    }

    public void setClassDirs(List classDirs) {
        this.classDirs = classDirs;
    }

    public List getSources() {
        return sources;
    }

    public void setSources(List sources) {
        this.sources = sources;
    }

    public Map getMappings() {
        return mappings;
    }

    public List getPathPriority() {
        return pathPriority;
    }

    public List getWebDirs() {
        return webDirs;
    }

    public void setWebDirs(List webDirs) {
        this.webDirs = webDirs;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void resolveDirs(String wd) {
        if (ideaConfig != null) {
            String[] parts = ideaConfig.split(",");
            for (int i = 0; i < parts.length; i++) {
                String full = resolveDir(parts[i], wd);

                try {
                    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = db.parse(full);
                    NodeList components = doc.getElementsByTagName("root");
                    List jars = new ArrayList();
                    for (int j = 0; j < components.getLength(); j++) {
                        Element e = (Element) components.item(j);
                        String value = e.getAttribute("url");
                        if (value != null && value.startsWith("jar://") && value.endsWith(".jar!/")) {
                            value = value.substring(6, value.length() - 2);
                            if (value.startsWith("$MODULE_DIR$")) {
                                value = value.substring(13);
                            }
                            jars.add(value);
                        }
                    }

                    if (this.libs != null) {
                        this.libs.addAll(jars);
                    } else {
                        this.libs = jars;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        resolve(this.libs, wd);
        resolve(this.classDirs, wd);
        resolve(this.sources, wd);

        // now resolve the web dirs
        for (Iterator iterator = webDirs.iterator(); iterator.hasNext();) {
            Mapping mapping = (Mapping) iterator.next();
            String path = mapping.getPath();
            String dir = mapping.getDir();
            dir = resolveDir(dir, wd);

            if (this.mappings == null) {
                this.mappings = new MultiHashMap();
                this.pathPriority = new ArrayList();
            }

            if (!this.pathPriority.contains(path)) {
                this.pathPriority.add(path);
            }
            this.mappings.put(path, dir);
        }
    }

    private void resolve(List list, String wd) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String s = (String) list.get(i);
                list.set(i, resolveDir(s, wd));
            }
        }
    }

    private String resolveDir(String dir, String wd) {
        File file = new File(wd, dir);
        if (!file.exists() && new File(dir).exists()) {
            file = new File(dir);
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    public void resolveExtensions(String wd, XStream xstream) throws FileNotFoundException {
        if (extendsConfig != null) {
            File config = new File(wd, extendsConfig);
            Configuration c = (Configuration) xstream.fromXML(new FileReader(config));
            c.resolveDirs(config.getParent());
            c.resolveExtensions(config.getParent(), xstream);

            // now copy over the props
            if (c.getResolver() != null) {
                this.resolver = c.getResolver();
            }

            if (port == null) {
                this.port = c.getPort();
            }

            if (c.getContext() != null) {
                this.context = c.getContext();
            }

            if (c.getLibs() != null) {
                if (this.libs != null) {
                    this.libs.addAll(c.getLibs());
                } else {
                    this.libs = c.getLibs();
                }
            }

            if (c.getClassDirs() != null) {
                if (this.classDirs != null) {
                    this.classDirs.addAll(c.getClassDirs());
                } else {
                    this.classDirs = c.getClassDirs();
                }
            }

            if (c.getSources() != null) {
                if (this.sources != null) {
                    this.sources.addAll(c.getSources());
                } else {
                    this.sources = c.getSources();
                }
            }

            for (Iterator iterator = c.getMappings().entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                List list = (List) this.mappings.get(entry.getKey());
                if (list != null) {
                    list.addAll((List) entry.getValue());
                } else {
                    this.mappings.put(entry.getKey(), (List) entry.getValue());
                }
            }

            // add only new paths
            for (Iterator iterator = c.getPathPriority().iterator(); iterator.hasNext();) {
                String path = (String) iterator.next();
                if (!this.pathPriority.contains(path)) {
                    this.pathPriority.add(path);
                }
            }
        }
    }

    public boolean validate() {
        boolean error = false;

        if (port == null) {
            System.out.println("Port must be greater than 0");
            error = true;
        }

        if (!context.startsWith("/")) {
            System.out.println("Context must start with /");
            error = true;
        }


        if (verifyList("Library", libs, false)) {
            error = true;
        }

        if (verifyList("ClassDir", classDirs, false)) {
            error = true;
        }

        if (verifyList("Sources", sources, true)) {
            error = true;
        }

        if (verifyMap("WebApp", mappings)) {
            error = true;
        }

        return error;
    }

    private boolean verifyMap(String name, Map map) {
        boolean error = false;
        if (map == null || map.size() == 0) {
            System.out.println(name + " must be specified");
            return true;
        }

        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            List list = (List) entry.getValue();
            verifyList(name, list, false);
        }

        return error;
    }

    private boolean verifyList(String name, List list, boolean allowEmpty) {
        boolean error = false;
        if (!allowEmpty) {
            if (list == null || list.size() == 0) {
                System.out.println(name + " must be specified");
                return true;
            }
        }

        if (list != null) {
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                String s = (String) iterator.next();
                if (!new File(s).exists()) {
                    System.out.println(name + " doesn't exist: " + s);
                    error = true;
                }
            }
        }

        return error;
    }
}
