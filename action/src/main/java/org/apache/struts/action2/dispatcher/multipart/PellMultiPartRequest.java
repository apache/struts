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

import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.StrutsConstants;
import http.utils.multipartrequest.ServletMultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


/**
 * Multipart form data request adapter for Jason Pell's multipart utils package.
 *
 * @author <a href="matt@smallleap.com">Matt Baldree</a> (modified for WW's use)
 * @author <a href="scott@atlassian.com">Scott Farquhar</a> (added i18n handling (WW-109))
 */
public class PellMultiPartRequest extends MultiPartRequest {

    private ServletMultipartRequest multi;


    /**
     * Creates a new request wrapper to handle multi-part data using methods adapted from Jason Pell's
     * multipart classes (see class description).
     *
     * @param maxSize        maximum size post allowed
     * @param saveDir        the directory to save off the file
     * @param servletRequest the request containing the multipart
     */
    public PellMultiPartRequest(HttpServletRequest servletRequest, String saveDir, int maxSize) throws IOException {
        //this needs to be synchronised, as we should not change the encoding at the same time as
        //calling the constructor.  See javadoc for MultipartRequest.setEncoding().
        synchronized (this) {
            setEncoding();
            multi = new ServletMultipartRequest(servletRequest, saveDir, maxSize);
        }
    }


    public Enumeration getFileParameterNames() {
        return multi.getFileParameterNames();
    }

    public String[] getContentType(String fieldName) {
        return new String[]{multi.getContentType(fieldName)};
    }

    public File[] getFile(String fieldName) {
        return new File[]{multi.getFile(fieldName)};
    }

    public String[] getFileNames(String fieldName) {

        // TODO - not sure about this - is this the filename of the actual file or
        // TODO - the uploaded filename as provided by the browser?
        // TODO - Not sure what version of Pell this class uses as it doesn't seem to be the latest
        return new String[]{multi.getFile(fieldName).getName()};
    }

    public String[] getFilesystemName(String fieldName) {
        return new String[]{multi.getFileSystemName(fieldName)};
    }

    public String getParameter(String name) {
        return multi.getURLParameter(name);
    }

    public Enumeration getParameterNames() {
        return multi.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        Enumeration enumeration = multi.getURLParameters(name);

        if (!enumeration.hasMoreElements()) {
            return null;
        }

        List values = new ArrayList();

        while (enumeration.hasMoreElements()) {
            values.add(enumeration.nextElement());
        }

        return (String[]) values.toArray(new String[values.size()]);
    }

    public List getErrors() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Sets the encoding for the uploaded params.  This needs to be set if you are using character sets other than
     * ASCII.
     * <p/>
     * The encoding is looked up from the configuration setting 'struts.i18n.encoding'.  This is usually set in
     * default.properties & struts.properties.
     */
    private static void setEncoding() {
        String encoding = null;

        try {
            encoding = Configuration.getString(StrutsConstants.STRUTS_I18N_ENCODING);

            if (encoding != null) {
                //NB: This should never be called at the same time as the constructor for
                //ServletMultiPartRequest, as it can cause problems.
                //See javadoc for MultipartRequest.setEncoding()
                http.utils.multipartrequest.MultipartRequest.setEncoding(encoding);
            } else {
                http.utils.multipartrequest.MultipartRequest.setEncoding("UTF-8");
            }
        } catch (IllegalArgumentException e) {
            log.info("Could not get encoding property 'struts.i18n.encoding' for file upload.  Using system default");
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding " + encoding + " is not a valid encoding.  Please check your struts.properties file.");
        }
    }
}
