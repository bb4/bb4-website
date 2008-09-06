/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.optimization.parameter.ui;

import com.becker.optimization.parameter.ParameterChangeListener;
import com.becker.optimization.parameter.Parameter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author becker
 */
public abstract class ParameterWidget extends JPanel {

    protected ParameterChangeListener changeListener;
    protected Parameter parameter;
    
    public ParameterWidget(Parameter param, ParameterChangeListener listener) {
        changeListener = listener;
        parameter = param;
        
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(300, getMaxHeight()));
        addChildren();
    }
    
    /**
     * Add the components to represent the parameter widget.
     */
    protected abstract void addChildren();
        
    protected void doNotification() {
        changeListener.parameterChanged(parameter);     
    }
    
    protected int getMaxHeight() { 
        return 50; 
    }
    
}
