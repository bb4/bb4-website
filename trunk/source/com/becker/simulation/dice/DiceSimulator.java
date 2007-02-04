package com.becker.simulation.dice;

import com.becker.optimization.*;
import com.becker.simulation.common.*;

import java.util.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Feb 4, 2007
 */
public class DiceSimulator extends Simulator {


    protected static final double TIME_STEP = 1.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 50;

    private HistogramRenderer histogram_;
    private int[] data_;

    private int numDice_ = 2;
    private int numSides_ = 6;

    private Random random_ = new Random(0);



    public DiceSimulator() {
        super("Dice HistogramRenderer");
        commonInit();
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


    private void initHistogram() {
        data_ = new int[numDice_ * (numSides_-1) + 1];
        histogram_ = new HistogramRenderer(data_, numDice_);
        random_.setSeed((int)(Math.random() * 1000));
    }

    private void commonInit() {
        initCommonUI();
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
        this.setPreferredSize(new Dimension( 600, 500 ));
    }

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new DiceOptionsDialog( frame_, this );
    }


    protected double getInitialTimeStep() {
        return TIME_STEP;
    }

    public double timeStep()
    {
        if ( !isPaused() ) {
            int total = 0;
            for (int i=0; i<numDice_; i++) {
               total += random_.nextInt(numSides_);
            }
            data_[total]++;
        }
        return timeStep_;
    }



    public void paint( Graphics g )
    {
        histogram_.setSize(getWidth(), getHeight());
        histogram_.paint(g);
    }


    protected String getFileNameBase()
    {
        return "dice";
    }

}


