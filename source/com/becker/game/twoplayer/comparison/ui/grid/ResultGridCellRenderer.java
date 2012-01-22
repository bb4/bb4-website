// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui.grid;

import com.becker.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.becker.game.twoplayer.comparison.model.Outcome;
import com.becker.game.twoplayer.comparison.model.PerformanceResults;
import com.becker.game.twoplayer.comparison.model.PerformanceResultsPair;
import com.becker.ui.components.GradientButton;
import com.becker.ui.table.TableButtonListener;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

/**
 * A button that can be placed in a table cell.
 * Add a TableButtonListener to do something when clicked.
 *
 * @author Barry Becker
 */
public class ResultGridCellRenderer extends JPanel
                         implements TableCellRenderer {
  
    private static final Color BG_COLOR = new Color(220, 220, 221);

    private static final Color TIME_BAR_COLOR = new Color(250, 180, 40);
    private static final Color NUM_MOVES_BAR_COLOR = new Color(190, 210, 1);

    PerformanceResultsPair perfResults;

    /**
     * Constructor
     */
    public ResultGridCellRenderer() {
        this.setLayout(new BorderLayout());
        
        this.setMinimumSize(new Dimension(100, 100));
        //label = new JLabel("foo");
        //this.add(label);
    }

    public Component getTableCellRendererComponent(JTable table,
                              Object value, boolean isSelected, boolean hasFocus,
                              int row, int col) {
        setLabel(table.getModel(), row, col);
        return this;
    }


    private void setLabel(TableModel tableModel, int row, int col) {

        perfResults = (PerformanceResultsPair) tableModel.getValueAt(row, col);
    
        this.setToolTipText(perfResults.toString());
    }
    
    
    /** draw the cartesian function */
    @Override
    public void paint(Graphics g) {
         
        Graphics2D g2 = (Graphics2D) g;
        drawBackGround(g2);

        int winBarHeight = getHeight()/2;
        int timeBarHeight = getHeight()/4;
        int movesBarHeight = getHeight()/4;
        
        drawWinBar(winBarHeight, g2);
        drawTimeBar(timeBarHeight, winBarHeight, g2);
        drawNumMovesBar(movesBarHeight, winBarHeight + timeBarHeight, g2);
    }
    
    private void drawBackGround(Graphics2D g2) {
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void drawWinBar(int barHeight, Graphics2D g2) {
        Outcome[] outcomes = perfResults.getOutcomes();

        int width = getWidth()/2;
        g2.setColor(outcomes[0].getColor());
        g2.fillRect(0, 0, width, barHeight);

        g2.setColor(outcomes[1].getColor());
        g2.fillRect(width, 0, width, barHeight);
    }

    private void drawTimeBar(int barHeight, int yOffset, Graphics2D g2) {
        double[] times = perfResults.getNormalizedTimes();
        drawDualBar(barHeight, yOffset, times, TIME_BAR_COLOR, g2);
    }
    
    private void drawNumMovesBar(int barHeight, int yOffset, Graphics2D g2) {
        double[] normNumMoves = perfResults.getNormalizedNumMoves();
        drawDualBar(barHeight, yOffset, normNumMoves, NUM_MOVES_BAR_COLOR, g2);
    }
    
    private void drawDualBar(int barHeight, int yOffset, double[] normValues, Color barColor, Graphics2D g2) {

        System.out.println("normValue1=" + normValues[0] +"normValue2=" + normValues[1] );
        int n1Width = (int)(normValues[0] * getWidth());
        g2.setPaint(createGradient(n1Width, barColor));
        g2.fillRect(0, yOffset, n1Width, barHeight);
        
        int n2Width = (int)(normValues[1] * getWidth());
        g2.setPaint(createGradient(n2Width, barColor));
        g2.fillRect(n1Width, yOffset, n2Width, barHeight);        
    }
    
    private GradientPaint createGradient(double width, Color color) {
        
        Point2D.Double origin = new Point2D.Double( 0.0, 0.0 );
        Point2D.Double end = new Point2D.Double( width, 0.0 );

        Color startColor = color.brighter();
      
        return new GradientPaint(origin, startColor, end, color);
    }
  
}
                                            