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
package org.apache.tiles.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Basic implementation for <code>AttributeContext</code>.
 *
 * @since 2.1.0
 */
public class BasicAttributeContext implements AttributeContext, Serializable {

    /**
     * The template attribute, to render a template.
     *
     * @since 2.1.2
     */
    protected Attribute templateAttribute;

    /**
     * Associated ViewPreparer URL or classname, if defined.
     *
     * @since 2.1.0
     */
    protected String preparer = null;

    /**
     * Template attributes.
     *
     * @since 2.1.0
     */
    protected Map<String, Attribute> attributes = null;

    /**
     * Cascaded template attributes.
     *
     * @since 2.1.0
     */
    protected Map<String, Attribute> cascadedAttributes = null;

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    public BasicAttributeContext() {
    }

    /**
     * Constructor.
     * Create a context and set specified attributes.
     *
     * @param attributes Attributes to initialize context.
     * @since 2.1.0
     */
    public BasicAttributeContext(Map<String, Attribute> attributes) {
        if (attributes != null) {
            this.attributes = deepCopyAttributeMap(attributes);
        }
    }

    /**
     * Copy constructor.
     *
     * @param context The constructor to copy.
     * @since 2.1.0
     */
    public BasicAttributeContext(AttributeContext context) {
        if (context instanceof BasicAttributeContext) {
            copyBasicAttributeContext((BasicAttributeContext) context);
        } else {
            Attribute parentTemplateAttribute = context.getTemplateAttribute();
            if (parentTemplateAttribute != null) {
                this.templateAttribute = new Attribute(parentTemplateAttribute);
            }
            this.preparer = context.getPreparer();
            this.attributes = new HashMap<>();
            Set<String> parentAttributeNames = context.getLocalAttributeNames();
            if (parentAttributeNames != null) {
                for (String name : parentAttributeNames) {
                    attributes.put(name, new Attribute(context.getLocalAttribute(name)));
                }
            }
            inheritCascadedAttributes(context);
        }
    }

    /**
     * Copy constructor.
     *
     * @param context The constructor to copy.
     * @since 2.1.0
     */
    public BasicAttributeContext(BasicAttributeContext context) {
        copyBasicAttributeContext(context);
    }

    /**
     * {@inheritDoc}
     */
    public Attribute getTemplateAttribute() {
        return templateAttribute;
    }

    /**
     * {@inheritDoc}
     */
    public void setTemplateAttribute(Attribute templateAttribute) {
        this.templateAttribute = templateAttribute;
    }

    /**
     * {@inheritDoc}
     */
    public String getPreparer() {
        return preparer;
    }

    /**
     * {@inheritDoc}
     */
    public void setPreparer(String url) {
        this.preparer = url;
    }

    /**
     * {@inheritDoc}
     */
    public void inheritCascadedAttributes(AttributeContext context) {
        if (context instanceof BasicAttributeContext) {
            if (((BasicAttributeContext) context).cascadedAttributes != null && !((BasicAttributeContext) context).cascadedAttributes.isEmpty()) {
                cascadedAttributes = deepCopyAttributeMap(((BasicAttributeContext) context).cascadedAttributes);
            }
        } else {
            this.cascadedAttributes = new HashMap<>();
            Set<String> parentAttributeNames = context.getCascadedAttributeNames();
            if (parentAttributeNames != null) {
                for (String name : parentAttributeNames) {
                    cascadedAttributes.put(name, new Attribute(context
                        .getCascadedAttribute(name)));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void inherit(AttributeContext parent) {
        if (parent instanceof BasicAttributeContext) {
            inherit((BasicAttributeContext) parent);
        } else {
            // Inheriting template, roles and preparer.
            Attribute parentTemplateAttribute = parent.getTemplateAttribute();
            inheritParentTemplateAttribute(parentTemplateAttribute);
            if (preparer == null) {
                preparer = parent.getPreparer();
            }

            // Inheriting attributes.
            Set<String> names = parent.getCascadedAttributeNames();
            if (names != null && !names.isEmpty()) {
                for (String name : names) {
                    Attribute attribute = parent.getCascadedAttribute(name);
                    Attribute destAttribute = getCascadedAttribute(name);
                    if (destAttribute == null) {
                        putAttribute(name, attribute, true);
                    } else if (attribute instanceof ListAttribute
                        && destAttribute instanceof ListAttribute
                        && ((ListAttribute) destAttribute).isInherit()) {
                        ((ListAttribute) destAttribute).inherit((ListAttribute) attribute);
                    }
                }
            }
            names = parent.getLocalAttributeNames();
            if (names != null && !names.isEmpty()) {
                for (String name : names) {
                    Attribute attribute = parent.getLocalAttribute(name);
                    Attribute destAttribute = getLocalAttribute(name);
                    if (destAttribute == null) {
                        putAttribute(name, attribute, false);
                    } else if (attribute instanceof ListAttribute
                        && destAttribute instanceof ListAttribute
                        && ((ListAttribute) destAttribute).isInherit()) {
                        ((ListAttribute) destAttribute).inherit((ListAttribute) attribute);
                    }
                }
            }
        }
    }

    /**
     * Inherits the attribute context, inheriting, i.e. copying if not present,
     * the attributes.
     *
     * @param parent The attribute context to inherit.
     * @since 2.1.0
     */
    public void inherit(BasicAttributeContext parent) {
        // Set template, roles and preparer if not set.
        inheritParentTemplateAttribute(parent.getTemplateAttribute());
        if (preparer == null) {
            preparer = parent.preparer;
        }

        // Sets attributes.
        cascadedAttributes = addMissingAttributes(parent.cascadedAttributes,
            cascadedAttributes);
        attributes = addMissingAttributes(parent.attributes, attributes);
    }

    /**
     * Add all attributes to this context.
     * Copies all the mappings from the specified attribute map to this context.
     * New attribute mappings will replace any mappings that this context had for any of the keys
     * currently in the specified attribute map.
     *
     * @param newAttributes Attributes to add.
     * @since 2.1.0
     */
    public void addAll(Map<String, Attribute> newAttributes) {
        if (newAttributes == null) {
            return;
        }

        if (attributes == null) {
            attributes = new HashMap<>(newAttributes);
            return;
        }

        attributes.putAll(newAttributes);
    }

    /**
     * {@inheritDoc}
     */
    public Attribute getAttribute(String name) {
        Attribute retValue = null;
        if (attributes != null) {
            retValue = attributes.get(name);
        }

        if (retValue == null && cascadedAttributes != null) {
            retValue = cascadedAttributes.get(name);
        }

        return retValue;
    }

    /**
     * {@inheritDoc}
     */
    public Attribute getLocalAttribute(String name) {
        if (attributes == null) {
            return null;
        }

        return attributes.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public Attribute getCascadedAttribute(String name) {
        if (cascadedAttributes == null) {
            return null;
        }

        return cascadedAttributes.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getLocalAttributeNames() {
        if (attributes != null && !attributes.isEmpty()) {
            return attributes.keySet();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getCascadedAttributeNames() {
        if (cascadedAttributes != null && !cascadedAttributes.isEmpty()) {
            return cascadedAttributes.keySet();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void putAttribute(String name, Attribute value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        attributes.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void putAttribute(String name, Attribute value, boolean cascade) {
        Map<String, Attribute> mapToUse;
        if (cascade) {
            if (cascadedAttributes == null) {
                cascadedAttributes = new HashMap<>();
            }
            mapToUse = cascadedAttributes;
        } else {
            if (attributes == null) {
                attributes = new HashMap<>();
            }
            mapToUse = attributes;
        }
        mapToUse.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        templateAttribute = null;
        preparer = null;
        attributes.clear();
        cascadedAttributes.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BasicAttributeContext)) {
            return false;
        }
        BasicAttributeContext bac = (BasicAttributeContext) obj;
        return Objects.equals(templateAttribute, bac.templateAttribute)
            && Objects.equals(preparer, bac.preparer)
            && Objects.equals(attributes, bac.attributes)
            && Objects.equals(cascadedAttributes, bac.cascadedAttributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(templateAttribute) + Objects.hashCode(preparer)
            + Objects.hashCode(attributes)
            + Objects.hashCode(cascadedAttributes);
    }

    /**
     * Inherits the parent template attribute.
     *
     * @param parentTemplateAttribute The parent template attribute.
     */
    private void inheritParentTemplateAttribute(
        Attribute parentTemplateAttribute) {
        if (parentTemplateAttribute != null) {
            if (templateAttribute == null) {
                templateAttribute = new Attribute(parentTemplateAttribute);
            } else {
                templateAttribute.inherit(parentTemplateAttribute);
            }
        }
    }

    /**
     * Copies a BasicAttributeContext in an easier way.
     *
     * @param context The context to copy.
     */
    private void copyBasicAttributeContext(BasicAttributeContext context) {
        Attribute parentTemplateAttribute = context.getTemplateAttribute();
        if (parentTemplateAttribute != null) {
            this.templateAttribute = new Attribute(parentTemplateAttribute);
        }
        preparer = context.preparer;
        if (context.attributes != null && !context.attributes.isEmpty()) {
            attributes = deepCopyAttributeMap(context.attributes);
        }
        if (context.cascadedAttributes != null && !context.cascadedAttributes.isEmpty()) {
            cascadedAttributes = deepCopyAttributeMap(context.cascadedAttributes);
        }
    }

    /**
     * Adds missing attributes to the destination map.
     *
     * @param source      The source attribute map.
     * @param destination The destination attribute map.
     * @return The destination attribute map if not null, a new one otherwise.
     */
    private Map<String, Attribute> addMissingAttributes(Map<String, Attribute> source, Map<String, Attribute> destination) {
        if (source != null && !source.isEmpty()) {
            if (destination == null) {
                destination = new HashMap<>();
            }
            for (Map.Entry<String, Attribute> entry : source.entrySet()) {
                String key = entry.getKey();
                Attribute destAttribute = destination.get(key);
                if (destAttribute == null) {
                    destination.put(key, entry.getValue());
                } else if (destAttribute instanceof ListAttribute
                    && entry.getValue() instanceof ListAttribute
                    && ((ListAttribute) destAttribute).isInherit()) {
                    ((ListAttribute) destAttribute)
                        .inherit((ListAttribute) entry.getValue());
                }
            }
        }

        return destination;
    }

    /**
     * Deep copies the attribute map, by creating clones (using copy
     * constructors) of the attributes.
     *
     * @param attributes The attribute map to copy.
     * @return The copied map.
     */
    private Map<String, Attribute> deepCopyAttributeMap(Map<String, Attribute> attributes) {
        Map<String, Attribute> retValue = new HashMap<>(attributes.size());
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            Attribute toCopy = entry.getValue();
            if (toCopy != null) {
                retValue.put(entry.getKey(), toCopy.copy());
            }
        }
        return retValue;
    }
}
