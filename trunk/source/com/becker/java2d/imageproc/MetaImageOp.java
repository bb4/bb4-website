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
 * @author Barry Becker
 */
public class MetaImageOp {

    private Class<? extends BufferedImageOp> opClass;
    private BufferedImageOp op;
    
    // list of params based on the params specified in the xml file.
    private List<Parameter> parameters;
   
    private boolean isDynamic;
    
    /** Ensures that all randomness is repeatable. */
    private static Random RANDOM = new Random(1);
    
    public FilterType type;
    
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
        return getRandomInstance(0);
    }
    
    /**
     * @param randomVariance number of standard deviations to use when randomizing params.
     * @return a concrete instance with tweaked parameters.
     */
    public  BufferedImageOp getRandomInstance(float randomVariance)
    {
        if (!isDynamic) {
            return op;
        }
        try {

            this.op = opClass.newInstance();
            setParameters(op, randomVariance);            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(MetaImageOp.class.getName()).log(Level.SEVERE, null, ex);  
        }
        return op;
    }
    
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    /**
     * Call the methods on the filter to set its custom parameters.
     * @param filter
     * @param randomVariance
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.IllegalArgumentException
     * @throws java.lang.reflect.InvocationTargetException
     */
    private void setParameters(BufferedImageOp filter, float randomVariance) 
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        System.out.println("op="+filter.getClass().getSimpleName());
        for (Parameter p : parameters) {
            // the name must match the property (e.g. foo will be set using setFoo)
            String methodName = 
                    "set" +  p.getName().substring(0, 1).toUpperCase() + p.getName().substring(1);
            Method method = filter.getClass().getDeclaredMethod(methodName, p.getType());
            //System.out.println("v=" +p.getValue() + "  type="+p.getType().getName() + "  method="+methodName);
          
            Object[] args = new Object[1];
            p.tweakValue(randomVariance, RANDOM);
            boolean isFloat =  (p.getType().equals(float.class)) ;
            //System.out.println("type to cast to = "+p.getType() + " isFloat="+isFloat);
            //args[0] = isFloat ? (float) p.getValue() :  (int) p.getValue();
            
            if (isFloat) {
                args[0] = (float) p.getValue();
            } else  {
                args[0] = (int) p.getValue(); 
            }
            //System.out.println("arg="+args[0] + " type="+args[0].getClass().getName());
            method.invoke(filter, args); // p.getType().cast(p.getValue()));            
        } 
        
    }
}
