package com.becker.apps.misc.colormixer;

import com.becker.ui.ApplicationApplet;
import com.becker.ui.GUIUtil;
import com.becker.ui.components.ColorInputPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;

public class ColorMixer extends ApplicationApplet implements ActionListener, ChangeListener
{
    private JButton colorButtonA_;
    private JButton colorButtonB_;
    private Color colorA_ = Color.WHITE;
    private Color colorB_ = Color.BLACK;

    private JSlider opacitySlider_;
    private static final int SLIDER_TICKS = 1000;

    private MixedColorsScrollPane mixedColorsPanel_;

    // constructor
    public ColorMixer()
    {
    }

    protected JPanel createMainPanel()
    {
        mixedColorsPanel_ = new MixedColorsScrollPane(colorA_, colorB_);
        //mixedColorsPanel_.setPreferredSize(new Dimension(300, 500));
        mixedColorsPanel_.setBorder(BorderFactory.createEtchedBorder());

        colorButtonA_ = createColorButton(colorA_);
        colorButtonB_ = createColorButton(colorB_);

        opacitySlider_ = createOpacitySlider();

        JPanel colorPanelA = new ColorInputPanel("Select first color : ",
                                                     "Select the first color to mix",
                                                     colorButtonA_, this);
        JPanel colorPanelB = new ColorInputPanel("Select second color : ",
                                                     "Select the second color to mix",
                                                     colorButtonB_, this);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
        controlsPanel.add(colorPanelA);
        controlsPanel.add( Box.createHorizontalStrut( 15 ) );
        controlsPanel.add(colorPanelB);
        controlsPanel.add( Box.createHorizontalStrut( 15 ) );
        controlsPanel.add(new JLabel("Opacity"));
        controlsPanel.add(opacitySlider_);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add( controlsPanel, BorderLayout.NORTH );
        mainPanel.add( mixedColorsPanel_, BorderLayout.CENTER );

        return mainPanel;
    }

    private JSlider createOpacitySlider() {
        JSlider opacitySlider = new JSlider(JSlider.HORIZONTAL, 0, SLIDER_TICKS, SLIDER_TICKS);
        Dictionary dict = new Hashtable();
        dict.put(0, new JLabel("0"));
        dict.put(SLIDER_TICKS, new JLabel("1.0"));
        opacitySlider.setLabelTable(dict);
        //opacitySlider.setMajorTickSpacing(100);
        //opacitySlider.setMinorTickSpacing(10);
        opacitySlider.setPaintLabels(true);
        //opacitySlider.setPaintTicks(true);
        //opacitySlider.setPaintTrack(true);
        opacitySlider.addChangeListener(this);
        return opacitySlider;
    }

    private static JButton createColorButton(Color initialColor) {
        JButton colorButton = new JButton("   ");
        colorButton.setBackground(initialColor);
        //colorButton.addActionListener(this);
        return colorButton;
    }

    /**
     * called when a button is pressed
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();

        if ( source == colorButtonA_ || source == colorButtonB_) {
            System.out.println("a or b pressed");
            mixedColorsPanel_.setColorsToMix(colorButtonA_.getBackground(), 1.0f,  colorButtonB_.getBackground(), 1.0f);
            mixedColorsPanel_.invalidate();
            resizablePanel_.repaint();
        }
    }



    public void stateChanged(ChangeEvent ce) {
        Object source = ce.getSource();
        if ( source == opacitySlider_) {
            mixedColorsPanel_.setOpacity((float)opacitySlider_.getValue()/SLIDER_TICKS);
            resizablePanel_.repaint();
        }
    }

    //------ Main method --------------------------------------------------------

    public static void main( String[] args )
    {
        ColorMixer simulator = new ColorMixer();
        GUIUtil.showApplet( simulator, "Color Mixer" );
    }
}