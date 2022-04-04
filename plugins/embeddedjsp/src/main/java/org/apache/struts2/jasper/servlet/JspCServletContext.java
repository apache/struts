/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.struts2.jasper.servlet;


import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.ActionContext;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.struts2.ServletActionContext;


/**
 * Simple <code>ServletContext</code> implementation without
 * HTTP-specific methods.
 *
 * @author Peter Rossbach (pr@webapp.de)
 */

public class JspCServletContext implements ServletContext {


    // ----------------------------------------------------- Instance Variables


    /**
     * Servlet context attributes.
     */
    protected Hashtable myAttributes;


    /**
     * The log writer we will write log messages to.
     */
    protected PrintWriter myLogWriter;


    /**
     * The base URL (document root) for this context.
     */
    private ClassLoaderInterface classLoaderInterface;


    // ----------------------------------------------------------- Constructors


    /**
     * Create a new instance of this ServletContext implementation.
     *
     * @param aLogWriter PrintWriter which is used for <code>log()</code> calls
     * @param classLoaderInterface classloader interface
     */
    public JspCServletContext(PrintWriter aLogWriter, ClassLoaderInterface classLoaderInterface) {

        myAttributes = new Hashtable();
        myLogWriter = aLogWriter;
        this.classLoaderInterface = classLoaderInterface;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * @return  the specified context attribute, if any.
     *
     * @param name Name of the requested attribute
     */
    public Object getAttribute(String name) {

        return (myAttributes.get(name));

    }


    /**
     * @return  an enumeration of context attribute names.
     */
    public Enumeration getAttributeNames() {

        return (myAttributes.keys());

    }


    /**
     * @return  the servlet context for the specified path.
     *
     * @param uripath Server-relative path starting with '/'
     */
    public ServletContext getContext(String uripath) {

        return (null);

    }


    /**
     * @return  the context path.
     */
    public String getContextPath() {

        return (null);

    }


    /**
     * @return  the specified context initialization parameter.
     *
     * @param name Name of the requested parameter
     */
    public String getInitParameter(String name) {

        return (null);

    }


    /**
     * @return  an enumeration of the names of context initialization
     * parameters.
     */
    public Enumeration getInitParameterNames() {

        return (new Vector().elements());

    }


    /**
     * @return  the Servlet API major version number.
     */
    public int getMajorVersion() {

        return (2);

    }


    /**
     * @return  the MIME type for the specified filename.
     *
     * @param file Filename whose MIME type is requested
     */
    public String getMimeType(String file) {

        return (null);

    }


    /**
     * @return  the Servlet API minor version number.
     */
    public int getMinorVersion() {

        return (3);

    }


    /**
     * @return  a request dispatcher for the specified servlet name.
     *
     * @param name Name of the requested servlet
     */
    public RequestDispatcher getNamedDispatcher(String name) {

        return (null);

    }


    /**
     * @return  the real path for the specified context-relative
     * virtual path.
     *
     * @param path The context-relative virtual path to resolve
     */
    public String getRealPath(String path) {
        try {
            return
                    (getResource(path).getFile().replace('/', File.separatorChar));
        } catch (Throwable t) {
            return (null);
        }
    }
            
            
    /**
     * @return  a request dispatcher for the specified context-relative path.
     *
     * @param path Context-relative path for which to acquire a dispatcher
     */
    public RequestDispatcher getRequestDispatcher(String path) {

        return (null);

    }


    /**
     * @return  a URL object of a resource that is mapped to the
     * specified context-relative path.
     *
     * @param path Context-relative path of the desired resource
     *
     * @exception MalformedURLException if the resource path is
     *  not properly formed
     */
    public URL getResource(String path) throws MalformedURLException {
        if ("/WEB-INF/web.xml".equals(path)) {
            if (ActionContext.getContext() != null) {
                ServletContext context = ServletActionContext.getServletContext();
                return context.getResource(path);
            }

            return null;
        }
        return classLoaderInterface.getResource(path);
    }


    /**
     * @return  an InputStream allowing access to the resource at the
     * specified context-relative path.
     *
     * @param path Context-relative path of the desired resource
     */
    public InputStream getResourceAsStream(String path) {
        try {
            return classLoaderInterface.getResourceAsStream(path);
        } catch (Throwable t) {
            return (null);
        }
    }


    /**
     * @return  the set of resource paths for the "directory" at the
     * specified context path.
     *
     * @param path Context-relative base path
     */
    public Set getResourcePaths(String path) {

        Set thePaths = new HashSet();
        if (!path.endsWith("/"))
            path += "/";
        String basePath = getRealPath(path);
        if (basePath == null)
            return (thePaths);
        File theBaseDir = new File(basePath);
        if (!theBaseDir.exists() || !theBaseDir.isDirectory())
            return (thePaths);
        String theFiles[] = theBaseDir.list();
        for (int i = 0; i < theFiles.length; i++) {
            File testFile = new File(basePath + File.separator + theFiles[i]);
            if (testFile.isFile())
                thePaths.add(path + theFiles[i]);
            else if (testFile.isDirectory())
                thePaths.add(path + theFiles[i] + "/");
        }
        return (thePaths);

    }


    /**
     * @return  descriptive information about this server.
     */
    public String getServerInfo() {

        return ("JspCServletContext/1.0");

    }


    /**
     * @return  a null reference for the specified servlet name.
     *
     * @param name Name of the requested servlet
     *
     * @deprecated This method has been deprecated with no replacement
     */
    public Servlet getServlet(String name) throws ServletException {

        return (null);

    }


    /**
     * @return  the name of this servlet context.
     */
    public String getServletContextName() {

        return (getServerInfo());

    }


    /**
     * @return  an empty enumeration of servlet names.
     *
     * @deprecated This method has been deprecated with no replacement
     */
    public Enumeration getServletNames() {

        return (new Vector().elements());

    }


    /**
     * @return  an empty enumeration of servlets.
     *
     * @deprecated This method has been deprecated with no replacement
     */
    public Enumeration getServlets() {

        return (new Vector().elements());

    }


    /**
     * Log the specified message.
     *
     * @param message The message to be logged
     */
    public void log(String message) {

        myLogWriter.println(message);

    }


    /**
     * Log the specified message and exception.
     *
     * @param exception The exception to be logged
     * @param message The message to be logged
     *
     * @deprecated Use log(String,Throwable) instead
     */
    public void log(Exception exception, String message) {

        log(message, exception);

    }


    /**
     * Log the specified message and exception.
     *
     * @param message The message to be logged
     * @param exception The exception to be logged
     */
    public void log(String message, Throwable exception) {

        myLogWriter.println(message);
        exception.printStackTrace(myLogWriter);

    }


    /**
     * Remove the specified context attribute.
     *
     * @param name Name of the attribute to remove
     */
    public void removeAttribute(String name) {

        myAttributes.remove(name);

    }


    /**
     * Set or replace the specified context attribute.
     *
     * @param name Name of the context attribute to set
     * @param value Corresponding attribute value
     */
    public void setAttribute(String name, Object value) {

        myAttributes.put(name, value);

    }



}
