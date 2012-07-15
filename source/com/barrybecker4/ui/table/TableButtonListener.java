/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.table;

import java.util.EventListener;

/**
 * Called when you click a TableButton in a table cell.
 */
 public interface TableButtonListener extends EventListener  {
      void tableButtonClicked( int row, int col, String e );
}
