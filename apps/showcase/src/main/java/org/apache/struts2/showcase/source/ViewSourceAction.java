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
package org.apache.struts2.showcase.source;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.struts2.action.ServletContextAware;

import jakarta.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes configuration, page, and action class paths to create snippets
 * of the files for display.
 */
public class ViewSourceAction extends ActionSupport implements ServletContextAware {

    private String page;
    private String className;
    private String config;

    private List<String> pageLines;
    private List<String> classLines;
    private List<String> configLines;

    private int configLine;
    private int padding = 10;

    private ServletContext servletContext;

    public String execute() throws IOException {

        if (page != null) {

            InputStream in = ClassLoaderUtil.getResourceAsStream(page.substring(page.indexOf("//") + 1), getClass());
            page = page.replace("//", "/");

            if (in == null) {
                in = servletContext.getResourceAsStream(page);
                while (in == null && page.indexOf('/', 1) > 0) {
                    page = page.substring(page.indexOf('/', 1));
                    in = servletContext.getResourceAsStream(page);
                }
            }
            pageLines = read(in, -1);

            if (in != null) {
                in.close();
            }
        }

        if (className != null) {
            className = "/" + className.replace('.', '/') + ".java";
            InputStream in = getClass().getResourceAsStream(className);
            if (in == null) {
                in = servletContext.getResourceAsStream("/WEB-INF/src/java" + className);
            }
            classLines = read(in, -1);

            if (in != null) {
                in.close();
            }
        }

        if (config != null && config.startsWith("file:/")) {
            int pos = config.lastIndexOf(':');
            configLine = Integer.parseInt(config.substring(pos + 1));
            configLines = read(new URL(config.substring(0, pos)).openStream(), configLine);
        }
        return SUCCESS;
    }


    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        if (className != null && className.trim().length() > 0) {
            this.className = className;
        }
    }

    /**
     * @param config the config to set
     */
    public void setConfig(String config) {
        if (config != null && config.trim().length() > 0) {
            this.config = config;
        }
    }

    /**
     * @param page the page to set
     */
    public void setPage(String page) {
        if (page != null && page.trim().length() > 0) {
            this.page = page;
        }
    }

    /**
     * @param padding the padding to set
     */
    public void setPadding(int padding) {
        this.padding = padding;
    }


    /**
     * @return the classLines
     */
    public List<String> getClassLines() {
        return classLines;
    }

    /**
     * @return the configLines
     */
    public List<String> getConfigLines() {
        return configLines;
    }

    /**
     * @return the pageLines
     */
    public List<String> getPageLines() {
        return pageLines;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the config
     */
    public String getConfig() {
        return config;
    }

    /**
     * @return the page
     */
    public String getPage() {
        return page;
    }

    /**
     * @return the configLine
     */
    public int getConfigLine() {
        return configLine;
    }

    /**
     * @return the padding
     */
    public int getPadding() {
        return padding;
    }

    /**
     * Reads in a stream, optionally only including the target line number
     * and its padding
     *
     * @param in               The input stream
     * @param targetLineNumber The target line number, negative to read all
     * @return A list of lines
     */
    private List<String> read(InputStream in, int targetLineNumber) {
        List<String> snippet = null;
        if (in != null) {
            snippet = new ArrayList<>();
            int startLine = 0;
            int endLine = Integer.MAX_VALUE;
            if (targetLineNumber > 0) {
                startLine = targetLineNumber - padding;
                endLine = targetLineNumber + padding;
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                int lineno = 0;
                String line;
                while ((line = reader.readLine()) != null) {
                    lineno++;
                    if (lineno >= startLine && lineno <= endLine) {
                        snippet.add(line);
                    }
                }
            } catch (Exception ex) {
                // ignoring as snippet not available isn't a big deal
            }
        }
        return snippet;
    }

    public void withServletContext(ServletContext arg0) {
        this.servletContext = arg0;
    }


}
