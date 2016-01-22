package org.apache.struts2.showcase.tiles;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.tiles.annotation.TilesDefinition;
import org.apache.struts2.tiles.annotation.TilesPutAttribute;

import com.opensymphony.xwork2.ActionSupport;

@Namespace("/tiles")
@ParentPackage("tiles")
@Result(name = "success", type="tiles")
@TilesDefinition(extend = "showcase.annotations", putAttributes = {
        @TilesPutAttribute(name = "header", value = "/WEB-INF/tiles/header.jsp"),
        @TilesPutAttribute(name = "body", value = "/WEB-INF/tiles/body.ftl"), })
public class TilesAnnotationsAction extends ActionSupport {

    private static final long serialVersionUID = 2900509995064928866L;

}
