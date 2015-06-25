package com.opensymphony.xwork2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

/**
 * Basic interface to access file on the File System and to monitor changes
 */
public interface FileManager {

    /**
     * Enables configs reloading when config file changed
     *
     * @param reloadingConfigs {@link XWorkConstants#RELOAD_XML_CONFIGURATION}
     */
    void setReloadingConfigs(boolean reloadingConfigs);

    /**
     * Checks if given file changed and must be reloaded if {@link #setReloadingConfigs(boolean)} is true
     *
     * @param fileName to check
     * @return true if file changed
     */
    boolean fileNeedsReloading(String fileName);

    /**
     * Checks if file represented by provided URL should be reloaded
     *
     * @param fileUrl url to a file
     * @return true if file exists and should be reloaded, if url is null return false
     */
    boolean fileNeedsReloading(URL fileUrl);

    /**
     * Loads opens the named file and returns the InputStream
     *
     * @param fileUrl - the URL of the file to open
     * @return an InputStream of the file contents or null
     * @throws IllegalArgumentException if there is no file with the given file name
     */
    InputStream loadFile(URL fileUrl);

    /**
     * Adds file to list of monitored files if {@link #setReloadingConfigs(boolean)} is true
     *
     * @param fileUrl {@link URL} to file to be monitored
     */
    void monitorFile(URL fileUrl);

    /**
     * Convert URLs to URLs with "file" protocol
     * @param url URL to convert to a jar url
     * @return a URL to a file, or null if the URL external form cannot be parsed
     */
    URL normalizeToFileProtocol(URL url);

    /**
     * Indicate if given implementation supports current OS File System
     *
     * @return true if supports current OS File System
     */
    boolean support();

    /**
     * User's implementation should return false as then it will be taken in first place
     *
     * @return true if it's a framework provided implementation
     */
    boolean internal();

    Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException;

}
