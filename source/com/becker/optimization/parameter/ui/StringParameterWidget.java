package com.becker.optimization.parameter.ui;

import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterChangeListener;
import com.becker.optimization.parameter.StringParameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Barry Becker
 */
public class StringParameterWidget extends ParameterWidget implements ActionListener {

    private JComboBox dropdown;
    
    public StringParameterWidget(Parameter param, ParameterChangeListener listener) {
        super(param, listener);
    }
    
   /**
     * Create a ui widget appropriate for the parameter type.
     */
    @Override
    protected void addChildren() {
             
           // create a dropdown
            StringParameter sparam = (StringParameter) parameter;
            dropdown = new JComboBox(sparam.getStringValues().toArray());
            dropdown.setName(parameter.getName());
            dropdown.setMaximumSize(new Dimension(200, 20));
            //System.out.println("values= " + sparam.getStringValues().toArray());
            dropdown.setToolTipText(parameter.getName());          
            dropdown.addActionListener(this);  
            add(dropdown, BorderLayout.CENTER);                     
    }
    
     /**
      * Called when a ComboBox selection has changed.
      * @param e the item event
      */
    public void actionPerformed(ActionEvent e) {    
        parameter.setValue(dropdown.getSelectedIndex());
        doNotification();
    }
    
    @Override
    public void refreshInternal() {
        dropdown.setSelectedItem((String)parameter.getNaturalValue());
    }
    
    @Override
    protected int getMaxHeight() { 
        return 20; 
    }
    
}
