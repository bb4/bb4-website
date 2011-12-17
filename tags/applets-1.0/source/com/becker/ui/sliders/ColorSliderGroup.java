/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.ui.sliders;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * A color swatch and r,g,b sliders to control its color.
 * @author Barry Becker Date: Jul 16, 2006
 */
public class ColorSliderGroup extends JPanel implements ChangeListener {

    private static final String RED_LABEL = "Red : ";
    private static final String GREEN_LABEL = "Green : ";
    private static final String BLUE_LABEL = "Blue : ";
    private JLabel red_, green_, blue_;
    private JSlider redSlider_, greenSlider_, blueSlider_;
    private JPanel swatch_;

    ColorChangeListener colorListener_;

    /**
     * constructor builds the ui for the slider group
     */
    public ColorSliderGroup() {

        BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);

        setLayout(bl);
        setBorder(BorderFactory.createEtchedBorder());

        red_ = new JLabel( RED_LABEL + '0', JLabel.LEFT  );
        green_ = new JLabel( GREEN_LABEL + '0', JLabel.LEFT );
        blue_ = new JLabel( BLUE_LABEL + '0', JLabel.LEFT  );

        JPanel redPanel = createColorLabelPanel(red_ );
        JPanel greenPanel = createColorLabelPanel(green_ );
        JPanel bluePanel = createColorLabelPanel(blue_ );


        redSlider_ = new JSlider( JSlider.HORIZONTAL, 0, 255, 0 );
        redSlider_.addChangeListener( this );

        greenSlider_ = new JSlider( JSlider.HORIZONTAL, 0, 255, 0 );
        greenSlider_.addChangeListener( this );

        blueSlider_ = new JSlider( JSlider.HORIZONTAL, 0, 255, 0 );
        blueSlider_.addChangeListener( this );

        JPanel swatchPanel = new JPanel(new BorderLayout());
        swatch_ = new JPanel();
        //swatch_.setBackground( Color.white );
        //swatch_.setMaximumSize(new Dimension(60, 20));
        //swatch_.setPreferredSize(new Dimension(60, 20));
        swatch_.setBorder(BorderFactory.createMatteBorder(2,20,2,20, this.getBackground()));


        add( swatch_ );
        add( redPanel );
        add( redSlider_ );
        add( greenPanel );
        add( greenSlider_ );
        add( bluePanel );
        add( blueSlider_ );

        updateSwatch();
    }

    private JPanel createColorLabelPanel(JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.WEST);
        p.add(new JPanel(), BorderLayout.CENTER);
        return p;
    }

    public void setColorChangeListener(ColorChangeListener listener) {
        colorListener_ = listener;
        //colorListener_.colorChanged();
        updateSwatch();
    }

    public void updateSwatch()
    {
        Color color = new Color( redSlider_.getValue(), greenSlider_.getValue(), blueSlider_.getValue());

        if (colorListener_ != null) {
            colorListener_.colorChanged(color);
        }

        swatch_.setBackground( color );
        swatch_.setOpaque( true );
        swatch_.repaint();
    }

    /**
     * one of the sliders has moved.
     * @param e
     */
    public void stateChanged( ChangeEvent e )
    {
        JSlider src = (JSlider) e.getSource();

        if ( src == redSlider_ ) {
            red_.setText( RED_LABEL + redSlider_.getValue() );
        }
        else if ( src == greenSlider_ ) {
            green_.setText( GREEN_LABEL + greenSlider_.getValue() );
        }
        else if ( src == blueSlider_ ) {
            blue_.setText( BLUE_LABEL + blueSlider_.getValue() );
        }
        updateSwatch();
    }

}
