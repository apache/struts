/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.showcase.fileupload;

import com.opensymphony.xwork.ActionSupport;

import java.io.File;

/**
 * Show case File Upload example's action. <code>FileUploadAction</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @author tm_jee
 * @version $Id: FileUploadAction.java,v 1.4 2006/03/05 16:48:34 rainerh Exp $
 */
public class FileUploadAction extends ActionSupport {


    private String contentType;
    private File upload;
    private String fileName;
    private String caption;
    
    // since we are using <ww:file name="upload" .../> the file name will be
    // obtained through getter/setter of <file-tag-name>FileName
    public String getUploadFileName() {
    	return fileName;
    }
    public void setUploadFileName(String fileName) {
    	this.fileName = fileName;
    }

    
    // since we are using <ww:file name="upload" ... /> the content type will be
    // obtained through getter/setter of <file-tag-name>ContentType
    public String getUploadContentType() {
        return contentType;
    }
    public void setUploadContentType(String contentType) {
        this.contentType = contentType;
    }

    
    // since we are using <ww:file name="upload" ... /> the File itself will be
    // obtained through getter/setter of <file-tag-name>
    public File getUpload() {
        return upload;
    }
    public void setUpload(File upload) {
        this.upload = upload;
    }

    
    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String input() throws Exception {
        return SUCCESS;
    }

    public String upload() throws Exception  {
        return SUCCESS;
    }

}
