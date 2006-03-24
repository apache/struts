/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.dispatcher.multipart;

import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.dispatcher.WebWorkRequestWrapper;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * Parses a multipart request and provides a wrapper around the request. The parsing implementation used
 * depends on the <tt>webwork.multipart.parser</tt> setting. It should be set to a class which
 * extends {@link com.opensymphony.webwork.dispatcher.multipart.MultiPartRequest}. <p>
 * <p/>
 * Webwork ships with three implementations,
 * {@link com.opensymphony.webwork.dispatcher.multipart.PellMultiPartRequest}, and
 * {@link com.opensymphony.webwork.dispatcher.multipart.CosMultiPartRequest} and
 * {@link com.opensymphony.webwork.dispatcher.multipart.JakartaMultiPartRequest}. The Jakarta implementation
 * is the default. The <tt>webwork.multipart.parser</tt> property should be set to <tt>jakarta</tt> for the
 * Jakarta implementation, <tt>pell</tt> for the Pell implementation and <tt>cos</tt> for the Jason Hunter
 * implementation. <p>
 * <p/>
 * The files are uploaded when the object is instantiated. If there are any errors they are logged using
 * {@link #addError(String)}. An action handling a multipart form should first check {@link #hasErrors()}
 * before doing any other processing. <p>
 *
 * @author Matt Baldree
 */
public class MultiPartRequestWrapper extends WebWorkRequestWrapper {
    protected static final Log log = LogFactory.getLog(MultiPartRequestWrapper.class);

    Collection errors;
    MultiPartRequest multi;

    /**
     * Instantiates the appropriate MultiPartRequest parser implementation and processes the data.
     *
     * @param request the servlet request object
     * @param saveDir directory to save the file(s) to
     * @param maxSize maximum file size allowed
     */
    public MultiPartRequestWrapper(HttpServletRequest request, String saveDir, int maxSize) {
        super(request);

        if (request instanceof MultiPartRequest) {
            multi = (MultiPartRequest) request;
        } else {
            String parser = Configuration.getString(WebWorkConstants.WEBWORK_MULTIPART_PARSER);

            // If it's not set, use Jakarta
            if (parser.equals("")) {
                log.warn("Property webwork.multipart.parser not set." +
                        " Using com.opensymphony.webwork.dispatcher.multipart.JakartaMultiPartRequest");
                parser = "com.opensymphony.webwork.dispatcher.multipart.JakartaMultiPartRequest";
            }
            // legacy support for old style property values
            else if (parser.equals("pell")) {
                parser = "com.opensymphony.webwork.dispatcher.multipart.PellMultiPartRequest";
            } else if (parser.equals("cos")) {
                parser = "com.opensymphony.webwork.dispatcher.multipart.CosMultiPartRequest";
            } else if (parser.equals("jakarta")) {
                parser = "com.opensymphony.webwork.dispatcher.multipart.JakartaMultiPartRequest";
            }

            try {
                Class baseClazz = com.opensymphony.webwork.dispatcher.multipart.MultiPartRequest.class;

                Class clazz = ClassLoaderUtils.loadClass(parser, MultiPartRequestWrapper.class);

                // make sure it extends MultiPartRequest
                if (!baseClazz.isAssignableFrom(clazz)) {
                    addError("Class '" + parser + "' does not extend MultiPartRequest");

                    return;
                }

                // get the constructor
                Constructor ctor = clazz.getDeclaredConstructor(new Class[]{
                        ClassLoaderUtils.loadClass("javax.servlet.http.HttpServletRequest", MultiPartRequestWrapper.class),
                        java.lang.String.class, int.class
                });

                // build the parameter list
                Object[] parms = new Object[]{
                        request, saveDir, new Integer(maxSize)
                };

                // instantiate it
                multi = (MultiPartRequest) ctor.newInstance(parms);
                for (Iterator iter = multi.getErrors().iterator(); iter.hasNext();) {
                    String error = (String) iter.next();
                    addError(error);
                }
            } catch (ClassNotFoundException e) {
                addError("Class: " + parser + " not found.");
            } catch (NoSuchMethodException e) {
                addError("Constructor error for " + parser + ": " + e);
            } catch (InstantiationException e) {
                addError("Error instantiating " + parser + ": " + e);
            } catch (IllegalAccessException e) {
                addError("Access errror for " + parser + ": " + e);
            } catch (InvocationTargetException e) {
                // This is a wrapper for any exceptions thrown by the constructor called from newInstance
                addError(e.getTargetException().toString());
            }
        }
    }

    /**
     * @deprecated use {@link #getFileParameterNames()} instead
     */
    public Enumeration getFileNames() {
        return getFileParameterNames();
    }

    /**
     * Get an enumeration of the parameter names for uploaded files
     *
     * @return enumeration of parameter names for uploaded files
     */
    public Enumeration getFileParameterNames() {
        if (multi == null) {
            return null;
        }

        return multi.getFileParameterNames();
    }

    /**
     * @deprecated use {@link #getContentTypes(String)} instead
     */
    public String getContentType(String fieldName) {
        String[] contentTypes = getContentTypes(fieldName);
        if (contentTypes != null && contentTypes.length > 0) {
            return contentTypes[0];
        }

        return null;
    }


    /**
     * Get an array of content encoding for the specified input field name or <tt>null</tt> if
     * no content type was specified.
     *
     * @param name input field name
     * @return an array of content encoding for the specified input field name
     */
    public String[] getContentTypes(String name) {
        if (multi == null) {
            return null;
        }

        return multi.getContentType(name);
    }

    /**
     * @deprecated use {@link #getFiles(String)} instead
     */
    public File getFile(String fieldName) {
        File[] files = getFiles(fieldName);
        if (files != null && files.length > 0) {
            return files[0];
        }

        return null;
    }

    /**
     * Get a {@link java.io.File[]} for the given input field name.
     *
     * @param fieldName input field name
     * @return a File[] object for files associated with the specified input field name
     */
    public File[] getFiles(String fieldName) {
        if (multi == null) {
            return null;
        }

        return multi.getFile(fieldName);
    }

    /**
     * Get a String array of the file names for uploaded files
     *
     * @return a String[] of file names for uploaded files
     */
    public String[] getFileNames(String fieldName) {
        if (multi == null) {
            return null;
        }

        return multi.getFileNames(fieldName);
    }

    /**
     * @deprecated use {@link #getFileSystemNames(String)} instead
     */
    public String getFilesystemName(String fieldName) {
        String[] names = getFileSystemNames(fieldName);
        if (names != null && names.length > 0) {
            return names[0];
        }

        return null;
    }

    /**
     * Get the filename(s) of the file(s) uploaded for the given input field name.
     * Returns <tt>null</tt> if the file is not found.
     *
     * @param fieldName input field name
     * @return the filename(s) of the file(s) uploaded for the given input field name or
     *         <tt>null</tt> if name not found.
     */
    public String[] getFileSystemNames(String fieldName) {
        if (multi == null) {
            return null;
        }

        return multi.getFilesystemName(fieldName);
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getParameter(String)
     */
    public String getParameter(String name) {
        return ((multi == null) || (multi.getParameter(name) == null)) ? super.getParameter(name) : multi.getParameter(name);
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getParameterMap()
     */
    public Map getParameterMap() {
        Map map = new HashMap();
        Enumeration enumeration = getParameterNames();

        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            map.put(name, this.getParameterValues(name));
        }

        return map;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getParameterNames()
     */
    public Enumeration getParameterNames() {
        if (multi == null) {
            return super.getParameterNames();
        } else {
            return mergeParams(multi.getParameterNames(), super.getParameterNames());
        }
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getParameterValues(String)
     */
    public String[] getParameterValues(String name) {
        return ((multi == null) || (multi.getParameterValues(name) == null)) ? super.getParameterValues(name) : multi.getParameterValues(name);
    }

    /**
     * Returns <tt>true</tt> if any errors occured when parsing the HTTP multipart request, <tt>false</tt> otherwise.
     *
     * @return <tt>true</tt> if any errors occured when parsing the HTTP multipart request, <tt>false</tt> otherwise.
     */
    public boolean hasErrors() {
        if ((errors == null) || errors.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns a collection of any errors generated when parsing the multipart request.
     *
     * @return the error Collection.
     */
    public Collection getErrors() {
        return errors;
    }

    /**
     * Adds an error message.
     *
     * @param anErrorMessage the error message to report.
     */
    protected void addError(String anErrorMessage) {
        if (errors == null) {
            errors = new ArrayList();
        }

        errors.add(anErrorMessage);
    }

    /**
     * Merges 2 enumeration of parameters as one.
     *
     * @param params1 the first enumeration.
     * @param params2 the second enumeration.
     * @return a single Enumeration of all elements from both Enumerations.
     */
    protected Enumeration mergeParams(Enumeration params1, Enumeration params2) {
        Vector temp = new Vector();

        while (params1.hasMoreElements()) {
            temp.add(params1.nextElement());
        }

        while (params2.hasMoreElements()) {
            temp.add(params2.nextElement());
        }

        return temp.elements();
    }
}
