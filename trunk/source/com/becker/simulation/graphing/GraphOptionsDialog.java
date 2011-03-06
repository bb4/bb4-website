package com.becker.simulation.graphing;


import com.becker.common.math.function.ArrayFunction;
import com.becker.common.math.function.Function;
import com.becker.common.math.interplolation.InterpolationMethod;
import com.becker.simulation.common.Simulator;
import com.becker.simulation.common.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class GraphOptionsDialog extends SimulatorOptionsDialog {

    /** type of interpolation to use.   */
    private JComboBox functionCombo_;

    /** type of interpolation to use.   */
    private JComboBox interpolationTypeCombo_;


    /**
     * constructor
     */
    public GraphOptionsDialog( JFrame parent, Simulator simulator )
    {
        super( parent, simulator );
    }

    @Override
    public String getTitle()
    {
        return "Graph Simulation Configuration";
    }

    @Override
    protected JPanel createCustomParamPanel()
    {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout( new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        ComboBoxModel model = new DefaultComboBoxModel(FunctionType.values());
        functionCombo_ = new JComboBox(model);
        innerPanel.add(functionCombo_);

        interpolationTypeCombo_ = new JComboBox(InterpolationMethod.values());
        innerPanel.add(interpolationTypeCombo_);
        interpolationTypeCombo_.setSelectedIndex(1);

        JPanel fill = new JPanel();
        paramPanel.add(innerPanel, BorderLayout.NORTH);
        paramPanel.add(fill, BorderLayout.CENTER);

        return paramPanel;
    }

    @Override
    protected void ok()
    {
        super.ok();

        GraphSimulator simulator = (GraphSimulator) getSimulator();

        Function func = ((FunctionType)functionCombo_.getSelectedItem()).function;
        if (func instanceof ArrayFunction)  {
            ((ArrayFunction)func).setInterpolationMethod(
                    (InterpolationMethod)interpolationTypeCombo_.getSelectedItem());
        }

        simulator.setFunction(func);
    }

}