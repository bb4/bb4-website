/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.ui.components;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * GradientButton with a gradient background
 * Standard GradientButton that shows a vertical gradient on it.
 * @author Barry Becker
 */
public class GradientButton extends JButton
                            implements MouseListener {

    /** color at the top of the button. */
    private Color gradientStartColor_ = null;

    /**  color at the bottom of the button. */
    private Color gradientEndColor_ = null;

    private static final long serialVersionUID = 0L;

    private boolean mousedOver_ = false;

    private CustomUI myUI_ = new CustomUI();

    /**
     * Constructor
     *  default to colors from the UIManager
     */
    public GradientButton() {
        commonDefaultInit();
    }

    /**
     * Constructor
     *  default to colors from the UIManager
     */
    public GradientButton( String text ) {
        commonDefaultInit();
        this.setText( text );
    }

    /**
     * Constructor
     *  default to colors from the UIManager
     */
    public GradientButton( String text, Icon icon ) {
        commonDefaultInit();
        this.setText( text );
        this.setIcon( icon );
    }

    /**
     * Constructor
     * @param startColor the color at the top of the button
     * @param endColor  the color at the bottom of the button
     */
    public GradientButton( Color startColor, Color endColor ) {
        gradientStartColor_ = startColor;
        gradientEndColor_ = endColor;
        setUI( myUI_ );
    }

    private void commonDefaultInit() {
        Color c = UIManager.getColor( "Button.background" );
        gradientStartColor_ = c.brighter();
        gradientEndColor_ = c;
        addMouseListener(this);
        setUI( myUI_ );
    }

    /**
     * Don't let anyone change the UI object.
     */
    @Override
    public void setUI( ButtonUI b ) {
        super.setUI( myUI_ );
    }

    /**
     * Set starting gradient color
     */
    public void setStartColor( Color pStartColor ) {
        gradientStartColor_ = pStartColor;
    }

    /**
     * Set ending gradient color
     */
    public void setEndColor( Color pEndColor ) {
        gradientEndColor_ = pEndColor;
    }


    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {
        mousedOver_ = true;
        this.repaint();
    }

    public void mouseExited(MouseEvent e) {
        mousedOver_ = false;
        this.repaint();
    }

    /**
     * Custom Button UI class that paints a gradient background on the button
     * before text or an icon is painted on the button.
     */
    private class CustomUI extends BasicButtonUI {
        @Override
        protected void paintText( Graphics g, JComponent c, Rectangle textRect, String text ) {
            //if the button has no icon, add the gradient background
            if ( c instanceof GradientButton && (((AbstractButton) c).getIcon() == null)) {
                addGradientBackground( g );
            }
            super.paintText( g, c, textRect, text );
        }

        @Override
        protected void paintIcon( Graphics g, JComponent c, Rectangle iconRect ) {
            //if the button has an icon, add the gradient background
            if ( c instanceof GradientButton && (((AbstractButton) c).getIcon() != null)) {
                addGradientBackground( g );
            }
            super.paintIcon( g, c, iconRect );
        }


        /**
         * Does the work of actually drawing the gradient background.
         */
        private void addGradientBackground( Graphics g )
        {
            Graphics2D g2D = (Graphics2D) g;

            double width = getSize().getWidth();
            double height = getSize().getHeight();

            Point2D.Double origin = new Point2D.Double( 0.0, 0.0 );
            Point2D.Double end = new Point2D.Double( 0.0, height );

            Color startColor = gradientStartColor_;
            Color endColor = gradientEndColor_;
            startColor = mousedOver_ ?  startColor.brighter() : startColor;
            //endColor = mousedOver_ ? endColor.brighter() : endColor;

            GradientPaint rtow;
            if ( isSelected() ) {
                rtow = new GradientPaint( origin, endColor, end, startColor );
            }
            else {
                rtow = new GradientPaint( origin, startColor, end, endColor );
            }

            float opacity = mousedOver_ ? 1.0f : 0.75f;
            if (!isEnabled()) {
                opacity = 0.6f;
            }
            g2D.setComposite(           // SRC_OVER
                    AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity ));
            g2D.setPaint( rtow );
            g2D.fill( new Rectangle2D.Double( 0, 0, width, height ) );
        }
    }
}

