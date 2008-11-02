package com.becker.simulation.parameter;

import com.becker.simulation.dice.*;
import com.becker.ui.HistogramRenderer;
import com.becker.simulation.common.*;
import com.becker.optimization.parameter.Parameter;
import java.awt.*;
import java.util.*;

/**
 * Simluates the rolling of N number of M sided dice lots of times
 * to see what kind of distribution of numbers you get.
 * 
 * @author Barry Becker Date: Feb 4, 2007
 */
public class ParameterSimulator extends DistributionSimulator {

    private static final int NUM_DOUBLE_BINS = 1000;

    // init with some default
    private Parameter parameter_ =  ParameterDistributionType.values()[0].getParameter();

    private boolean showRedistribution_ = true;

    public ParameterSimulator() {
        super("Parameter Histogram");
        initHistogram();
    }

    public void setParameter(Parameter parameter) {
        parameter_ = parameter;
        initHistogram();
    }
    
    public void setShowRedistribution(boolean show) {
        showRedistribution_ = show;
    }
    
    public boolean isShowRedistribution() {
        return showRedistribution_;
    }

    protected void initHistogram() {
        if (parameter_.isIntegerOnly()) {
            data_ = new int[(int)parameter_.getRange() + 1];
            histogram_ = new HistogramRenderer(data_, 0);
        }
        else {
            data_ = new int[NUM_DOUBLE_BINS];
            histogram_ = 
                    new HistogramRenderer(data_, parameter_.getMinValue(), 
                                                            parameter_.getRange()/NUM_DOUBLE_BINS);
        }                
    }

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new ParameterOptionsDialog( frame_, this );
    }
    
    protected int getXPositionToIncrement() {
        if (showRedistribution_) {
            parameter_.randomizeValue(random_);  
        }
        else {
            double scale = parameter_.isIntegerOnly()?  parameter_.getRange() + 1.0 : parameter_.getRange();
            double v = parameter_.getMinValue() + random_.nextDouble() * scale;
            parameter_.setValue(v);
        }
        
        //if (Math.abs(setValue - parameter_.getValue()) >0.5) {
        //    System.out.println("setValue="+setValue +" but got "+parameter_.getValue());
        //}
        int xpos;
        if (parameter_.isIntegerOnly()) {
           xpos = (int)parameter_.getValue();
        }
        else {
            xpos =  (int)((NUM_DOUBLE_BINS-1) * parameter_.getValue() / parameter_.getRange());
        }
         return xpos;
    }

    protected String getFileNameBase()
    {
        return "parameter";
    }

    public static void main( String[] args )
    {
        final ParameterSimulator sim = new ParameterSimulator();
        runSimulation(sim);
    }
}


