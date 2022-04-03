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
package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.config.AbstractBeanSelectionProvider;
import org.apache.struts2.config.StrutsBeanSelectionProvider;

/**
 * Please see {@link StrutsBeanSelectionProvider} for more details.
 *
 * <p>
 * The following is a list of the allowed extension points:
 *
 * <table border="1" summary="">
 *   <tr>
 *     <th>Type</th>
 *     <th>Property</th>
 *     <th>Scope</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.views.velocity.VelocityManager</td>
 *     <td>struts.velocity.manager.classname</td>
 *     <td>singleton</td>
 *     <td>Loads and processes Velocity templates</td>
 *   </tr>
 * </table>
 */
public class VelocityBeanSelectionProvider extends AbstractBeanSelectionProvider {

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        alias(VelocityManager.class, VelocityConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME, builder, props);
    }

}
