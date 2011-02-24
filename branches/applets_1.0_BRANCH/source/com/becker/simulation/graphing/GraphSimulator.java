package com.becker.simulation.graphing;

import com.becker.common.math.function.Function;
import com.becker.simulation.common.Simulator;
import com.becker.simulation.common.SimulatorOptionsDialog;
import com.becker.ui.animation.AnimationFrame;
import com.becker.ui.renderers.FunctionRenderer;

import javax.swing.*;
import java.awt.*;


/**
 * Simluates graphing a function
 *
 * @author Barry Becker
 */
public class GraphSimulator extends Simulator {

    FunctionRenderer graph_;
    Function function_;


    public GraphSimulator() {
        super("Graph");
        initGraph();
    }

    public void setFunction(Function function) {
        function_ = function;
        initGraph();
    }

    @Override
    protected void reset() {
        initGraph();
    }

    @Override
    protected double getInitialTimeStep() {
        return 1.0;
    }

@Override
    public double timeStep()
    {
        return timeStep_;
    }


    protected void initGraph() {
        if (function_ == null) {

            function_ = FunctionType.DIAGONAL.function;
        }
        graph_ = new FunctionRenderer(function_);
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new GraphOptionsDialog( frame_, this );
    }


    @Override
    public void paint( Graphics g )
    {
        graph_.setSize(getWidth(), getHeight());
        graph_.paint(g);
    }

    public static void main( String[] args )
    {
        final GraphSimulator sim = new GraphSimulator();

        sim.setPaused(true);
        JFrame f = new AnimationFrame( sim );
    }
}