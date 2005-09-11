package com.becker.ui;

/**
 * GradientButton
 * Standard GradientButton that shows a vertical gradient on it.
 *
 */

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * GradientButton with a gradient background
 */
public class GradientButton extends JButton
{
    // color at the top of the button
    private Color gradientStartColor_ = null;
    //  color at the bottom of the button
    private Color gradientEndColor_ = null;
    private static final long serialVersionUID = 0L;

    private CustomUI myUI_ = new CustomUI();

    /**
     * Constructor
     *  default to colors from the UIManager
     */
    public GradientButton()
    {
        commonDefaultInit();
    }

    /**
     * Constructor
     *  default to colors from the UIManager
     */
    public GradientButton( String text )
    {
        commonDefaultInit();
        this.setText( text );
    }

    /**
     * Constructor
     *  default to colors from the UIManager
     */
    public GradientButton( String text, Icon icon )
    {
        commonDefaultInit();
        this.setText( text );
        this.setIcon( icon );
    }

    /**
     * Constructor
     * @param startColor the color at the top of the button
     * @param endColor  the color at the bottom of the button
     */
    public GradientButton( Color startColor, Color endColor )
    {
        gradientStartColor_ = startColor;
        gradientEndColor_ = endColor;
        setUI( myUI_ );
    }

    private void commonDefaultInit()
    {
        Color c = UIManager.getColor( "Button.background" );
        gradientStartColor_ = c.brighter();
        gradientEndColor_ = c;
        setUI( myUI_ );
    }

    /**
     * Don't let anyone change the UI object.
     */
    public void setUI( ButtonUI b )
    {
        super.setUI( myUI_ );
    }

    /**
     * Set starting gradient color
     */
    public void setStartColor( Color pStartColor )
    {
        gradientStartColor_ = pStartColor;
    }

    /**
     * Set ending gradient color
     */
    public void setEndColor( Color pEndColor )
    {
        gradientEndColor_ = pEndColor;
    }

    /**
     * does the work of actually drawing th gradient background
     */
    private void addGradientBackground( Graphics g )
    {
        Graphics2D g2D = (Graphics2D) g;

        double width = this.getSize().getWidth();
        double height = this.getSize().getHeight();

        Point2D.Double origin = new Point2D.Double( 0.0, 0.0 );
        Point2D.Double end = new Point2D.Double( 0.0, height );

        GradientPaint rtow;
        if ( isSelected() ) {
            rtow = new GradientPaint( origin, gradientEndColor_,
                    end, gradientStartColor_ );
        }
        else {
            rtow = new GradientPaint( origin, gradientStartColor_,
                    end, gradientEndColor_ );
        }

        //int type = AlphaComposite.SRC_OVER;

        // 1.0F = no transparency
        g2D.setComposite(
                AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0F ) );
        g2D.setPaint( rtow );
        g2D.fill( new Rectangle2D.Double( 0, 0, width, height ) );
    }

    /**
     * Custom Button UI class that paints a gradient background on the button
     * before text or an icon is painted on the button.
     */
    private class CustomUI extends BasicButtonUI
    {
        protected void paintText( Graphics g, JComponent c, Rectangle textRect, String text )
        {
            //if the button has no icon, add the gradient background
            if ( c instanceof GradientButton && (((GradientButton) c).getIcon() == null) ) {
                addGradientBackground( g );
            }
            super.paintText( g, c, textRect, text );
        }

        protected void paintIcon( Graphics g, JComponent c, Rectangle iconRect )
        {
            //if the button has an icon, add the gradient background
            if ( c instanceof GradientButton && (((GradientButton) c).getIcon() != null) ) {
                addGradientBackground( g );
            }
            super.paintIcon( g, c, iconRect );
        }

    }
}

