package com.opensymphony.webwork.views.freemarker;

import com.opensymphony.util.ClassLoaderUtil;
import freemarker.cache.URLTemplateLoader;

import java.net.URL;

/**
 * User: plightbo
 * Date: Aug 10, 2005
 * Time: 11:25:05 PM
 */
public class WebWorkClassTemplateLoader extends URLTemplateLoader {
    protected URL getURL(String name) {
        return ClassLoaderUtil.getResource(name, getClass());
    }
}
