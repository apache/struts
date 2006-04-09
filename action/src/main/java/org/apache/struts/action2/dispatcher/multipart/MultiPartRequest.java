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
package org.apache.struts.action2.dispatcher.multipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Enumeration;
import java.util.List;


/**
 * Abstract wrapper class HTTP requests to handle multi-part data. <p>
 *
 * @author <a href="mailto:matt@smallleap.com">Matt Baldree</a>
 * @author Patrick Lightbody
 * @author Bill Lynch (docs)
 */
public abstract class MultiPartRequest {

    protected static Log log = LogFactory.getLog(MultiPartRequest.class);


    /**
     * Returns <tt>true</tt> if the request is multipart form data, <tt>false</tt> otherwise.
     *
     * @param request the http servlet request.
     * @return <tt>true</tt> if the request is multipart form data, <tt>false</tt> otherwise.
     */
    public static boolean isMultiPart(HttpServletRequest request) {
        String content_type = request.getContentType();
        return content_type != null && content_type.indexOf("multipart/form-data") != -1;
    }

    /**
     * Returns an enumeration of the parameter names for uploaded files
     *
     * @return an enumeration of the parameter names for uploaded files
     */
    public abstract Enumeration getFileParameterNames();

    /**
     * Returns the content type(s) of the file(s) associated with the specified field name
     * (as supplied by the client browser), or <tt>null</tt> if no files are associated with the
     * given field name.
     *
     * @param fieldName input field name
     * @return an array of content encoding for the specified input field name or <tt>null</tt> if
     *         no content type was specified.
     */
    public abstract String[] getContentType(String fieldName);

    /**
     * Returns a {@link java.io.File} object for the filename specified or <tt>null</tt> if no files
     * are associated with the given field name.
     *
     * @param fieldName input field name
     * @return a File[] object for files associated with the specified input field name
     */
    public abstract File[] getFile(String fieldName);

    /**
     * Returns a String[] of file names for files associated with the specified input field name
     *
     * @param fieldName input field name
     * @return a String[] of file names for files associated with the specified input field name
     */
    public abstract String[] getFileNames(String fieldName);

    /**
     * Returns the file system name(s) of files associated with the given field name or
     * <tt>null</tt> if no files are associated with the given field name.
     *
     * @param fieldName input field name
     * @return the file system name(s) of files associated with the given field name
     */
    public abstract String[] getFilesystemName(String fieldName);

    /**
     * Returns the specified request parameter.
     *
     * @param name the name of the parameter to get
     * @return the parameter or <tt>null</tt> if it was not found.
     */
    public abstract String getParameter(String name);

    /**
     * Returns an enumeration of String parameter names.
     *
     * @return an enumeration of String parameter names.
     */
    public abstract Enumeration getParameterNames();

    /**
     * Returns a list of all parameter values associated with a parameter name. If there is only
     * one parameter value per name the resulting array will be of length 1.
     *
     * @param name the name of the parameter.
     * @return an array of all values associated with the parameter name.
     */
    public abstract String[] getParameterValues(String name);

    /**
     * Returns a list of error messages that may have occurred while processing the request.
     * If there are no errors, an empty list is returned. If the underlying implementation
     * (ie: pell, cos, jakarta, etc) cannot support providing these errors, an empty list is
     * also returned. This list of errors is repoted back to the
     * {@link MultiPartRequestWrapper}'s errors field.
     *
     * @return a list of Strings that represent various errors during parsing
     */
    public abstract List getErrors();
}
