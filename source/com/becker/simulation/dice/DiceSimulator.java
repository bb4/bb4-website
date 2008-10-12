package com.becker.simulation.dice;

import com.becker.ui.HistogramRenderer;
import com.becker.simulation.common.*;
import java.awt.*;
import java.util.*;

/**
 * Simluates the rolling of N number of M sided dice lots of times
 * to see what kind of distribution of numbers you get.
 * 
 * @author Barry Becker Date: Feb 4, 2007
 */
public class DiceSimulator extends DistributionSimulator {

    private int numDice_ = 2;
    private int numSides_ = 6;


    public DiceSimulator() {
        super("Dice Histogram");
        initHistogram();
    }

    public void setNumDice(int numDice) {
        numDice_ = numDice;
        initHistogram();
    }

    public void setNumSides(int numSides) {
        numSides_ = numSides;
        initHistogram();
    }

    protected void initHistogram() {
        data_ = new int[numDice_ * (numSides_-1) + 1];
        histogram_ = new HistogramRenderer(data_, numDice_);
    }

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new DiceOptionsDialog( frame_, this );
    }
   
    protected int getXPositionToIncrement() {
        int total = 0;
        for (int i=0; i<numDice_; i++) {
           total += random_.nextInt(numSides_);
        }
        return total;
    }
    
    protected String getFileNameBase()
    {
        return "dice";
    }

    public static void main( String[] args )
    {
        final DiceSimulator sim = new DiceSimulator();
        sim.setNumDice(3);
        sim.setNumSides(6);
        runSimulation(sim);
    }
}


