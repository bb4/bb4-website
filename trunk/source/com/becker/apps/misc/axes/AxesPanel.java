package com.becker.apps.misc.axes;

import com.becker.common.*;
import com.becker.ui.legend.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * User: Barry Becker
 * Date: Feb 21, 2005
 * Time: 3:07:56 PM
 */
public class AxesPanel extends JPanel implements ActionListener {

    //private static final double[] values1_ = {-0.0,  40.0,  1101.1};
    //private static final double[] values2_ = {-100.1,  0.0,  1.1};


    private static final double[] values1_ = {-30.1,  -0.5, 1101.1};


    private static final double[] values2_ = {-1.1, 0.5, 1.1};

    private static final int CM_TRANS = 150;
    // this colormap is used to show a spectrum of colors representing a groups health status.
    private static final Color[] colors_ = {new Color( 200, 0, 0, CM_TRANS + 40 ),
                                            new Color( 220, 220, 220, 0 ),
                                            new Color( 150, 0, 250, CM_TRANS + 40 )};
    private JButton synchButton_;

    private ContinuousColorLegend legend1_;
    private ContinuousColorLegend legend2_;



    public AxesPanel() {
        this.setLayout(new BorderLayout());
        //this.setPreferredSize(new Dimension(500, 200));


        ColorMap colormap1 = new ColorMap( values1_, colors_ );
        ColorMap colormap2 = new ColorMap( values2_, colors_ );

        legend1_ = new ContinuousColorLegend("test1", colormap1);
        legend2_ = new ContinuousColorLegend("test2", colormap2);


        synchButton_ = new JButton("Synchronize O point");
        synchButton_.setMaximumSize(new Dimension(100, 22));
        synchButton_.addActionListener(this);

        JPanel buttonContainer = new JPanel(new FlowLayout());
        buttonContainer.add(synchButton_);

        this.add(buttonContainer, BorderLayout.NORTH);
        this.add(legend1_, BorderLayout.CENTER);
        this.add(legend2_, BorderLayout.SOUTH);
    }



    public void actionPerformed(ActionEvent e) {
        ContinuousColorLegend.synchronizeLegends(legend1_, legend2_);
        this.repaint();
    }
}
