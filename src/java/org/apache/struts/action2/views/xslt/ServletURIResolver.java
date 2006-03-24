/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;


/**
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 *         Date: 14.10.2003
 *         Time: 16:50:06
 */
public class ServletURIResolver implements URIResolver {

    protected static Log log = LogFactory.getLog(URIResolver.class);
    static final String protocol = "res:";


    private ServletContext sc;


    public ServletURIResolver(ServletContext sc) {
        this.sc = sc;
    }


    public Source resolve(String href, String base) throws TransformerException {
        if (href.startsWith(protocol)) {
            String res = href.substring(protocol.length());
            log.debug("Resolving resource <" + res + ">");

            InputStream is = sc.getResourceAsStream(res);

            if (is == null) {
                throw new TransformerException("Resource " + res + " not found in resources.");
            }

            return new StreamSource(is);
        }

        throw new TransformerException("Cannot handle procotol of resource " + href);
    }
}
