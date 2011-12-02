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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: description -->
 *
 * A composite action mapper that is capable of delegating to a series of {@link ActionMapper} if the former
 * failed to obtained a valid {@link ActionMapping} or uri.
 * <p/>
 * It is configured through struts.properties.
 * <p/>
 * For example, with the following entries in struts.properties
 * <p/>
 * <pre>
 * &lt;bean type="org.apache.struts2.dispatcher.mapper.ActionMapper" name="struts" 
 *       class="org.apache.struts2.dispatcher.mapper.CompositeActionMapper" /&gt;
 * &lt;constant name="struts.mapper.composite" 
 *       value="org.apache.struts2.dispatcher.mapper.DefaultActionMapper,org.apache.struts2.dispatcher.mapper.RestfulActionMapper,org.apache.struts2.dispatcher.mapper.Restful2ActionMapper" /&gt;
 * </pre>
 * <p/>
 * When {@link CompositeActionMapper#getMapping(HttpServletRequest, ConfigurationManager)} or
 * {@link CompositeActionMapper#getUriFromActionMapping(ActionMapping)} is invoked,
 * {@link CompositeActionMapper} would go through these {@link ActionMapper}s in sequence
 * starting from {@link ActionMapper} identified by 'struts.mapper.composite.1', followed by
 * 'struts.mapper.composite.2' and finally 'struts.mapper.composite.3' (in this case) until either
 * one of the {@link ActionMapper} return a valid result (not null) or it runs out of {@link ActionMapper}
 * in which case it will just return null for both
 * {@link CompositeActionMapper#getMapping(HttpServletRequest, ConfigurationManager)} and
 * {@link CompositeActionMapper#getUriFromActionMapping(ActionMapping)} methods.
 * <p/>
 *
 * For example with the following in struts-*.xml :-
 * <pre>
 *    &lt;bean type="org.apache.struts2.dispatcher.mapper.ActionMapper" name="struts" 
 *       class="org.apache.struts2.dispatcher.mapper.CompositeActionMapper" /&gt;
 *    &lt;constant name="struts.mapper.composite" 
 *       value="org.apache.struts2.dispatcher.mapper.DefaultActionMapper,foo.bar.MyActionMapper,foo.bar.MyAnotherActionMapper" /&gt;
 * </pre>
 * <p/>
 * <code>CompositeActionMapper</code> will be configured with 3 ActionMapper, namely
 * "DefaultActionMapper", "MyActionMapper" and "MyAnotherActionMapper".
 * <code>CompositeActionMapper</code> would consult each of them in order described above.
 *
 * <!-- END SNIPPET: description -->
 *
 * @see ActionMapper
 * @see ActionMapping
 *
 * @version $Date$ $Id$
 */
public class CompositeActionMapper implements ActionMapper {

    private static final Logger LOG = LoggerFactory.getLogger(CompositeActionMapper.class);

    protected Container container;
    
    protected List<ActionMapper> actionMappers = new ArrayList<ActionMapper>();
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
    
    @Inject(StrutsConstants.STRUTS_MAPPER_COMPOSITE)
    public void setActionMappers(String list) {
        if (list != null) {
            String[] arr = list.split(",");
            for (String name : arr) {
                Object obj = container.getInstance(ActionMapper.class, name);
                if (obj != null) {
                    actionMappers.add((ActionMapper) obj);
                }
            }
        }
    }


    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {

        for (ActionMapper actionMapper : actionMappers) {
            ActionMapping actionMapping = actionMapper.getMapping(request, configManager);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using ActionMapper "+actionMapper);
            }
            if (actionMapping == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ActionMapper "+actionMapper+" failed to return an ActionMapping (null)");
                }
            }
            else {
                return actionMapping;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        }
        return null;
    }

    public ActionMapping getMappingFromActionName(String actionName) {

        for (ActionMapper actionMapper : actionMappers) {
            ActionMapping actionMapping = actionMapper.getMappingFromActionName(actionName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using ActionMapper "+actionMapper);
            }
            if (actionMapping == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ActionMapper "+actionMapper+" failed to return an ActionMapping (null)");
                }
            }
            else {
                return actionMapping;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        }
        return null;
    }

    public String getUriFromActionMapping(ActionMapping mapping) {

        for (ActionMapper actionMapper : actionMappers) {
            String uri = actionMapper.getUriFromActionMapping(mapping);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using ActionMapper "+actionMapper);
            }
            if (uri == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ActionMapper "+actionMapper+" failed to return an ActionMapping (null)");
                }
            }
            else {
                return uri;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("exhausted from ActionMapper that could return a uri");
        }
        return null;
    }
}
