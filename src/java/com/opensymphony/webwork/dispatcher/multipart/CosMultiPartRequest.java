/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.dispatcher.multipart;

import com.oreilly.servlet.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


/**
 * Multipart form data request adapter for Jason Hunter's
 * <a href="http://www.servlets.com/cos/index.html" target="_blank">multipart utils</a>
 * (COS == com.oreilly.servlet).
 *
 * @author <a href="mailto:matt@smallleap.com">Matt Baldree</a> (modified for WW's use)
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a> (added i18n handling (WW-109))
 */
public class CosMultiPartRequest extends MultiPartRequest {

    private MultipartRequest multi;


    /**
     * Creates a new request wrapper to handle multi-part data using methods adapted from the COS
     * multipart classes (see class description).
     *
     * @param maxSize        maximum size post allowed
     * @param saveDir        the directory to save off the file
     * @param servletRequest the request containing the multipart
     */
    public CosMultiPartRequest(HttpServletRequest servletRequest, String saveDir, int maxSize) throws IOException {
        String encoding = getEncoding();

        if (encoding != null) {
            multi = new MultipartRequest(servletRequest, saveDir, maxSize, encoding);
        } else {
            multi = new MultipartRequest(servletRequest, saveDir, maxSize);
        }
    }


    public Enumeration getFileParameterNames() {
        return multi.getFileNames();
    }

    public String[] getContentType(String fieldName) {
        return new String[]{multi.getContentType(fieldName)};
    }

    public File[] getFile(String fieldName) {
        return new File[]{multi.getFile(fieldName)};
    }

    public String[] getFileNames(String fieldName) {
        return new String[]{multi.getFile(fieldName).getName()};
    }

    public String[] getFilesystemName(String name) {
        return new String[]{multi.getFilesystemName(name)};
    }

    public String getParameter(String name) {
        return multi.getParameter(name);
    }

    public Enumeration getParameterNames() {
        return multi.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        return multi.getParameterValues(name);
    }

    public List getErrors() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Set the encoding for the uploaded parameters. This needs to be set if you are using character sets
     * other than ASCII.<p>
     * <p/>
     * The encoding is looked up from the configuration setting 'webwork.i18n.encoding'.  This is usually set in
     * default.properties and webwork.properties.
     */
    private static String getEncoding() {
        return "utf-8";

        //todo: configuration in xwork needs to support non-action level config

        /*
        String encoding = null;
        try {

            //encoding = Configuration.getString("webwork.i18n.encoding");
        } catch (IllegalArgumentException e) {
            LogFactory.getLog(PellMultiPartRequest.class).info("Could not get encoding property 'webwork.i18n.encoding' for file upload.  Using system default");
        }
        return encoding;
        */
    }
}
