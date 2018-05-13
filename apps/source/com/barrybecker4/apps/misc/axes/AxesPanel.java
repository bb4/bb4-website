/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.axes;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.ui.legend.ContinuousColorLegend;
import com.barrybecker4.ui.legend.LegendSynchronizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author  Barry Becker
 */
public class AxesPanel extends JPanel implements ActionListener {

    private static final double[] values1 = {-30.1,  -0.5, 1101.1};
    private static final double[] values2 = {-1.1, 0.5, 1.1};

    private static final int CM_TRANS = 150;

    /** this colormap is used to show a spectrum of colors representing a groups health status.   */
    private static final Color[] colors = {new Color( 200, 0, 0, CM_TRANS + 40 ),
                                            new Color( 220, 220, 220, 0 ),
                                            new Color( 150, 0, 250, CM_TRANS + 40 )};

    private ContinuousColorLegend legend1;
    private ContinuousColorLegend legend2;

    /**
     * Constructor
     */
    public AxesPanel() {
        this.setLayout(new BorderLayout());

        ColorMap colormap1 = new ColorMap(values1, colors);
        ColorMap colormap2 = new ColorMap(values2, colors);

        legend1 = new ContinuousColorLegend("test1", colormap1);
        legend2 = new ContinuousColorLegend("test2", colormap2);


        JButton synchButton = new JButton("Synchronize O point");
        synchButton.setMaximumSize(new Dimension(100, 22));
        synchButton.addActionListener(this);

        JPanel buttonContainer = new JPanel(new FlowLayout());
        buttonContainer.add(synchButton);

        this.add(buttonContainer, BorderLayout.NORTH);
        this.add(legend1, BorderLayout.CENTER);
        this.add(legend2, BorderLayout.SOUTH);
    }


    public void actionPerformed(ActionEvent e) {
        new LegendSynchronizer().synchronizeLegends(legend1, legend2);
        this.repaint();
    }
}
