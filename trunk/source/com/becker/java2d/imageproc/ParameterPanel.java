package com.becker.java2d.imageproc;

import com.becker.common.*;
import com.becker.optimization.Parameter;

import com.becker.ui.sliders.LabeledSlider;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Auto create a panel of sliders and dropdowns (etc) for manipulating a set of parmeters.
 * @author Barry Becker
 */
public class ParameterPanel extends Panel  implements ChangeListener                          
{
    private List<ParameterChangeListener> changeListeners;
    
    private List<Parameter> parameters;

    public ParameterPanel( List<Parameter> params )
    {           
        changeListeners = new ArrayList<ParameterChangeListener>();
        parameters = params;
        setMinimumSize(new Dimension(250, 280));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        if (params != null) {
            initializeUI();           
        }
    }

    /**
     * Add a unique ui element for manipulating each individual parameter.
     */
    protected void initializeUI()
    {              
        for (Parameter param : parameters) {
            
           LabeledSlider slider = new LabeledSlider(param.getName(), param.getValue(), param.getMinValue(), param.getMaxValue());
           if (param.getType() == int.class) {
               slider.setShowAsInteger(true);
           }
           slider.addChangeListener(this);
           add(slider);
        }        
    }
    
    public void addParameterChangeListener(ParameterChangeListener listener) {
        changeListeners.add(listener);
    }
    
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider)e.getSource();
        for (Parameter param : parameters) {
            if (param.getName().equals(slider.getName())) {
                param.setValue(slider.getValue());
            }                
        }
        for (ParameterChangeListener listener : changeListeners) {
            listener.parameterChanged();
        }
    }
             
            
    
}