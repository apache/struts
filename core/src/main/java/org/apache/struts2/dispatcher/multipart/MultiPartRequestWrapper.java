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

package org.apache.struts2.dispatcher.multipart;

import com.opensymphony.xwork2.DefaultLocaleProvider;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;


/**
 * Parse a multipart request and provide a wrapper around the request. The parsing implementation used
 * depends on the <tt>struts.multipart.parser</tt> setting. It should be set to a class which
 * extends {@link org.apache.struts2.dispatcher.multipart.MultiPartRequest}. 
 * <p/>
 * The <tt>struts.multipart.parser</tt> property should be set to <tt>jakarta</tt> for the
 * Jakarta implementation, <tt>pell</tt> for the Pell implementation and <tt>cos</tt> for the Jason Hunter
 * implementation.
 * <p/>
 * The files are uploaded when the object is instantiated. If there are any errors they are logged using
 * {@link #addError(String)}. An action handling a multipart form should first check {@link #hasErrors()}
 * before doing any other processing.
 * <p/>
 * An alternate implementation, PellMultiPartRequest, is provided as a plugin.
 *
 */
public class MultiPartRequestWrapper extends StrutsRequestWrapper {

    protected static final Logger LOG = LoggerFactory.getLogger(MultiPartRequestWrapper.class);

    private Collection<String> errors;
    private MultiPartRequest multi;
    private Locale defaultLocale = Locale.ENGLISH;

    /**
     * Process file downloads and log any errors.
     *
     * @param multiPartRequest Our MultiPartRequest object
     * @param request Our HttpServletRequest object
     * @param saveDir Target directory for any files that we save
     * @param provider
     */
    public MultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request,
                                   String saveDir, LocaleProvider provider,
                                   boolean disableRequestAttributeValueStackLookup) {
        super(request, disableRequestAttributeValueStackLookup);
        errors = new ArrayList<String>();
        multi = multiPartRequest;
        defaultLocale = provider.getLocale();
        setLocale(request);
        try {
            multi.parse(request, saveDir);
            for (String error : multi.getErrors()) {
                addError(error);
            }
        } catch (IOException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(e.getMessage(), e);
            }
            addError(buildErrorMessage(e, new Object[] {e.getMessage()}));
        } 
    }

    public MultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request, String saveDir, LocaleProvider provider) {
        this(multiPartRequest, request, saveDir, provider, false);
    }

    protected void setLocale(HttpServletRequest request) {
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
    }

    protected String buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.messages.upload.error." + e.getClass().getSimpleName();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Preparing error message for key: [#0]", errorKey);
        }
        if (LocalizedTextUtil.findText(this.getClass(), errorKey, getLocale(), null, new Object[0]) == null) {
            return LocalizedTextUtil.findText(this.getClass(), "struts.messages.error.uploading", defaultLocale, null, new Object[] { e.getMessage() });
        } else {
            return LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, null, args);
        }
    }

    /**
     * Get an enumeration of the parameter names for uploaded files
     *
     * @return enumeration of parameter names for uploaded files
     */
    public Enumeration<String> getFileParameterNames() {
        if (multi == null) {
            return null;
        }

        return multi.getFileParameterNames();
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
     * @param fieldName Field to check for file names.
     * @return a String[] of file names for uploaded files
     */
    public String[] getFileNames(String fieldName) {
        if (multi == null) {
            return null;
        }

        return multi.getFileNames(fieldName);
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
        Map<String, String[]> map = new HashMap<String, String[]>();
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
        return !errors.isEmpty();
    }

    /**
     * Returns a collection of any errors generated when parsing the multipart request.
     *
     * @return the error Collection.
     */
    public Collection<String> getErrors() {
        return errors;
    }

    /**
     * Adds an error message when it isn't already added.
     *
     * @param anErrorMessage the error message to report.
     */
    protected void addError(String anErrorMessage) {
        if (!errors.contains(anErrorMessage)) {
            errors.add(anErrorMessage);
        }
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

    public void cleanUp() {
        if (multi != null) {
            multi.cleanUp();
        }
    }

}
