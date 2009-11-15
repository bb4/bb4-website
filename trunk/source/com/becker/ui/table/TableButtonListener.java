package com.becker.ui.table;

import java.awt.event.ActionEvent;
import java.util.EventListener;

/**
 * Called when you click a TableButton in a table cell.
 */
 public interface TableButtonListener extends EventListener  {
      public void tableButtonClicked( int row, int col, String e );
}
