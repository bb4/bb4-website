package com.becker.ui.table;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * Copied with modification from from Graphic Java (Swing) book (by David Geary).
 */
public abstract class AbstractCellEditor
                implements TableCellEditor, TreeCellEditor {

    protected EventListenerList listenerList_ =
                                        new EventListenerList();
    protected Object value_;
    protected ChangeEvent changeEvent_ = null;
    protected int clickCountToStart_ = 1;

    public Object getCellEditorValue() {
        return value_;
    }

    public void setCellEditorValue(Object value) {
        this.value_ = value;
    }

    public void setClickCountToStart(int count) {
        clickCountToStart_ = count;
    }

    public int getClickCountToStart() {
        return clickCountToStart_;
    }

    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            if (((MouseEvent)anEvent).getClickCount() <
                                                clickCountToStart_)
                return false;
        }
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    public void addCellEditorListener(CellEditorListener l) {
        listenerList_.add(CellEditorListener.class, l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        listenerList_.remove(CellEditorListener.class, l);
    }

    public Component getTreeCellEditorComponent(
                        JTree tree, Object value,
                        boolean isSelected, boolean expanded,
                        boolean leaf, int row) {
        return null;
    }


    protected void fireEditingStopped() {
        Object[] listeners = listenerList_.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == CellEditorListener.class) {
                if (changeEvent_ == null)
                    changeEvent_ = new ChangeEvent(this);
                ((CellEditorListener)
                listeners[i+1]).editingStopped(changeEvent_);
            }
        }
    }

    protected void fireEditingCanceled() {
        Object[] listeners = listenerList_.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==CellEditorListener.class) {
                if (changeEvent_ == null)
                    changeEvent_ = new ChangeEvent(this);
                ((CellEditorListener)
                listeners[i+1]).editingCanceled(changeEvent_);
            }
        }
    }
}
