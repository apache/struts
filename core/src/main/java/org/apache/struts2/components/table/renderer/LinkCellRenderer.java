/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.components.table.renderer;

import org.apache.struts2.components.table.WebTable;


/**
 */
public class LinkCellRenderer extends AbstractCellRenderer {

    /**
     * this is the actual renderer tha will be used to display the text
     */
    protected CellRenderer _delegateRenderer = new DefaultCellRenderer();

    /**
     * the CSS class this link belongs to. Optional
     */
    protected String _cssClass = null;

    /**
     * the id attribute this link belongs to. Optional
     */
    protected String _cssId = null;

    /**
     * this is the link we are setting (required)
     */
    protected String _link = null;

    /**
     * the (Java)script/ function to execute when the link is clicked. Optional
     */
    protected String _onclick = null;

    /**
     * the (Java)script/ function to execute when the link is clicked twice. Optional
     */
    protected String _ondblclick = null;

    /**
     * the (Java)script/ function to execute when cursor is away from the link. Optional
     */
    protected String _onmouseout = null;

    /**
     * the (Java)script/ function to execute when cursor is over the link. Optional
     */
    protected String _onmouseover = null;

    /**
     * if set there will be a parameter attached to link.  (optional)
     * This should be extended to allow multiple parameters
     */
    protected String _param = null;

    /**
     * directly set the value for the param.  Will overide paramColumn if set.
     * optional.  Either this or paramColumn must be set if param is used.
     * Will be ignored if param not used
     */
    protected String _paramValue = null;

    /**
     * the target frame to open in. Optional
     */
    protected String _target = null;

    /**
     * the title attribute this link belongs to. Optional
     */
    protected String _title = null;

    /**
     * additional parameters after the above parameter is generated. Optional
     */
    protected String _trailParams = null;

    /**
     * if used the param value will be taken from another column in the table.  Useful if each row
     * needs a different paramter.  The paramter can be taken from a hidden cell.
     * if paramValue is also set it will overrid this.  (option either this or paramValue must be set
     * if param is used. Will be ignored if param not used
     */
    protected int _paramColumn = -1;


    public LinkCellRenderer() {
    }


    /**
     * should the link data be encodeed?
     */
    public String getCellValue(WebTable table, Object data, int row, int col) {
        String value = _delegateRenderer.renderCell(table, data, row, col);

        StringBuffer cell = new StringBuffer(256);
        cell.append("<a href='").append(_link);

        if (_param != null) {
            cell.append("?").append(_param).append("=");

            if (_paramValue != null) {
                cell.append(_paramValue);
            } else if (_paramColumn >= 0) {
                cell.append(table.getModel().getValueAt(row, _paramColumn).toString());
            }
        }

        if ((_trailParams != null) && !"".equals(_trailParams)) {
            if (_param == null) {
                cell.append("?");
            } else {
                cell.append("&");
            }

            cell.append(_trailParams);
        }

        cell.append("'");

        if ((_target != null) && (!"".equals(_target))) {
            cell.append(" target='").append(_target).append("'");
        }

        if ((_cssClass != null) && (!"".equals(_cssClass))) {
            cell.append(" class='").append(_cssClass).append("'");
        }

        if ((_cssId != null) && (!"".equals(_cssId))) {
            cell.append(" id='").append(_cssId).append("'");
        }

        if ((_title != null) && (!"".equals(_title))) {
            cell.append(" title='").append(_title).append("'");
        }

        if ((_onclick != null) && (!"".equals(_onclick))) {
            cell.append(" onclick='").append(_onclick).append("'");
        }

        if ((_ondblclick != null) && (!"".equals(_ondblclick))) {
            cell.append(" ondblclick='").append(_ondblclick).append("'");
        }

        if ((_onmouseover != null) && (!"".equals(_onmouseover))) {
            cell.append(" onmouseover='").append(_onmouseover).append("'");
        }

        if ((_onmouseout != null) && (!"".equals(_onmouseout))) {
            cell.append(" onmouseout='").append(_onmouseout).append("'");
        }

        cell.append(">").append(value).append("</a>");

        return cell.toString();
    }

    public void setCssClass(String cssClass) {
        _cssClass = cssClass;
    }

    public void setCssId(String cssId) {
        _cssId = cssId;
    }

    public void setLink(String link) {
        _link = link;
    }

    public void setOnclick(String onclick) {
        _onclick = onclick;
    }

    public void setOndblclick(String ondblclick) {
        _ondblclick = ondblclick;
    }

    public void setOnmouseout(String onmouseout) {
        _onmouseout = onmouseout;
    }

    public void setOnmouseover(String onmouseover) {
        _onmouseover = onmouseover;
    }

    public void setParam(String param) {
        _param = param;
    }

    public void setParamColumn(int paramColumn) {
        _paramColumn = paramColumn;
    }

    public void setParamValue(String paramValue) {
        _paramValue = paramValue;
    }

    /**
     * used to set the renderer to delgate to.
     * if the render is an AbstractCellRenderer then it will take the alignment from
     * the delegate renderer and set it that way.
     */
    public void setRenderer(CellRenderer delegateRenderer) {
        _delegateRenderer = delegateRenderer;

        if (_delegateRenderer instanceof AbstractCellRenderer) {
            setAlignment(((AbstractCellRenderer) _delegateRenderer).getAlignment());
        }
    }

    public void setTarget(String target) {
        _target = target;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public void setTrailParams(String trailParams) {
        _trailParams = trailParams;
    }
}
