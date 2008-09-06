package com.becker.java2d.imageproc;

import com.becker.common.*;
import com.becker.optimization.parameter.Parameter;

import com.becker.optimization.parameter.ParameterChangeListener;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


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
     * @param c the swing component that was activated.
     */
    public void parameterChanged(Parameter param) {
         for (ParameterChangeListener listener : changeListeners) {
             listener.parameterChanged(param);
         }                            
    }
    
}