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

package org.apache.struts2.config_browser;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.struts2.xwork2.inject.Container;
import org.apache.struts2.xwork2.inject.Inject;
import org.apache.struts2.xwork2.util.ClassLoaderUtil;

/**
 * Shows all constants as loaded by Struts
 */
public class ShowJarsAction extends ActionNamesAction {

    List<Properties> poms;
    
    @Inject
    public void setContainer(Container container) {
        try {
            poms = configHelper.getJarProperties();
        }
        catch (IOException ioe) {
            // this is the config browser, so it doesn't seem necessary to do more than just
            // send up a debug message
            if (LOG.isDebugEnabled()) {
                LOG.debug("IOException caught while retrieving jar properties - " + ioe.getMessage());
            }
            poms = Collections.EMPTY_LIST; // maybe avoiding NPE later
        }
    }
    
    public List<Properties> getJarPoms()
    {
        return poms;
    }
    
    public Iterator<URL> getPluginsLoaded() 
    {
        try {
            return ClassLoaderUtil.getResources("struts-plugin.xml", ShowJarsAction.class, false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
