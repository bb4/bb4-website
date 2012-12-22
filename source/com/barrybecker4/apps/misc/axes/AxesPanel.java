/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.axes;

import com.barrybecker4.common.ColorMap;
import com.barrybecker4.ui.legend.ContinuousColorLegend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author  Barry Becker
 */
public class AxesPanel extends JPanel implements ActionListener {

    private static final double[] values1_ = {-30.1,  -0.5, 1101.1};
    private static final double[] values2_ = {-1.1, 0.5, 1.1};

    private static final int CM_TRANS = 150;

    /** this colormap is used to show a spectrum of colors representing a groups health status.   */
    private static final Color[] colors_ = {new Color( 200, 0, 0, CM_TRANS + 40 ),
                                            new Color( 220, 220, 220, 0 ),
                                            new Color( 150, 0, 250, CM_TRANS + 40 )};

    private ContinuousColorLegend legend1_;
    private ContinuousColorLegend legend2_;

    /**
     * Constructor
     */
    public AxesPanel() {
        this.setLayout(new BorderLayout());

        ColorMap colormap1 = new ColorMap( values1_, colors_ );
        ColorMap colormap2 = new ColorMap( values2_, colors_ );

        legend1_ = new ContinuousColorLegend("test1", colormap1);
        legend2_ = new ContinuousColorLegend("test2", colormap2);


        JButton synchButton = new JButton("Synchronize O point");
        synchButton.setMaximumSize(new Dimension(100, 22));
        synchButton.addActionListener(this);

        JPanel buttonContainer = new JPanel(new FlowLayout());
        buttonContainer.add(synchButton);

        this.add(buttonContainer, BorderLayout.NORTH);
        this.add(legend1_, BorderLayout.CENTER);
        this.add(legend2_, BorderLayout.SOUTH);
    }


    public void actionPerformed(ActionEvent e) {
        ContinuousColorLegend.synchronizeLegends(legend1_, legend2_);
        this.repaint();
    }
}
