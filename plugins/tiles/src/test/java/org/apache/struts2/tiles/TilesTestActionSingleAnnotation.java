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

import org.apache.struts2.tiles.annotation.TilesAddAttribute;
import org.apache.struts2.tiles.annotation.TilesAddListAttribute;
import org.apache.struts2.tiles.annotation.TilesDefinition;
import org.apache.struts2.tiles.annotation.TilesPutAttribute;
import org.apache.struts2.tiles.annotation.TilesPutListAttribute;

@TilesDefinition(
        name = "definition-name", 
        extend = "base-definition", 
        preparer = "preparer", 
        role = "role", 
        template = "template", 
        templateExpression = "templ*", 
        templateType = "type",
        putAttributes = {
                @TilesPutAttribute(
                        cascade = true, 
                        expression = "lang:expr", 
                        name = "put-attr", 
                        role = "attr-role", 
                        type = "attr-type", 
                        value = "attr-val")
        },
        putListAttributes = {
                @TilesPutListAttribute(
                        cascade = true, 
                        inherit = true, 
                        name = "list-name", 
                        role = "list-role",
                        addAttributes = {
                                @TilesAddAttribute(
                                        expression = "list-attr-expr", 
                                        role = "list-attr-role", 
                                        type = "list-attr-type", 
                                        value = "list-attr-val")
                        },
                        addListAttributes = {
                                @TilesAddListAttribute(
                                        role = "list-list-attr-role", 
                                        addAttributes = {@TilesAddAttribute("list-list-add-attr")})
                        }
                )
        }
)
public class TilesTestActionSingleAnnotation {

}
