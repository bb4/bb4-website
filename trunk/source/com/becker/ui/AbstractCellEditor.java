package com.becker.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * taken from Geary Swing book.
 */
public abstract class AbstractCellEditor
				implements TableCellEditor, TreeCellEditor {

	protected EventListenerList listenerList =
										new EventListenerList();
	protected Object value;
	protected ChangeEvent changeEvent = null;
	protected int clickCountToStart = 1;

	public Object getCellEditorValue() {
		return value;
	}

	public void setCellEditorValue(Object value) {
		this.value = value;
	}

	public void setClickCountToStart(int count) {
		clickCountToStart = count;
	}

	public int getClickCountToStart() {
		return clickCountToStart;
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			if (((MouseEvent)anEvent).getClickCount() <
												clickCountToStart)
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
		listenerList.add(CellEditorListener.class, l);
	}

	public void removeCellEditorListener(CellEditorListener l) {
		listenerList.remove(CellEditorListener.class, l);
	}

	public Component getTreeCellEditorComponent(
						JTree tree, Object value,
						boolean isSelected, boolean expanded,
						boolean leaf, int row) {
		return null;
	}
	public abstract Component getTableCellEditorComponent(
									JTable table, Object value,
									boolean isSelected,
									int row, int column);

	protected void fireEditingStopped() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i] == CellEditorListener.class) {
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((CellEditorListener)
				listeners[i+1]).editingStopped(changeEvent);
			}
		}
	}

	protected void fireEditingCanceled() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==CellEditorListener.class) {
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((CellEditorListener)
				listeners[i+1]).editingCanceled(changeEvent);
			}
		}
	}
}
