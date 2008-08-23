/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.java2d.imageproc;

import com.becker.optimization.Parameter;
import java.awt.image.BufferedImageOp;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.*;
import java.util.Random;


/**
 * Contains a 
 * @author becker
 */
public class MetaImageOp {

    private Class<? extends BufferedImageOp> opClass;
    private BufferedImageOp op;
    
    private List<Parameter> parameters;
    
    private boolean isDynamic;
    
    private static Random RANDOM = new Random(1);
    
    /**
     * Use this constructor if no parameters.
     * @param opClass
     */
    public MetaImageOp(BufferedImageOp op) {
        // an empty list of parameters because there are none.
        this.op = op;
        isDynamic = false;
    }
    
    /**
     * @param opClass the operator class.
     * @param params all the parameters that need to be set on the op.
     */
    public MetaImageOp(Class<? extends BufferedImageOp> opClass, List<Parameter> params) {
                
        this.opClass = opClass;
        this.parameters = params;       
        isDynamic = true;
    }
    
    /**
     * 
     * @return a concrete instance.
     */
    public  BufferedImageOp getInstance()
    {
        if (!isDynamic) {
            return op;
        }
        try {

            this.op = opClass.newInstance();
            setParameters(op);            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(MetaImageOp.class.getName()).log(Level.SEVERE, null, ex);  
        }
        return op;
    }
    
    private void setParameters(BufferedImageOp filter) 
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        System.out.println("op="+filter.getClass().getSimpleName());
        for (Parameter p : parameters) {
            // the name must match the property (e.g. foo will be set using setFoo)
            String methodName = 
                    "set" +  p.getName().substring(0, 1).toUpperCase() + p.getName().substring(1);
            Method method = filter.getClass().getDeclaredMethod(methodName, p.getType());
            System.out.println("v=" +p.getValue() + "  type="+p.getType().getName() + "  method="+methodName);
          
            Object[] args = new Object[1];
            p.tweakValue(0.1, RANDOM);
            args[0] = (p.getType() == float.class) ? (float) p.getValue() : (int) p.getValue(); 

            method.invoke(filter, args); // p.getType().cast(p.getValue()));
        } 
        
    }
}
