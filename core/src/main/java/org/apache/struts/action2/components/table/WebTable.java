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
package org.apache.struts.action2.components.table;

import org.apache.struts.action2.components.GenericUIBean;
import org.apache.struts.action2.components.table.renderer.CellRenderer;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.table.TableModel;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @a2.tag name="table" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.table.WebTableTag"
 * description="Instantiate a JavaBean and place it in the context."
 */
public class WebTable extends GenericUIBean {
    private static final Log LOG = LogFactory.getLog(WebTable.class);

    final public static String TEMPLATE = "table";

    protected String sortOrder = SortableTableModel.NONE;
    protected String modelName = null;
    protected TableModel model = null;
    protected WebTableColumn[] columns = null;
    protected boolean sortableAttr = false;
    protected int sortColumn = -1;
    protected int curRow = 0;

    public WebTable(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public boolean end(Writer writer, String body) {
        if (sortableAttr && model instanceof SortableTableModel) {
            LOG.debug("we are looking for " + getSortColumnLinkName());

            String sortColumn = request.getParameter(getSortColumnLinkName());
            String sortOrder = request.getParameter(getSortOrderLinkName());

            try {
                if ((sortColumn != null) || (sortOrder != null)) {
                    if (sortColumn != null) {
                        try {
                            this.sortColumn = Integer.parseInt(sortColumn);
                        } catch (Exception ex) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("coudn't convert column, take default");
                            }
                        }
                    }

                    if (sortOrder != null) {
                        this.sortOrder = sortOrder;
                    }
                } else {
                    LOG.debug("no sorting info in the request");
                }

                if (this.sortColumn >= 0) {
                    LOG.debug("we have the sortColumn " + Integer.toString(this.sortColumn));
                    LOG.debug("we have the sortOrder " + this.sortOrder);

                    try {
                        ((SortableTableModel) model).sort(this.sortColumn, this.sortOrder);
                    } catch (Exception ex) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("couldn't sort the data");
                        }
                    }

                    LOG.debug("we just sorted the data");
                }
            } catch (Exception e) {
                LOG.error(e);
                throw new RuntimeException("Error with WebTable: " + toString(e));
            }
        }

        return super.end(writer, body);
    }

    public WebTableColumn getColumn(int index) {
        try {
            return (columns[index]);
        } catch (Exception E) {
            //blank
        }

        return null;
    }

    protected void evaluateExtraParams() {
        if (modelName != null) {
            modelName = findString(modelName);

            Object obj = stack.findValue(this.modelName);

            if (obj instanceof TableModel) {
                setModel((TableModel) obj);
            }
        }

        super.evaluateExtraParams();
    }

    protected int getNumberOfVisibleColumns() {
        int count = 0;

        for (int i = 0; i < columns.length; ++i) {
            if (!columns[i].isHidden()) {
                ++count;
            }
        }

        return count;
    }

    public int getColumnCount() {
        return (columns.length);
    }

    public void setColumnDisplayName(int column, String displayName) {
        columns[column].setDisplayName(displayName);
    }

    public void getColumnDisplayName(int column) {
        columns[column].getDisplayName();
    }

    public void setColumnHidden(int column, boolean hide) {
        columns[column].setHidden(hide);
    }

    public boolean isColumnHidden(int column) {
        return columns[column].isHidden();
    }

    public void setColumnRenderer(int column, CellRenderer renderer) {
        columns[column].setRenderer(renderer);
    }

    public CellRenderer getColumnRenderer(int column) {
        return columns[column].getRenderer();
    }

    public WebTableColumn[] getColumns() {
        return columns;
    }

    public String[] getFormattedRow(int row) {
        ArrayList data = new ArrayList(getNumberOfVisibleColumns());

        for (int i = 0; i < getColumnCount(); ++i) {
            if (columns[i].isVisible()) {
                data.add(columns[i].getRenderer().renderCell(this, model.getValueAt(row, i), row, i));
            }
        }

        return (String[]) data.toArray(new String[0]);
    }

    public void setModel(TableModel model) {
        this.model = model;
        columns = new WebTableColumn[this.model.getColumnCount()];

        for (int i = 0; i < columns.length; ++i) {
            columns[i] = new WebTableColumn(this.model.getColumnName(i), i);
        }

        if ((sortableAttr) && !(this.model instanceof SortableTableModel)) {
            this.model = new SortFilterModel(this.model);
        }
    }

    public TableModel getModel() {
        return (model);
    }

    /**
     * The name of model to use
     * @a2.tagattribute required="true" type="String"
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public Object getRawData(int row, int column) {
        return model.getValueAt(row, column);
    }

    public Iterator getRawDataRowIterator() {
        return new WebTableRowIterator(this, WebTableRowIterator.RAW_DATA);
    }

    public Object[] getRow(int row) {
        ArrayList data = new ArrayList(getNumberOfVisibleColumns());

        for (int i = 0; i < getColumnCount(); ++i) {
            if (columns[i].isVisible()) {
                data.add(model.getValueAt(row, i));
            }
        }

        return data.toArray(new Object[0]);
    }

    public int getRowCount() {
        return model.getRowCount();
    }

    public Iterator getRowIterator() {
        return new WebTableRowIterator(this);
    }

    /**
     * Index of column to sort data by
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setSortColumn(int sortColumn) {
        this.sortColumn = sortColumn;
    }

    public int getSortColumn() {
        if (model instanceof SortableTableModel) {
            return ((SortableTableModel) model).getSortedColumnNumber();
        }

        return -1;
    }

    public String getSortColumnLinkName() {
        return "WEBTABLE_" + modelName + "_SORT_COLUMN";
    }

    /**
     * Set sort order. Allowed values are NONE, ASC and DESC
     * @a2.tagattribute required="false" type="String" default="NONE"
     */
    public void setSortOrder(String sortOrder) {
        if (sortOrder.equals(SortableTableModel.NONE)) {
            this.sortOrder = SortableTableModel.NONE;
        } else if (sortOrder.equals(SortableTableModel.DESC)) {
            this.sortOrder = SortableTableModel.DESC;
        } else if (sortOrder.equals(SortableTableModel.ASC)) {
            this.sortOrder = SortableTableModel.ASC;
        } else {
            this.sortOrder = SortableTableModel.NONE;
        }
    }

    public String getSortOrder() {
        if ((model instanceof SortableTableModel) && (getSortColumn() >= 0)) {
            return ((SortableTableModel) model).getSortedDirection(getSortColumn());
        }

        return SortableTableModel.NONE;
    }

    public String getSortOrderLinkName() {
        return "WEBTABLE_" + modelName + "_SORT_ORDER";
    }

    /**
     * Whether the table should be sortable. Requires that model implements org.apache.struts.action2.components.table.SortableTableModel if set to true.
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setSortable(boolean sortable) {
        sortableAttr = sortable;

        if ((sortableAttr) && (model != null) && !(model instanceof SortableTableModel)) {
            model = new SortFilterModel(model);
        }
    }

    public boolean isSortable() {
        return sortableAttr;
    }

    /**
     * inner class to iteratoe over a row of the table.
     * It can return formatted data, using the columnRenderer
     * for the column or it can return the raw data.
     */
    public class WebTableRowIterator implements Iterator {
        public static final int FORMATTED_DATA = 0;
        public static final int RAW_DATA = 1;
        protected WebTable _table;
        protected int _curRow = 0;
        protected int _mode = 0;

        protected WebTableRowIterator(WebTable table) {
            this(table, FORMATTED_DATA);
        }

        protected WebTableRowIterator(WebTable table, int mode) {
            _table = table;
            _mode = mode;
        }

        public boolean hasNext() {
            if (_table == null) {
                return false;
            }

            return (_table.getRowCount() > _curRow);
        }

        public Object next() throws NoSuchElementException {
            if (_table == null) {
                throw new NoSuchElementException("WebTable is null");
            }

            if (!hasNext()) {
                throw new NoSuchElementException("Beyond end of WebTable");
            }

            if (_mode == RAW_DATA) {
                return _table.getRow(_curRow++);
            }

            return _table.getFormattedRow(_curRow++);
        }

        public void remove() throws UnsupportedOperationException, IllegalStateException {
            throw new UnsupportedOperationException("Remove not supported in WebTable");
        }
    }
}
