package com.barrybecker4.java2d.examples;

import com.barrybecker4.ui.application.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class CombiningShapes  extends JComponent {

    public static void main( String[] args ) {
        ApplicationFrame f = new ApplicationFrame( "CombiningShapes v1.0" );
        f.getContentPane().add( new CombiningShapes() );
        f.setSize( 220, 220 );
        f.center();
        f.setVisible( true );
    }

    private Shape mShapeOne, mShapeTwo;
    private JComboBox<String> mOptions;

    public CombiningShapes() {
        // Create the two shapes, a circle and a square.
        mShapeOne = new Ellipse2D.Double( 40, 20, 80, 80 );
        mShapeTwo = new Rectangle2D.Double( 60, 40, 80, 80 );
        setBackground( Color.white );
        setLayout( new BorderLayout() );
        // Create a panel to hold the combo box.
        JPanel controls = new JPanel();
        // Create the combo box with the names of the area operators.
        mOptions = new JComboBox<>(
                new String[]{"outline", "add", "intersection",
                             "subtract", "exclusive or"}
        );
        // Repaint ourselves when the selection changes.
        mOptions.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent ie ) {
                repaint();
            }
        });
        controls.add( mOptions );
        add( controls, BorderLayout.SOUTH );
    }

    @Override
    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        // Retrieve the selection option from the combo box.
        String option = (String) mOptions.getSelectedItem();
        if ( "outline".equals(option) ) {
            // Just draw the outlines and return.
            g2.draw( mShapeOne );
            g2.draw( mShapeTwo );
            return;
        }
        // Create Areas from the shapes.
        Area areaOne = new Area( mShapeOne );
        Area areaTwo = new Area( mShapeTwo );
        // Combine the Areas according to the selected option.
        if ( "add".equals(option) )
            areaOne.add( areaTwo );
        else if ( "intersection".equals(option) )
            areaOne.intersect( areaTwo );
        else if ( "subtract".equals(option) )
            areaOne.subtract( areaTwo );
        else if ( "exclusive or".equals(option) ) areaOne.exclusiveOr( areaTwo );

        // Fill the resulting Area.
        g2.setPaint( Color.orange );
        g2.fill( areaOne );
        // Draw the outline of the resulting Area.
        g2.setPaint( Color.black );
        g2.draw( areaOne );
    }
}