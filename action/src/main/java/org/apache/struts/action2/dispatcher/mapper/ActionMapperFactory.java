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
package org.apache.struts.action2.dispatcher.mapper;

import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.StrutsConstants;
import com.opensymphony.xwork.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Factory that creates {@link ActionMapper}s. This factory looks up the class name of the {@link ActionMapper} from
 * Struts's configuration using the key <b>struts.mapper.class</b>.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * @author Patrick Lightbody
 */
public class ActionMapperFactory {
    protected static final Log LOG = LogFactory.getLog(ActionMapperFactory.class);

    private static final HashMap classMap = new HashMap();

    public static ActionMapper getMapper() {
        synchronized (classMap) {
            String clazz = (String) Configuration.get(StrutsConstants.STRUTS_MAPPER_CLASS);
            try {
                ActionMapper mapper = (ActionMapper) classMap.get(clazz);
                if (mapper == null) {
                    mapper = (ActionMapper) ObjectFactory.getObjectFactory().buildBean(clazz, null);
                    classMap.put(clazz, mapper);
                }

                return mapper;
            } catch (Exception e) {
                String msg = "Could not create ActionMapper: Struts Action Framework will *not* work!";
                LOG.fatal(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
    }
}
