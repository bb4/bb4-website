package com.becker.misc.colormixer.colormixer;

import com.becker.ui.ResizableAppletPanel;
import com.becker.ui.GradientButton;
import com.becker.ui.GUIUtil;
import com.becker.ui.NumberInputPanel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;
import java.awt.*;


import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ColorMixer extends JApplet implements ActionListener
{

    ResizableAppletPanel resizablePanel_ = null;

    // the passage thickness in pixels

    private JButton colorButtonA_;
    private JButton colorButtonB_;
    private Color colorA_ = Color.WHITE;
    private Color colorB_ = Color.BLACK;

    private MixedColorsScrollPane mixedColorsPanel_;


    public boolean isStandalone = false;
    // the frame is only created if we run as an application
    protected JFrame baseFrame_ = null;

    // constructor
    public ColorMixer()
    {
        commonInit();
    }

    // constructor
    public void commonInit()
    {
        GUIUtil.setCustomLookAndFeel();

        System.out.println( "creating color mixer" );
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        setFont( new Font( "Serif", Font.PLAIN, 14 ) );

        JPanel mainPanel = createMainPanel();

        resizablePanel_ = new ResizableAppletPanel( mainPanel );
        this.getContentPane().add( resizablePanel_ );

        mixedColorsPanel_.addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                    //resized();
            }
        } );
    }

    /**
     *  Overrides the applet init() method
     */
    public void init()
    {
        //resized();
    }

    private JPanel createMainPanel()
    {
        mixedColorsPanel_ = new MixedColorsScrollPane(colorA_, colorB_);
        //mixedColorsPanel_.setPreferredSize(new Dimension(300, 500));
        mixedColorsPanel_.setBorder(BorderFactory.createEtchedBorder());

        colorButtonA_ = createColorButton(colorA_);
        colorButtonB_ = createColorButton(colorB_);

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
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add( controlsPanel, BorderLayout.NORTH );
        mainPanel.add( mixedColorsPanel_, BorderLayout.CENTER );

        return mainPanel;
    }

    private JButton createColorButton(Color initialColor) {
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
        }

    }


    /**
     * This method allow javascript to resize the applet from the browser.
     */
    public void setSize( int width, int height )
    {
        resizablePanel_.setSize( width, height );
        //resized();
    }


    public void start()
    {
        //resized();
    }

    //------ Main method --------------------------------------------------------

    public static void main( String[] args )
    {
        ColorMixer simulator = new ColorMixer();
        GUIUtil.showApplet( simulator, "Color Mixer" );
    }
}