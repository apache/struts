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
package com.opensymphony.xwork2.inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class InitializableFactory<T> implements InternalFactory<T> {

    private static final Logger LOG = LogManager.getLogger(InitializableFactory.class);

    private InternalFactory<T> internalFactory;

    private InitializableFactory(InternalFactory<T> internalFactory) {
        this.internalFactory = internalFactory;
    }

    public static <T> InternalFactory<T> wrapIfNeeded(InternalFactory<T> internalFactory) {
        if (Initializable.class.isAssignableFrom(internalFactory.type())) {
            return new InitializableFactory<>(internalFactory);
        }
        return internalFactory;
    }

    @Override
    public T create(InternalContext context) {
        T instance = internalFactory.create(context);
        if (Initializable.class.isAssignableFrom(instance.getClass())) {
            Initializable.class.cast(instance).init();
        } else {
            LOG.error("Class {} is not marked as {}!", internalFactory.getClass().getName(), Initializable.class.getName());
        }
        return instance;
    }

    @Override
    public Class<? extends T> type() {
        return internalFactory.type();
    }
}
