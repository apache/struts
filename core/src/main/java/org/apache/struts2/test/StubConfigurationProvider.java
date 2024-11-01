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
package org.apache.struts2.test;

import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.util.location.LocatableProperties;

public class StubConfigurationProvider implements ConfigurationProvider {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
        // TODO Auto-generated method stub
    }

    @Override
    public void loadPackages() throws ConfigurationException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean needsReload() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {
        // TODO Auto-generated method stub

    }

}
