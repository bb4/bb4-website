/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.colormixer;

import com.barrybecker4.ui.application.ApplicationApplet;
import com.barrybecker4.ui.components.ColorInputPanel;
import com.barrybecker4.ui.util.GUIUtil;

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
    private JButton colorButtonA;
    private JButton colorButtonB;
    private Color colorA = Color.WHITE;
    private Color colorB = Color.BLACK;

    private JSlider opacitySlider;
    private static final int SLIDER_TICKS = 1000;

    private MixedColorsScrollPane mixedColorsPanel;

    // constructor
    public ColorMixer() {}

    @Override
    public JPanel createMainPanel()
    {
        mixedColorsPanel = new MixedColorsScrollPane(colorA, colorB);
        //mixedColorsPanel.setPreferredSize(new Dimension(300, 500));
        mixedColorsPanel.setBorder(BorderFactory.createEtchedBorder());

        colorButtonA = createColorButton(colorA);
        colorButtonB = createColorButton(colorB);

        opacitySlider = createOpacitySlider();

        JPanel colorPanelA = new ColorInputPanel("Select first color : ",
                                                     "Select the first color to mix",
                colorButtonA, this);
        JPanel colorPanelB = new ColorInputPanel("Select second color : ",
                                                     "Select the second color to mix",
                colorButtonB, this);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
        controlsPanel.add(colorPanelA);
        controlsPanel.add( Box.createHorizontalStrut( 15 ) );
        controlsPanel.add(colorPanelB);
        controlsPanel.add( Box.createHorizontalStrut( 15 ) );
        controlsPanel.add(new JLabel("Opacity"));
        controlsPanel.add(opacitySlider);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add( controlsPanel, BorderLayout.NORTH );
        mainPanel.add(mixedColorsPanel, BorderLayout.CENTER );

        return mainPanel;
    }

    private JSlider createOpacitySlider() {
        JSlider opacitySlider = new JSlider(JSlider.HORIZONTAL, 0, SLIDER_TICKS, SLIDER_TICKS);
        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
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
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();

        if ( source == colorButtonA || source == colorButtonB) {
            System.out.println("a or b pressed");
            mixedColorsPanel.setColorsToMix(colorButtonA.getBackground(), 1.0f,  colorButtonB.getBackground(), 1.0f);
            mixedColorsPanel.invalidate();
            resizablePanel().repaint();
        }
    }

    @Override
    public String getName() {
        return "Color Mixer";
    }

    public void stateChanged(ChangeEvent ce) {
        Object source = ce.getSource();
        if ( source == opacitySlider) {
            mixedColorsPanel.setOpacity((float) opacitySlider.getValue()/SLIDER_TICKS);
            resizablePanel().repaint();
        }
    }

    //------ Main method --------------------------------------------------------

    public static void main( String[] args )
    {
        ColorMixer simulator = new ColorMixer();
        GUIUtil.showApplet(simulator);
    }
}