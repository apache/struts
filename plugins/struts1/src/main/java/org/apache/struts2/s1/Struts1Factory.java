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

package org.apache.struts2.s1;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ExceptionConfig;
import org.apache.struts.config.ModuleConfig;

import java.util.Arrays;
import java.util.Iterator;


/**
 *  Provides conversion methods between the Struts Action 1.x and XWork
 *  classes.
 */
public class Struts1Factory {
    
    private Configuration configuration;

    public Struts1Factory(Configuration config) {
        this.configuration = config;
    }
    
    /**
     * Create a Struts 1.x ModuleConfig based on an XWork package configuration.
     * 
     * @param packageName the name of the XWork package configuration to wrap.  This becomes the module prefix for the
     *     newly-created ModuleConfig.
     * @return a wrapper Struts 1.x ModuleConfig.
     */
    public ModuleConfig createModuleConfig(String packageName) {
        assert packageName != null;
        return new WrapperModuleConfig(this, configuration.getPackageConfig(packageName));
    }
    
    /**
     * Create a Struts 1.x ActionMapping from an XWork ActionConfig.
     * 
     * @param cfg the XWork ActionConfig.
     * @return a wrapper Struts 1.x ActionMapping.
     */
    public ActionMapping createActionMapping(ActionConfig cfg) {
        assert cfg != null;
        return new WrapperActionMapping(this, cfg);
    }

    /**
     * Create a Struts 1.x ActionMapping from an XWork ActionConfig.  This version provides an existing action path
     * and ModuleConfig.  Package-protected for now; may not need to be exposed publicly.
     * 
     * @param cfg the XWork ActionConfig.
     * @param actionPath the Struts 1.x-style action path ('/' + action-name).
     * @param moduleConfig the Struts 1.x ModuleConfig that contains the ActionMapping.
     * @return a wrapper Struts 1.x ActionMapping.
     */
    ActionMapping createActionMapping(ActionConfig cfg, String actionPath, ModuleConfig moduleConfig) {
        assert cfg != null;
        assert moduleConfig != null;
        return new WrapperActionMapping(this, cfg, actionPath, moduleConfig);
    }

    /**
     * Create a Struts 1.x ActionForward from an XWork ResultConfig.
     * 
     * @param cfg the XWork ResultConfig.
     * @return a wrapper Struts 1.x ActionMapping.
     */
    public ActionForward createActionForward(ResultConfig cfg) {
        assert cfg != null;
        return new WrapperActionForward(cfg);
    }

    /**
     * Create a Struts 1.x ExceptionConfig from an XWork ExceptionMappingConfig.
     * 
     * @param cfg the XWork ExceptionMappingConfig.
     * @return a wrapper Struts 1.x ExceptionConfig.
     */
    public ExceptionConfig createExceptionConfig(ExceptionMappingConfig cfg) {
        assert cfg != null;
        return new WrapperExceptionConfig(cfg);
    }

    public void convertErrors(ActionErrors errors, Object action) {
        ValidationAware vaction = null;
        TextProvider text = null;

        if (action instanceof ValidationAware) {
            vaction = (ValidationAware)action;
        }
        if (action instanceof TextProvider) {
            text = (TextProvider)action;
        }
        // if there is no ValidationAware, it doesn't make sense to process messages
        // as there be no place to put message in
        if (vaction == null) {
            return;
        }
        for (Iterator i = errors.properties(); i.hasNext(); ) {
            String field = (String) i.next();
            for (Iterator it = errors.get(field); it.hasNext(); ) {
                ActionMessage error = (ActionMessage) it.next();
                String msg = error.getKey();
                if (error.isResource() && text != null) {
                    Object[] values = error.getValues();
                    if (values != null) {
                        msg = text.getText(error.getKey(), Arrays.asList(values));
                    } else {
                        msg = text.getText(error.getKey());
                    }
                }
                if (ActionMessages.GLOBAL_MESSAGE.equals(field)) {
                    vaction.addActionError(msg);
                } else {
                    vaction.addFieldError(field, msg);
                }
            }
        }
    }
}
