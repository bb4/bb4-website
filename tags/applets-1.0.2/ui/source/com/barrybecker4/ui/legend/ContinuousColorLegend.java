/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.legend;

import com.barrybecker4.ui.util.ColorMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * shows a continuous color legend given a list of colors and corresponding values.
 * It may be editable if isEditable is set.
 * Might be nice to throw a change event when edited.
 *
 * @author Barry Becker
 */
public class ContinuousColorLegend extends JPanel {

    private String title_;
    private ColorMap colormap_;

    private LegendEditBar legendEditBar_;
    private boolean isEditable_ = false;
    private LegendLabelsPanel labelsPanel_;

    public ContinuousColorLegend(String title, ColorMap colormap) {
        this(title, colormap, false);
    }

    public ContinuousColorLegend(String title, ColorMap colormap, boolean editable)  {
        title_ = title;
        colormap_ = colormap;
        isEditable_ = editable;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                      BorderFactory.createEtchedBorder(),
                      BorderFactory.createMatteBorder(1, 0, 2, 0, this.getBackground())));

        int height = 40;
        if (title_ != null) {
            JPanel titlePanel = new JPanel();
            titlePanel.setOpaque(false);
            JLabel title = new JLabel(title_, JLabel.LEFT);
            title.setOpaque(false);
            titlePanel.add(title, Component.LEFT_ALIGNMENT);
            add(titlePanel);
            add(Box.createRigidArea(new Dimension(4, 4)));
            height = 55;
        }
        legendEditBar_ = new LegendEditBar(colormap_, this);
        if (isEditable_)  {
            add(legendEditBar_, BorderLayout.NORTH);
        }
        add(createLegendPanel(), BorderLayout.CENTER);

        labelsPanel_ = new LegendLabelsPanel(colormap_);
        add(labelsPanel_, BorderLayout.SOUTH);

        setMaximumSize(new Dimension(2000, height));

        this.addComponentListener( new ComponentAdapter()  {
            @Override
            public void componentResized( ComponentEvent ce ) {}
        } );
    }

    private JPanel createLegendPanel() {
        return new ColoredLegendLine(colormap_);
    }


    public boolean isEditable() {
        return isEditable_;
    }

    public void setEditable(boolean editable) {
        if (isEditable_ == editable) {
            return;
        }
        isEditable_ = editable;
        if (isEditable_) {
            add(legendEditBar_, BorderLayout.NORTH);
        } else {
            remove(legendEditBar_);
        }
    }

    public double getMin() {
        return labelsPanel_.getMin();
    }

    public void setMin(double min) {
        labelsPanel_.setMin(min);
    }

    public double getMax() {
        return labelsPanel_.getMax();
    }

    public void setMax(double max) {
        labelsPanel_.setMax(max);
    }
}
