package com.opensymphony.xwork2.util.fs;

import java.net.URL;

/**
 * Class represents common revision resource, should be used as default class when no other option exists
 */
public class Revision {

    protected Revision() {
    }

    public boolean needsReloading() {
        return false;
    }

    public static Revision build(URL fileUrl) {
        return new Revision();
    }

}
