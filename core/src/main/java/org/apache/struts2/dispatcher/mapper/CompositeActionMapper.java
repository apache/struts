/*
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

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * <!-- START SNIPPET: description -->
 *
 * A composite action mapper that is capable of delegating to a series of {@link ActionMapper} if the former
 * failed to obtained a valid {@link ActionMapping} or uri.
 *
 * More details: http://struts.apache.org/docs/actionmapper.html
 *
 * @see ActionMapper
 * @see ActionMapping
 *
 * @version $Date$ $Id$
 */
public class CompositeActionMapper implements ActionMapper {

    private static final Logger LOG = LogManager.getLogger(CompositeActionMapper.class);

    protected List<ActionMapper> actionMappers = new LinkedList<>();

    @Inject
    public CompositeActionMapper(Container container,
                                 @Inject(value = StrutsConstants.STRUTS_MAPPER_COMPOSITE) String list) {
        String[] arr = StringUtils.split(StringUtils.trimToEmpty(list), ",");
        for (String name : arr) {
            Object obj = container.getInstance(ActionMapper.class, name);
            if (obj != null) {
                actionMappers.add((ActionMapper) obj);
            }
        }
    }

    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {

        for (ActionMapper actionMapper : actionMappers) {
            ActionMapping actionMapping = actionMapper.getMapping(request, configManager);
            LOG.debug("Using ActionMapper: {}", actionMapper);
            if (actionMapping == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", actionMapper);
            }
            else {
                return actionMapping;
            }
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }

    public ActionMapping getMappingFromActionName(String actionName) {

        for (ActionMapper actionMapper : actionMappers) {
            ActionMapping actionMapping = actionMapper.getMappingFromActionName(actionName);
            LOG.debug("Using ActionMapper: {}", actionMapper);
            if (actionMapping == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", actionMapper);
            }
            else {
                return actionMapping;
            }
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }

    public String getUriFromActionMapping(ActionMapping mapping) {

        for (ActionMapper actionMapper : actionMappers) {
            String uri = actionMapper.getUriFromActionMapping(mapping);
            LOG.debug("Using ActionMapper: {}", actionMapper);
            if (uri == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", actionMapper);
            }
            else {
                return uri;
            }
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }
}
