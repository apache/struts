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
package org.apache.struts2.tiles;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.tiles.annotation.TilesAddAttribute;
import org.apache.struts2.tiles.annotation.TilesAddListAttribute;
import org.apache.struts2.tiles.annotation.TilesDefinition;
import org.apache.struts2.tiles.annotation.TilesDefinitions;
import org.apache.struts2.tiles.annotation.TilesPutAttribute;
import org.apache.struts2.tiles.annotation.TilesPutListAttribute;
import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.Expression;
import org.apache.tiles.ListAttribute;

/**
 * Processes tiles annotations to create {@link Definition}s and
 * {@link Attribute}s in a way as close to <code>tiles.xml</code> as possible.
 *
 */
public class StrutsTilesAnnotationProcessor {

    /**
     * Search strategy is as follows:
     * <ul>
     *   <li>Check if action has Annotation {@link TilesDefinition}</li>
     *   <li>If not, check if action has Annotation {@link TilesDefinitions}</li>
     *   <li>If given tileName is not null and present in {@link TilesDefinitions}, return it</li>
     *   <li>Return first element of {@link TilesDefinitions}</li>
     *   <li>Return null</li>
     * </ul>
     *
     * @param action
     *            Annotated action.
     * @param tileName
     *            Tilename to search for. May be null in some circumstances.
     * @return {@link TilesDefinition}
     */
    public TilesDefinition findAnnotation(Object action, String tileName) {
        Class<?> clazz = action.getClass();
        TilesDefinition tilesDefinition = clazz.getAnnotation(TilesDefinition.class);
        TilesDefinitions tilesDefinitions = clazz.getAnnotation(TilesDefinitions.class);

        if (tilesDefinition == null && tilesDefinitions != null) {
            if (!StringUtils.isEmpty(tileName)) {
                for (TilesDefinition i : tilesDefinitions.value()) {
                    if (i.name().equals(tileName)) {
                        tilesDefinition = i;
                        break;
                    }
                }
            } else {
                if (tilesDefinitions.value().length > 0) {
                    tilesDefinition = tilesDefinitions.value()[0];
                }
            }
        }

        return tilesDefinition;
    }

    /**
     * Builds a {@link Definition} based on given {@link TilesDefinition} with
     * given name.
     *
     * @param tileName
     *            name for resulting {@link Definition}.
     * @param tilesDefinition
     *            {@link TilesDefinition} to process.
     * @return {@link Definition} represented by given {@link TilesDefinition}.
     */
    public Definition buildTilesDefinition(String tileName, TilesDefinition tilesDefinition) {
        Definition definition = new Definition();

        definition.setName(tileName);

        String extend = getValueOrNull(tilesDefinition.extend());
        if (extend != null) {
            definition.setExtends(extend);
        }
        String preparer = getValueOrNull(tilesDefinition.preparer());
        if (preparer != null) {
            definition.setPreparer(preparer);
        }
        definition.setTemplateAttribute(buildTemplateAttribute(tilesDefinition));

        for (TilesPutAttribute putAttribute : tilesDefinition.putAttributes()) {
            Attribute attribute = buildPutAttribute(putAttribute);
            definition.putAttribute(putAttribute.name(), attribute, putAttribute.cascade());
        }
        for (TilesPutListAttribute putListAttribute : tilesDefinition.putListAttributes()) {
            Attribute attribute = buildPutListAttribute(putListAttribute);
            definition.putAttribute(putListAttribute.name(), attribute, putListAttribute.cascade());
        }

        return definition;
    }

    protected Attribute buildTemplateAttribute(TilesDefinition tilesDef) {
        // see tiles DigesterDefinitionsReader
        Attribute attribute = Attribute.createTemplateAttribute(getValueOrNull(tilesDef.template()));
        String templateExpression = getValueOrNull(tilesDef.templateExpression());
        Expression expression = Expression.createExpressionFromDescribedExpression(templateExpression);
        attribute.setExpressionObject(expression);
        attribute.setRole(getValueOrNull(tilesDef.role()));
        String templateType = getValueOrNull(tilesDef.templateType());
        if (templateType != null) {
            attribute.setRenderer(templateType);
        } else if (getValueOrNull(tilesDef.extend()) != null && templateType == null) {
            attribute.setRenderer(null);
        }
        return attribute;
    }

    protected Attribute buildPutAttribute(TilesPutAttribute putAttribute) {
        Attribute attribute = new Attribute();
        attribute.setValue(getValueOrNull(putAttribute.value()));
        String expression = getValueOrNull(putAttribute.expression());
        attribute.setExpressionObject(Expression.createExpressionFromDescribedExpression(expression));
        attribute.setRole(getValueOrNull(putAttribute.role()));
        attribute.setRenderer(getValueOrNull(putAttribute.type()));
        return attribute;
    }

    protected Attribute buildPutListAttribute(TilesPutListAttribute putListAttribute) {
        ListAttribute attribute = new ListAttribute();
        attribute.setRole(getValueOrNull(putListAttribute.role()));
        attribute.setInherit(putListAttribute.inherit());
        for (TilesAddAttribute addAttribute : putListAttribute.addAttributes()) {
            attribute.add(buildAddAttribute(addAttribute));
        }
        for (TilesAddListAttribute addListAttribute : putListAttribute.addListAttributes()) {
            attribute.add(buildAddListAttribute(addListAttribute));
        }
        return attribute;
    }

    protected Attribute buildAddAttribute(TilesAddAttribute addAttribute) {
        Attribute attribute = new Attribute();
        attribute.setValue(getValueOrNull(addAttribute.value()));
        String expression = getValueOrNull(addAttribute.expression());
        attribute.setExpressionObject(Expression.createExpressionFromDescribedExpression(expression));
        attribute.setRole(getValueOrNull(addAttribute.role()));
        attribute.setRenderer(getValueOrNull(addAttribute.type()));
        return attribute;
    }

    protected Attribute buildAddListAttribute(TilesAddListAttribute addListAttribute) {
        ListAttribute attribute = new ListAttribute();
        attribute.setRole(getValueOrNull(addListAttribute.role()));
        for (TilesAddAttribute addAttribute : addListAttribute.addAttributes()) {
            attribute.add(buildAddAttribute(addAttribute));
        }
        return attribute;
    }

    protected String getValueOrNull(String value) {
        return value != null && value.length() > 0 ? value : null;
    }
}
