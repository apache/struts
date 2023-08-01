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

package org.apache.tiles.core.definition.pattern;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Definition;
import org.apache.tiles.api.Expression;
import org.apache.tiles.api.ListAttribute;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for pattern matching and substitution.
 *
 * @since 2.2.0
 */
public final class PatternUtil {

    /**
     * The root locale. Notice that this is a replacement for {@link Locale#ROOT} for
     * Java 1.6.
     */
    private static final Locale ROOT_LOCALE = new Locale("", "");

    /** Pattern to find {.*} occurrences that do not match {[0-9]+} so to prevent MessageFormat from crashing.
     */
    private static final Pattern INVALID_FORMAT_ELEMENT = Pattern.compile("\\{[^}0-9]+}");

    /**
     * Private constructor to avoid instantiation.
     */
    private PatternUtil() {
    }

    /**
     * Creates a definition given its representation with wildcards and
     * attribute values with placeholders, replacing real values into
     * placeholders.
     *
     * @param d The definition to replace.
     * @param name The name of the definition to be created.
     * @param varsOrig The variables to be substituted.
     * @return The definition that can be rendered.
     * @since 2.2.0
     */
    public static Definition replacePlaceholders(Definition d, String name,
                                                 Object... varsOrig) {

        Object[] vars = replaceNullsWithBlank(varsOrig);

        Definition nudef = new Definition();

        nudef.setExtends(replace(d.getExtends(), vars));
        nudef.setName(name);
        nudef.setPreparer(replace(d.getPreparer(), vars));
        Attribute templateAttribute = d.getTemplateAttribute();
        if (templateAttribute != null) {
            nudef.setTemplateAttribute(replaceVarsInAttribute(
                    templateAttribute, vars));
        }

        Set<String> attributeNames = d.getLocalAttributeNames();
        if (attributeNames != null && !attributeNames.isEmpty()) {
            for (String attributeName : attributeNames) {
                Attribute attr = d.getLocalAttribute(attributeName);
                Attribute nuattr = replaceVarsInAttribute(attr, vars);

                nudef.putAttribute(replace(attributeName, vars), nuattr);
            }
        }

        attributeNames = d.getCascadedAttributeNames();
        if (attributeNames != null && !attributeNames.isEmpty()) {
            for (String attributeName : attributeNames) {
                Attribute attr = d.getCascadedAttribute(attributeName);
                Attribute nuattr = replaceVarsInAttribute(attr, vars);

                nudef.putAttribute(replace(attributeName, vars), nuattr, true);
            }
        }

        return nudef;
    }

    /**
     * Creates a new map that contains all the entries of the
     * <code>defsMap</code> whose keys are contained in <code>keys</code>.
     *
     * @param map The map to read.
     * @param keys The keys to extract.
     * @param <K> The key of the map.
     * @param <V> The value of the map.
     * @return The extracted map.
     * @since 2.2.1
     */
    public static <K, V> Map<K, V> createExtractedMap(Map<K, V> map, Set<K> keys) {
        Map<K, V> retValue = new LinkedHashMap<>();
        for (K key : keys) {
            retValue.put(key, map.get(key));
        }
        return retValue;
    }

    /**
     * Replaces variables into an attribute.
     *
     * @param attr The attribute to be used as a basis, containing placeholders
     * for variables.
     * @param vars The variables to replace.
     * @return A new instance of an attribute, whose properties have been
     * replaced with variables' values.
     */
    private static Attribute replaceVarsInAttribute(Attribute attr,
            Object... vars) {
        Attribute nuattr;
        if (attr instanceof ListAttribute) {
            nuattr = replaceVarsInListAttribute((ListAttribute) attr, vars);
        } else {
            nuattr = replaceVarsInSimpleAttribute(attr, vars);
        }
        return nuattr;
    }

    /**
     * Replaces variables into a simple (not list) attribute.
     *
     * @param attr The attribute to be used as a basis, containing placeholders
     * for variables.
     * @param vars The variables to replace.
     * @return A new instance of an attribute, whose properties have been
     * replaced with variables' values.
     */
    private static Attribute replaceVarsInSimpleAttribute(Attribute attr,
            Object... vars) {
        Attribute nuattr;
        nuattr = new Attribute();

        nuattr.setRole(replace(attr.getRole(), vars));
        nuattr.setRenderer(attr.getRenderer());
        Expression expressionObject = attr.getExpressionObject();
        if (expressionObject != null) {
            Expression newExpressionObject = Expression
                    .createExpression(replace(expressionObject.getExpression(), vars), expressionObject.getLanguage());
            nuattr.setExpressionObject(newExpressionObject);
        }

        Object value = attr.getValue();
        if (value instanceof String) {
            value = replace((String) value, vars);
        }
        nuattr.setValue(value);
        return nuattr;
    }

    /**
     * Replaces variables into a list attribute.
     *
     * @param listAttr The attribute to be used as a basis, containing attributes
     * that may contain placeholders for variables.
     * @param vars The variables to replace.
     * @return A new instance of an attribute, whose properties have been
     * replaced with variables' values.
     */
    private static Attribute replaceVarsInListAttribute(ListAttribute listAttr,
            Object... vars) {
        Attribute nuattr;
        ListAttribute nuListAttr = new ListAttribute();
        nuListAttr.setInherit(listAttr.isInherit());
        List<Attribute> nuItems = nuListAttr.getValue();
        for (Attribute item : listAttr.getValue()) {
            Attribute child = item;
            child = replaceVarsInAttribute(child, vars);
            nuItems.add(child);
        }
        nuattr = nuListAttr;
        return nuattr;
    }

    /**
     * Replaces a string with placeholders using values of a variable map.
     *
     * @param st The string to replace.
     * @param vars The variables.
     * @return The replaced string.
     */
    private static String replace(String st, Object... vars) {
        if (st != null && st.indexOf('{') >= 0) {

            // replace them with markers
            List<String> originals = new ArrayList<>();
            for(Matcher m = INVALID_FORMAT_ELEMENT.matcher(st); m.find() ; m = INVALID_FORMAT_ELEMENT.matcher(st)) {
                originals.add(m.group());
                st = m.replaceFirst("INVALID_FORMAT_ELEMENT");
            }

            // do the MessageFormat replacement (escaping quote characters)
            st = new MessageFormat(st.replaceAll("'", "'''"), ROOT_LOCALE)
                    .format(vars, new StringBuffer(), null).toString();

            // return the markers to their original invalid occurrences
            for (String original : originals) {
                st = st.replaceFirst("INVALID_FORMAT_ELEMENT", original);
            }
        }
        return st;
    }

    private static Object[] replaceNullsWithBlank(Object[] varsOrig) {
        Object[] vars = new Object[varsOrig.length];
        for(int i = 0; i < varsOrig.length; ++i) {
            vars[i] = null != varsOrig[i] ? varsOrig[i] : "";
        }
        return vars;
    }
}
