/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.optimization.parameter.ui;

import com.becker.optimization.parameter.BooleanParameter;
import com.becker.optimization.parameter.ParameterChangeListener;
import com.becker.optimization.parameter.Parameter;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;

/**
 *
 * @author becker
 */
public class BooleanParameterWidget extends ParameterWidget implements ItemListener {

    public BooleanParameterWidget(Parameter param, ParameterChangeListener listener) {
        super(param, listener);
    }
    
   /**
     * Create a ui widget appropriate for the parameter type.
     */
    protected void addChildren() {
             
            JCheckBox cb = new JCheckBox();            
            cb.setText(parameter.getName());
       
            BooleanParameter bparam = (BooleanParameter) parameter;
            cb.setSelected((Boolean)bparam.getNaturalValue());            
            cb.addItemListener(this);       
            add(cb, BorderLayout.CENTER);
    }
    
     /**
      * Called when a checkbox selection has changed for a BooleanParameter
      * @param e the item event
      */
    public void itemStateChanged(ItemEvent e) { 
        JCheckBox cb = (JCheckBox) e.getSource();
        parameter.setValue(cb.isSelected()?1:0);
        doNotification();
    }
    
    protected int getMaxHeight() { 
        return 20; 
    }
    
}
