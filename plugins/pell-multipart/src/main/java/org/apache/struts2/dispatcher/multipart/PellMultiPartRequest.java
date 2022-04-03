/*
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import http.utils.multipartrequest.ServletMultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Multipart form data request adapter for Jason Pell's multipart utils package.
 *
 */
public class PellMultiPartRequest extends AbstractMultiPartRequest {

    private static final Logger LOG = LogManager.getLogger(PellMultiPartRequest.class);

    private ServletMultipartRequest multi;

    /**
     * Creates a new request wrapper to handle multi-part data using methods adapted from Jason Pell's
     * multipart classes (see class description).
     *
     * @param saveDir        the directory to save off the file
     * @param servletRequest the request containing the multipart
     */
    public void parse(HttpServletRequest servletRequest, String saveDir) throws IOException {
        //this needs to be synchronised, as we should not change the encoding at the same time as
        //calling the constructor.  See javadoc for MultipartRequest.setEncoding().
        synchronized (this) {
            setEncoding();
            if (maxSizeProvided){
                int intMaxSize = (maxSize >= Integer.MAX_VALUE ? Integer.MAX_VALUE : Long.valueOf(maxSize).intValue());
            	multi = new ServletMultipartRequest(servletRequest, saveDir, intMaxSize);
            }else{
            	multi = new ServletMultipartRequest(servletRequest, saveDir);
            }
        }
    }
    
    public Enumeration getFileParameterNames() {
        return multi.getFileParameterNames();
    }

    public String[] getContentType(String fieldName) {
        return new String[]{multi.getContentType(fieldName)};
    }

    public UploadedFile[] getFile(String fieldName) {
        return new UploadedFile[]{ new StrutsUploadedFile(multi.getFile(fieldName)) };
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

    public Enumeration<String> getParameterNames() {
        return multi.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        Enumeration enumeration = multi.getURLParameters(name);

        if (!enumeration.hasMoreElements()) {
            return null;
        }

        List<String> values = new ArrayList<String>();

        while (enumeration.hasMoreElements()) {
            values.add((String) enumeration.nextElement());
        }

        return values.toArray(new String[values.size()]);
    }

    /**
     * Sets the encoding for the uploaded params.  This needs to be set if you are using character sets other than
     * ASCII.
     * <p>
     * The encoding is looked up from the configuration setting 'struts.i18n.encoding'.  This is usually set in
     * default.properties & struts.properties.
     * </p>
     */
    private void setEncoding() {
        String encoding = null;

        try {
            encoding = defaultEncoding;

            if (encoding != null) {
                //NB: This should never be called at the same time as the constructor for
                //ServletMultiPartRequest, as it can cause problems.
                //See javadoc for MultipartRequest.setEncoding()
                http.utils.multipartrequest.MultipartRequest.setEncoding(encoding);
            } else {
                http.utils.multipartrequest.MultipartRequest.setEncoding("UTF-8");
            }
        } catch (IllegalArgumentException e) {
            if (LOG.isInfoEnabled()) {
        	    LOG.info("Could not get encoding property 'struts.i18n.encoding' for file upload.  Using system default");
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error("Encoding " + encoding + " is not a valid encoding.  Please check your struts.properties file.");
        }
    }

    /* (non-Javadoc)
    * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#cleanUp()
    */
    public void cleanUp() {
        Enumeration fileParameterNames = multi.getFileParameterNames();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            String inputValue = (String) fileParameterNames.nextElement();
            UploadedFile[] files = getFile(inputValue);
            for (UploadedFile currentFile : files) {
                LOG.debug("Removing file {} {}", inputValue, currentFile);
                if ((currentFile != null) && currentFile.isFile()) {
                    if (!currentFile.delete()) {
                        LOG.warn("Resource Leaking: Could not remove uploaded file [{}]", currentFile.getAbsolutePath());
                    }
                }
            }
        }
    }

}
