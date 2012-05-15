package com.opensymphony.xwork2;

/**
 * Factory that creates FileManager, default to {@link com.opensymphony.xwork2.util.fs.DefaultFileManager}
 */
public interface FileManagerFactory {

    FileManager getFileManager();

}
