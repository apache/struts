/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.dispatcher.mapper;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.config.Settings;

import com.opensymphony.xwork2.ObjectFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Factory that creates {@link ActionMapper}s. This factory looks up the class name of the {@link ActionMapper} from
 * Struts's configuration using the key <b>struts.mapper.class</b>.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 */
public class ActionMapperFactory {
    protected static final Log LOG = LogFactory.getLog(ActionMapperFactory.class);

    private static final HashMap<String,ActionMapper> classMap = new HashMap<String,ActionMapper>();

    /**
     * Gets an instance of the ActionMapper
     *
     * @return The action mapper
     */
    public static ActionMapper getMapper() {
        synchronized (classMap) {
            String clazz = (String) Settings.get(StrutsConstants.STRUTS_MAPPER_CLASS);
            try {
                ActionMapper mapper = (ActionMapper) classMap.get(clazz);
                if (mapper == null) {
                    mapper = (ActionMapper) ObjectFactory.getObjectFactory().buildBean(clazz, null);
                    classMap.put(clazz, mapper);
                }

                return mapper;
            } catch (Exception e) {
                String msg = "Could not create ActionMapper: Struts will *not* work!";
                throw new StrutsException(msg, e);
            }
        }
    }
}
