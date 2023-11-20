/*
 * $Id: VelocityRendererBuilder.java 1066512 2011-02-02 16:13:31Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.velocity.render;

import java.util.HashMap;
import java.util.Map;

import org.apache.tiles.request.ApplicationContext;
import org.apache.velocity.tools.view.VelocityView;

/**
 * Builds a VelocityRenderer.
 */
public final class VelocityRendererBuilder {

    /**
     * The initialization parameters for VelocityView.
     */
    private Map<String, String> params = new HashMap<String, String>();

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * Constructor.
     */
    private VelocityRendererBuilder() {
    }

    /**
     * Returns a new instance of the builder.
     *
     * @return A new builder.
     */
    public static VelocityRendererBuilder createInstance() {
        return new VelocityRendererBuilder();
    }

    /**
     * Sets a parameter for the internal servlet.
     *
     * @param key   The name of the parameter.
     * @param value The value of the parameter.
     * @return This builder.
     */
    public VelocityRendererBuilder setParameter(String key, String value) {
        params.put(key, value);
        return this;
    }

    /**
     * Sets the application context.
     *
     * @param applicationContext The application context.
     * @return This builder.
     */
    public VelocityRendererBuilder setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    /**
     * Creates the Velocity renderer.
     *
     * @return The Velocity renderer.
     */
    public VelocityRenderer build() {
        VelocityView velocityView = new VelocityView(new ApplicationContextJeeConfig(applicationContext, params));
        return new VelocityRenderer(velocityView);
    }
}
