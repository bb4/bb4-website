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
    private boolean notificationEnabled = true;
    
    public ParameterWidget(Parameter param, ParameterChangeListener listener) {
        changeListener = listener;
        parameter = param;
        
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(300, getMaxHeight()));
        addChildren();
    }
    
    /**
     * Make sure that the UI reflects the current parameter value, in case it has changed underneath
     */
    public void refresh() {
        // temporarly turn of notification to listeners so that we do not update listeners when 
        // we modify our own internal state.
        notificationEnabled = false;
        refreshInternal();
        notificationEnabled = true;
    }
    
    /**
     * Make sure that the UI reflects the current parameter value, in case it has changed underneath
     */
    public abstract void refreshInternal();
    
    /**
     * Add the components to represent the parameter widget.
     */
    protected abstract void addChildren();
        
    protected void doNotification() {
        if (notificationEnabled)
            changeListener.parameterChanged(parameter);     
    }
    
    protected int getMaxHeight() { 
        return 50; 
    }
    
    
    
}
