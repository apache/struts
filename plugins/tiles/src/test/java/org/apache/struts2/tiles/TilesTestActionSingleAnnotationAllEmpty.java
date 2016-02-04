package org.apache.struts2.tiles;

import org.apache.struts2.tiles.annotation.TilesAddAttribute;
import org.apache.struts2.tiles.annotation.TilesAddListAttribute;
import org.apache.struts2.tiles.annotation.TilesDefinition;
import org.apache.struts2.tiles.annotation.TilesPutAttribute;
import org.apache.struts2.tiles.annotation.TilesPutListAttribute;

@TilesDefinition(
        putAttributes = {
                @TilesPutAttribute(name = "put-attr")
        },
        putListAttributes = {
                @TilesPutListAttribute(
                        name = "list-name",
                        addAttributes = {
                                @TilesAddAttribute()
                        },
                        addListAttributes = {
                                @TilesAddListAttribute(
                                        addAttributes = {@TilesAddAttribute()})
                        }
                )
        }
)
public class TilesTestActionSingleAnnotationAllEmpty {

}
