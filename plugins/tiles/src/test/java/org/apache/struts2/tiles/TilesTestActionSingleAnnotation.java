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
