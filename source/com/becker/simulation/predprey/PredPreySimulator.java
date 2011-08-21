package com.becker.simulation.predprey;

import com.becker.common.math.function.Function;
import com.becker.simulation.common.ui.Simulator;
import com.becker.simulation.common.ui.SimulatorOptionsDialog;
import com.becker.simulation.graphing.GraphOptionsDialog;
import com.becker.simulation.predprey.creatures.Foxes;
import com.becker.simulation.predprey.creatures.Population;
import com.becker.simulation.predprey.creatures.Rabbits;
import com.becker.simulation.predprey.options.DynamicOptions;
import com.becker.ui.animation.AnimationFrame;
import com.becker.ui.renderers.MultipleFunctionRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.util.*;
import java.util.List;

/**
 * Simulates foxes (predators) and rabbits (prey) in the wild.
 *
 * @author Barry Becker
 */
public class PredPreySimulator extends Simulator {

    MultipleFunctionRenderer graph_;
    long iteration;

    Foxes foxes;
    Rabbits rabbits;

    PredPreyFunction rabbitFunction;
    PredPreyFunction foxFunction;

    DynamicOptions options_;

    /** Constructor */
    public PredPreySimulator() {
        super("Predator Prey Simulation");

        foxes = new Foxes();
        rabbits = new Rabbits();

        initGraph();
    }

    public List<Population> getCreatures() {
        List<Population> creatures = new ArrayList<Population>();
        creatures.add(foxes);
        creatures.add(rabbits);
        return creatures;
    }

    @Override
    protected void reset() {

        initGraph();
        options_.reset();
    }

    @Override
    protected double getInitialTimeStep() {
        return 1.0;
    }

    @Override
    public double timeStep() {
        iteration++;

        foxes.population = (int)
                Math.round(foxes.population * foxes.birthRate
                           - foxes.population * foxes.deathRate / rabbits.population);
        rabbits.population = (int)
                Math.round(rabbits.population * rabbits.birthRate
                           - foxes.population * rabbits.deathRate * rabbits.population);

        rabbitFunction.addValue(iteration, rabbits.population);
        foxFunction.addValue(iteration, foxes.population);

        return timeStep_;
    }

    protected void initGraph() {
        iteration = 0;

        rabbits.reset();
        foxes.reset();

        rabbitFunction = new PredPreyFunction(Rabbits.INITIAL_NUM_RABBITS);
        foxFunction = new PredPreyFunction(Foxes.INITIAL_NUM_FOXES);

        List<Function> functions = new LinkedList<Function>();
        functions.add(rabbitFunction);
        functions.add(foxFunction);

        List<Color> lineColors = new LinkedList<Color>();
        lineColors.add(Rabbits.COLOR);
        lineColors.add(Foxes.COLOR);

        graph_ = new MultipleFunctionRenderer(functions, lineColors);
    }


    @Override
    public JPanel createDynamicControls() {
        options_ = new DynamicOptions(this);
        return options_;
    }


    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new GraphOptionsDialog( frame_, this );
    }

    @Override
    public void paint( Graphics g ) {
        graph_.setSize(getWidth(), getHeight());
        graph_.paint(g);
    }

    public static void main( String[] args ) {
        final PredPreySimulator sim = new PredPreySimulator();

        sim.setPaused(true);
        JFrame f = new AnimationFrame( sim );
    }
}