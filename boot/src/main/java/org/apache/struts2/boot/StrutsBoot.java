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
package org.apache.struts2.boot;

import org.apache.struts2.config.ConfigurationException;

import java.lang.reflect.InvocationTargetException;

public final class StrutsBoot {

    private StrutsBoot() {
    }

    public static void run(Class<? extends StrutsBootApplication> app, String... args) {
        start(app, args).await();
    }

    public static StrutsBootServer start(Class<? extends StrutsBootApplication> app, String... args) {
        ActionRegistry registry = new ActionRegistry();
        instantiate(app).actions(registry);
        return StrutsBootServer.start(registry, BootSettings.load(args));
    }

    private static StrutsBootApplication instantiate(Class<? extends StrutsBootApplication> app) {
        try {
            return app.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw new ConfigurationException("Cannot create " + app.getName() + ": its constructor threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new ConfigurationException("Cannot create " + app.getName()
                    + ": it needs a public no-argument constructor", e);
        }
    }
}
