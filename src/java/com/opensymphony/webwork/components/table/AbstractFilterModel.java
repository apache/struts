/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.components.table;

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
