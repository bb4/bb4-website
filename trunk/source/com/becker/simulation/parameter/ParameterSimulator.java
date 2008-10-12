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


    public ParameterSimulator() {
        super("Parameter Histogram");
        initHistogram();
    }

    public void setParameter(Parameter parameter) {
        parameter_ = parameter;
        initHistogram();
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
         parameter_.randomizeValue(random_);  
         int xpos;
        if (parameter_.isIntegerOnly()) {
           xpos = (int)parameter_.getValue();
        }
        else {
            xpos =  (int)((NUM_DOUBLE_BINS-1) * parameter_.getValue()/ parameter_.getRange());
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
        //sim.setParameter(new IntegerParameter(2, 0, 5, "int"));
        runSimulation(sim);
    }
}


