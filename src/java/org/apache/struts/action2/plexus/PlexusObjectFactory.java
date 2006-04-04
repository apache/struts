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
package org.apache.struts.action2.plexus;

import org.apache.struts.action2.util.ObjectFactoryInitializable;
import com.opensymphony.xwork.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.PlexusContainer;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Plexus integartion. You need three optional files: plexus-request.xml, plexus-session.xml, and
 * plexus-application.xml.
 *
 * The syntax of these files is:
 *
 * <pre>
 * &lt;plexus&gt;
 * &lt;components&gt;
 *  &lt;component&gt;
 *      &lt;role&gt;com.acme.MyBean&lt;/role&gt;
 *      &lt;implementation&gt;com.acme.MyBean|com.acme.MyBeanImpl&lt;/implementation&gt;
 *      &lt;componentComposer&gt;field|setter|?&lt;/componentComposer&gt;
 *      &lt;requirements&gt;
 *          &lt;requirement&gt;
 *              &lt;role&gt;com.acme.MyOtherBean&lt;/role&gt;
 *          &lt;/requirement&gt;
 *      &lt;/requirements&gt;
 *      &lt;configuration&gt;
 *          &lt;foo&gt;123&lt;/foo&gt;
 *          &lt;bar&gt;hello, world&lt;/bar&gt;
 *      &lt;/configuration&gt;
 *      &lt;/component&gt;
 *  &lt;/components&gt;
 * &lt;/plexus&gt;
 * </pre>
 */
public class PlexusObjectFactory extends ObjectFactory implements ObjectFactoryInitializable {
    private static final Log log = LogFactory.getLog(PlexusObjectFactory.class);

    private PlexusContainer base;

    public void init(ServletContext servletContext) {
        if (!PlexusLifecycleListener.loaded || !PlexusFilter.loaded) {
            // uh oh! looks like the lifecycle listener wasn't installed. Let's inform the user
            String message = "********** FATAL ERROR STARTING UP PLEXUS-STRUTS INTEGRATION **********\n" +
                    "Looks like the Plexus listener was not configured for your web app! \n" +
                    "You need to add the following to web.xml: \n" +
                    "\n" +
                    "    <!-- this should be before the Struts filter -->\n" +
                    "    <filter>\n" +
                    "        <filter-name>plexus</filter-name>\n" +
                    "        <filter-class>org.apache.struts.action2.plexus.PlexusFilter</filter-class>\n" +
                    "    </filter>\n" +
                    "\n" +
                    "...\n" +
                    "\n" +
                    "    <!-- this should be before the Struts filter -->\n" +
                    "    <filter-mapping>\n" +
                    "        <filter-name>plexus</filter-name>\n" +
                    "        <url-pattern>/*</url-pattern>\n" +
                    "    </filter-mapping>\n" +
                    "\n" +
                    "...\n" +
                    "\n" +
                    "    <listener>\n" +
                    "        <listener-class>org.apache.struts.action2.plexus.PlexusLifecycleListener</listener-class>\n" +
                    "    </listener>";
            log.fatal(message);
            return;
        }

        base = (PlexusContainer) servletContext.getAttribute(PlexusLifecycleListener.KEY);
    }

    public Object buildBean(String className, Map extraContext) throws Exception {
        PlexusContainer pc = PlexusThreadLocal.getPlexusContainer();
        if (pc == null) {
            pc = base;
        }

        try {
            return pc.lookup(className);
        } catch (Exception e) {
            Object o = super.buildBean(className, extraContext);
            pc.autowire(o);
            return o;
        }
    }
}
