package com.becker.ui.table;

import com.becker.ui.components.GradientButton;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A button that can be placed in a table cell.
 * Add a TableButtonListener to do something when clicked.
 *
 * @author Barry Becker
 */
public class TableButton extends GradientButton
                                             implements TableCellRenderer, TableCellEditor {
  private int selectedRow;
  private int selectedColumn;
  private List<TableButtonListener> listeners;
  private final String id_;
  private int columnIndex_ = -1;

  /**
   * Constructor
   * @param text label for all the buttons in the column
   * @param id used to identifiy the button clicked in the tableButton handler.
   */
  public TableButton(String text,  String id) {
      super(text);
    
      id_ = id;
      columnIndex_ = -1;
      commonInit();
  }
  
  /**
   * Constructor
   * @param columnIndex the column that has the label to show in the button.
   * @param id used to identifiy the button clicked in the tableButton handler.
   */
  public TableButton(int columnIndex,  String id) { 
      id_ = id;
      columnIndex_ = columnIndex;
      commonInit();
  }

  private void commonInit() {
      listeners = new ArrayList<TableButtonListener>();
    this.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent e) {
               for (TableButtonListener l : listeners) {
                    l.tableButtonClicked(selectedRow, selectedColumn, id_);
               }
           }
      });
  }


  public void addTableButtonListener( TableButtonListener l ) {
      listeners.add(l);
  }

  public void removeTableButtonListener( TableButtonListener l ) {
      listeners.remove(l);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table,
      Object value, boolean isSelected, boolean hasFocus, int row, int col) {
      setLabel(table.getModel(), row);
      return this;
  }

  @Override
  public Component getTableCellEditorComponent(JTable table,
      Object value, boolean isSelected, int row, int col) {
    selectedRow = row;
    selectedColumn = col;
    setLabel(table.getModel(), row);
    return this;
  }

  private void setLabel(TableModel tableModel, int row) {
      boolean isNullValued = tableModel.getValueAt(row, columnIndex_) ==null;
      if (columnIndex_ >=0 &&  !isNullValued) {
          this.setText(tableModel.getValueAt(row, columnIndex_).toString());
      }
      if (isNullValued) {
          this.setEnabled(false);
      }
  }

  @Override
  public void addCellEditorListener(CellEditorListener arg0) {
  }

  @Override
  public void cancelCellEditing() {
  }

  @Override
  public Object getCellEditorValue() {
    return "";
  }

  @Override
  public boolean isCellEditable(EventObject arg0) {
    return true;
  }

  @Override
  public void removeCellEditorListener(CellEditorListener arg0) {
  }

  @Override
  public boolean shouldSelectCell(EventObject arg0) {
    return false;  // was true
  }

  @Override
  public boolean stopCellEditing() {
    return true;
  }
}
