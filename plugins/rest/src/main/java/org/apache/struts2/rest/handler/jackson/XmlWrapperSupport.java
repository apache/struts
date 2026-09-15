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
package org.apache.struts2.rest.handler.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.dataformat.xml.deser.WrapperHandlingDeserializer;

/**
 * Jackson XML's wrapper for a bean with an unwrapped list cannot take a new delegatee; it recomputes
 * itself from the bean it is given when contextualized. {@code jackson-dataformat-xml} is optional
 * for the plugin, so the class that names it is only loaded once it is known to be present.
 */
final class XmlWrapperSupport {

    private static final boolean AVAILABLE = available();

    private XmlWrapperSupport() {
        // utility
    }

    private static boolean available() {
        try {
            Class.forName("com.fasterxml.jackson.dataformat.xml.deser.WrapperHandlingDeserializer",
                    false, XmlWrapperSupport.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError absent) {
            return false;
        }
    }

    static boolean isUnwrappedListWrapper(JsonDeserializer<?> deserializer) {
        return AVAILABLE && Xml.isUnwrappedListWrapper(deserializer);
    }

    /**
     * A {@code null} property keeps the re-contextualization from building the bean's id reader
     * over again; the property-specific state is already on the bean from its first one.
     */
    static <T> JsonDeserializer<T> rebuildAround(DeserializationContext ctxt, BeanDeserializerBase bean)
            throws JsonMappingException {
        return Xml.rebuildAround(ctxt, bean);
    }

    private static final class Xml {

        private Xml() {
        }

        static boolean isUnwrappedListWrapper(JsonDeserializer<?> deserializer) {
            return deserializer instanceof WrapperHandlingDeserializer;
        }

        @SuppressWarnings("unchecked")
        static <T> JsonDeserializer<T> rebuildAround(DeserializationContext ctxt, BeanDeserializerBase bean)
                throws JsonMappingException {
            return (JsonDeserializer<T>) new WrapperHandlingDeserializer(bean).createContextual(ctxt, null);
        }
    }
}
