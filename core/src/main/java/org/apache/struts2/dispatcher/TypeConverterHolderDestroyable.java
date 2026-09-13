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

import org.apache.struts2.conversion.TypeConverterHolder;
import org.apache.struts2.inject.Inject;

/**
 * Clears the {@link TypeConverterHolder} in use. Registered as a separate bean rather than making
 * the holder itself an {@link InternalDestroyable}: the container keys singletons by (type, name),
 * so a second registration of the holder class would build a second, empty instance.
 *
 * @since 7.4.0
 */
public class TypeConverterHolderDestroyable implements InternalDestroyable {

    private TypeConverterHolder typeConverterHolder;

    @Inject
    public void setTypeConverterHolder(TypeConverterHolder typeConverterHolder) {
        this.typeConverterHolder = typeConverterHolder;
    }

    @Override
    public void destroy() {
        typeConverterHolder.clearCache();
    }
}
