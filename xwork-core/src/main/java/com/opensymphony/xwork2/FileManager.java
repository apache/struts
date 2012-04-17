package com.opensymphony.xwork2;

import java.io.InputStream;
import java.net.URL;

/**
 * Basic interface to access file on the File System and to monitor changes
 */
public interface FileManager {

    void setReloadingConfigs(boolean reloadingConfigs);

    boolean isReloadingConfigs();

    /**
     * Checks if given file changed and must be reloaded if {@link #isReloadingConfigs()} is true
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
     * Adds file to list of monitored files if {@link #isReloadingConfigs()} is true
     *
     * @param fileUrl {@link URL} to file to be monitored
     */
    void monitorFile(URL fileUrl);

}
