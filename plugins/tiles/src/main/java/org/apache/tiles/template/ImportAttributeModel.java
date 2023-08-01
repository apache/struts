/*
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
package org.apache.tiles.template;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.request.Request;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <strong>Import attribute(s) in specified context.</strong>
 * </p>
 * <p>
 * Import attribute(s) to requested scope. Attribute name and scope are
 * optional. If not specified, all attributes are imported in page scope. Once
 * imported, an attribute can be used as any other beans from jsp contexts.
 * </p>
 *
 * @since 2.2.0
 */
public class ImportAttributeModel {

    /**
     * The logging object.
     */
    private static final Logger LOG = LogManager.getLogger(ImportAttributeModel.class);

    /**
     * Executes the model.
     *
     * @param name    The name of the attribute to import. If it is
     *                <code>null</code>, all the attributes will be imported.
     * @param scope   The scope into which the attribute(s) will be imported. If
     *                <code>null</code>, the import will go in page scope.
     * @param toName  The name of the attribute into which the attribute will be
     *                imported. To be used in conjunction to <code>name</code>. If
     *                <code>null</code>, the value of <code>name</code> will be used.
     * @param ignore  If <code>true</code>, if the attribute is not present, the
     *                problem will be ignored.
     * @param request The request.
     */
    public void execute(
        String name,
        String scope,
        String toName,
        boolean ignore,
        Request request
    ) {
        Map<String, Object> attributes = getImportedAttributes(name, toName, ignore, request);
        if (scope == null) {
            scope = request.getAvailableScopes().get(0);
        }
        request.getContext(scope).putAll(attributes);
    }

    /**
     * Retuns a Map that contains the attributes to be imported. The importing
     * code must be done by the caller.
     *
     * @param name    The attribute to import. If null, all the attributes will be
     *                imported.
     * @param toName  The destination name of the attribute to import. Valid only
     *                if <code>name</code> is specified.
     * @param ignore  If <code>true</code> and the attribute is not found, or an
     *                exception happens, the problem will be ignored.
     * @param request The request.
     * @return A Map of the attributes to be imported: the key is the name of an
     * attribute, the value is the value of that attribute.
     * @since 2.2.0
     */
    private Map<String, Object> getImportedAttributes(String name, String toName, boolean ignore, Request request) {
        TilesContainer container = TilesAccess.getCurrentContainer(request);
        Map<String, Object> retValue = new HashMap<>();
        AttributeContext attributeContext = container.getAttributeContext(request);
        // Some tags allow for unspecified attributes. This
        // implies that the tag should use all the attributes.
        if (name != null) {
            importSingleAttribute(container, attributeContext, name, toName, ignore, retValue, request);
        } else {
            importAttributes(attributeContext.getCascadedAttributeNames(), container, attributeContext, retValue, ignore, request);
            importAttributes(attributeContext.getLocalAttributeNames(), container, attributeContext, retValue, ignore, request);
        }
        return retValue;
    }

    /**
     * Imports a single attribute.
     *
     * @param container        The Tiles container to use.
     * @param attributeContext The context from which the attributes will be
     *                         got.
     * @param name             The name of the attribute.
     * @param toName           The name of the destination attribute. If null,
     *                         <code>name</code> will be used.
     * @param ignore           If <code>true</code> and the attribute is not found, or an
     *                         exception happens, the problem will be ignored.
     * @param attributes       The map of the attributes to fill.
     * @param request          The request.
     */
    private void importSingleAttribute(
        TilesContainer container,
        AttributeContext attributeContext,
        String name, String toName,
        boolean ignore,
        Map<String, Object> attributes,
        Request request
    ) {
        Attribute attr = attributeContext.getAttribute(name);
        if (attr != null) {
            try {
                Object attributeValue = container.evaluate(attr,
                    request);
                if (attributeValue == null) {
                    if (!ignore) {
                        throw new NoSuchAttributeException("Error importing attributes. " + "Attribute '" + name + "' has a null value ");
                    }
                } else {
                    if (toName != null) {
                        attributes.put(toName, attributeValue);
                    } else {
                        attributes.put(name, attributeValue);
                    }
                }
            } catch (RuntimeException e) {
                if (!ignore) {
                    throw e;
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring Tiles Exception", e);
                }
            }
        } else if (!ignore) {
            throw new NoSuchAttributeException("Error importing attributes. " + "Attribute '" + name + "' is null");
        }
    }

    /**
     * Imports all the attributes.
     *
     * @param names            The names of the attributes to be imported.
     * @param container        The Tiles container to use.
     * @param attributeContext The context from which the attributes will be
     *                         got.
     * @param attributes       The map of the attributes to fill.
     * @param ignore           If <code>true</code> and the attribute is not found, or an
     *                         exception happens, the problem will be ignored.
     * @param request          The request.
     */
    private void importAttributes(
        Collection<String> names,
        TilesContainer container,
        AttributeContext attributeContext,
        Map<String, Object> attributes,
        boolean ignore,
        Request request
    ) {
        if (names == null || names.isEmpty()) {
            return;
        }

        for (String name : names) {
            if (name == null && !ignore) {
                throw new NoSuchAttributeException("Error importing attributes. Attribute with null key found.");
            } else if (name == null) {
                continue;
            }

            importSingleAttribute(container, attributeContext, name, name, ignore, attributes, request);
        }
    }
}
