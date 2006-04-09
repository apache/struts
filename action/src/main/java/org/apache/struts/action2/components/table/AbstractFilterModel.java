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

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.Vector;


/**
 * @author Onyeje Bose
 * @version 1.0
 */
abstract public class AbstractFilterModel extends AbstractTableModel {

    protected TableModel model;


    public AbstractFilterModel(TableModel tm) {
        model = tm;
    }


    public boolean isCellEditable(int par1, int par2) {
        return model.isCellEditable(par1, par2);
    }

    public Class getColumnClass(int par1) {
        return model.getColumnClass(par1);
    }

    public int getColumnCount() {
        return model.getColumnCount();
    }

    public String getColumnName(int par1) {
        return model.getColumnName(par1);
    }

    public void setModel(TableModel model) {
        this.model = model;
        this.fireTableDataChanged();
    }

    public TableModel getModel() {
        return model;
    }

    public int getRowCount() {
        return model.getRowCount();
    }

    public void setValueAt(Object par1, int par2, int par3) {
        model.setValueAt(par1, par2, par3);
    }

    public Object getValueAt(int par1, int par2) {
        return model.getValueAt(par1, par2);
    }

    public void addRow(Vector data) throws IllegalStateException {
        if (model instanceof DefaultTableModel) {
            ((DefaultTableModel) model).addRow(data);
        } else if (model instanceof AbstractFilterModel) {
            ((AbstractFilterModel) model).addRow(data);
        } else {
            throw (new IllegalStateException("Error attempting to add a row to an underlying model that is not a DefaultTableModel."));
        }
    }

    public void removeAllRows() throws ArrayIndexOutOfBoundsException, IllegalStateException {
        while (this.getRowCount() > 0) {
            this.removeRow(0);
        }
    }

    public void removeRow(int rowNum) throws ArrayIndexOutOfBoundsException, IllegalStateException {
        if (model instanceof DefaultTableModel) {
            ((DefaultTableModel) model).removeRow(rowNum);
        } else if (model instanceof AbstractFilterModel) {
            ((AbstractFilterModel) model).removeRow(rowNum);
        } else {
            throw (new IllegalStateException("Error attempting to remove a row from an underlying model that is not a DefaultTableModel."));
        }
    }
}
