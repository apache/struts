/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.quickstart;

/**
 * Used for mapping configuration for the QuickStart application.
 *
 * @author patrick
 */
public class Mapping {
    String path;
    String dir;

    public Mapping(String path, String dir) {
        this.path = path;
        this.dir = dir;
    }

    public Mapping() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
