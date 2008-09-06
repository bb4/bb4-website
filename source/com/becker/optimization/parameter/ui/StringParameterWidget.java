/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.optimization.parameter.ui;

import com.becker.optimization.parameter.ParameterChangeListener;
import com.becker.optimization.parameter.Parameter;

import com.becker.optimization.parameter.StringParameter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;

/**
 *
 * @author becker
 */
public class StringParameterWidget extends ParameterWidget implements ActionListener {

    
    public StringParameterWidget(Parameter param, ParameterChangeListener listener) {
        super(param, listener);
    }
    
   /**
     * Create a ui widget appropriate for the parameter type.
     */
    protected void addChildren() {
             
           // create a dropdown
            StringParameter sparam = (StringParameter) parameter;
            JComboBox dropdown = new JComboBox(sparam.getStringValues().toArray());
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
        JComboBox dropdown = (JComboBox) e.getSource();
        parameter.setValue(dropdown.getSelectedIndex());
        doNotification();
    }
    
    protected int getMaxHeight() { 
        return 20; 
    }
    
}
