package com.barrybecker4.java2d.imageproc;

import com.barrybecker4.optimization.parameter.ParameterChangeListener;
import com.barrybecker4.optimization.parameter.types.Parameter;
import com.barrybecker4.optimization.parameter.ui.ParameterWidget;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Auto create a panel of sliders and dropdowns (etc) for manipulating a set of parmeters.
 * @author Barry Becker
 */
public class ParameterPanel extends JScrollPane
                            implements ParameterChangeListener
{
    /** called when a parameter changes */
    private List<ParameterChangeListener> changeListeners;

    private List<Parameter> parameters;

    private JPanel viewPanel;

    public ParameterPanel( List<Parameter> params )
    {
        changeListeners = new ArrayList<ParameterChangeListener>();
        parameters = params;

        viewPanel = new JPanel();
        viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));

        if (params != null) {
            initializeUI();
        }
        this.setViewportView(viewPanel);
    }

    /**
     * @param params set of parameters that match the number and type of the original
     */
    public void updateParameters( List<Parameter> params)
    {
        if (params == null)
            return;
        if (parameters != null) {
            assert params.size() == parameters.size() :
                "old param size = "+parameters.size() + " new param size = " + params.size();
        }

        for (int i=0; i<params.size(); i++) {

            Parameter newp = params.get(i);
            Parameter currentp = parameters.get(i);
            assert newp.getName().equals(currentp.getName());
            currentp.setValue(newp.getValue());
        }
        int numKids = viewPanel.getComponentCount();
        for (int i=0; i<numKids; i++) {
            ParameterWidget w = (ParameterWidget) viewPanel.getComponent(i);
            w.refresh();
        }
    }

    /**
     * Add a unique UI element for manipulating each individual parameter.
     */
    protected void initializeUI()
    {
        for (Parameter param : parameters) {
            viewPanel.add(param.createWidget(this));
        }
    }

    public void addParameterChangeListener(ParameterChangeListener listener) {
        changeListeners.add(listener);
    }

    /**
     * we only want to call parmeterChange listeners if a parameter actually changed.
     * @param param the swing component that was activated.
     */
    public void parameterChanged(Parameter param) {
         for (ParameterChangeListener listener : changeListeners) {
             listener.parameterChanged(param);
         }
    }

}