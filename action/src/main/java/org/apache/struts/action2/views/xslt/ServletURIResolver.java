/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.views.xslt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;


/**
 *
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
