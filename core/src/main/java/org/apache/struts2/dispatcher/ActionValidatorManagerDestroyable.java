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
package org.apache.struts2.dispatcher;

import org.apache.struts2.inject.Inject;
import org.apache.struts2.validator.ActionValidatorManager;

/**
 * Clears the {@link ActionValidatorManager} in use. Registered as a separate bean rather than
 * making the manager itself an {@link InternalDestroyable}: the container keys singletons by
 * (type, name), so a second registration of the manager class would build a second, empty instance.
 *
 * @since 7.4.0
 */
public class ActionValidatorManagerDestroyable implements InternalDestroyable {

    private ActionValidatorManager actionValidatorManager;

    @Inject
    public void setActionValidatorManager(ActionValidatorManager actionValidatorManager) {
        this.actionValidatorManager = actionValidatorManager;
    }

    @Override
    public void destroy() {
        actionValidatorManager.clearCache();
    }
}
