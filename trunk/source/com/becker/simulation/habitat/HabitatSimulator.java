package com.becker.simulation.habitat;

import com.becker.simulation.common.ui.Simulator;
import com.becker.simulation.common.ui.SimulatorOptionsDialog;
import com.becker.simulation.graphing.GraphOptionsDialog;
import com.becker.simulation.habitat.creatures.Populations;
import com.becker.simulation.habitat.options.DynamicOptions;
import com.becker.ui.animation.AnimationFrame;
import com.becker.ui.renderers.MultipleFunctionRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Simulates foxes (predators) and rabbits (prey) in the wild.
 *
 * @author Barry Becker
 */
public class HabitatSimulator extends Simulator {

    /** Number of times greater area to allocate to the habitat compared to the graph. */
    private static final int HABITAT_TO_GRAPH_RATIO = 3;

    private HabitatRenderer habitatRenderer_;
    private MultipleFunctionRenderer graphRenderer_;
    private Populations populations;
    private DynamicOptions options_;


    /** Constructor */
    public HabitatSimulator() {

        super("Habitat Simulation");
        setBackground(Color.WHITE);
        populations = new Populations();
        initialize();
    }

    @Override
    protected void reset() {

        this.setPaused(true);
        populations.initialize();
        initialize();
        options_.reset();
    }

    @Override
    protected double getInitialTimeStep() {
        return 1.0;
    }

    @Override
    public double timeStep() {

        options_.update();
        populations.nextDay();

        return timeStep_;
    }

    protected void initialize() {
        habitatRenderer_ = new HabitatRenderer(populations);
        graphRenderer_ = populations.createFunctionRenderer();
    }

    public Populations getPopulations() {
        return populations;
    }

    /**
     * Draw the population graph under the habitat.
     * @param g
     */
    @Override
    public void paint( Graphics g ) {

        int denom = HABITAT_TO_GRAPH_RATIO + 1;
        habitatRenderer_.setSize(getWidth(), HABITAT_TO_GRAPH_RATIO * getHeight()/denom);
        habitatRenderer_.paint(g);

        graphRenderer_.setPosition(0, HABITAT_TO_GRAPH_RATIO * getHeight()/denom);
        graphRenderer_.setSize(getWidth(), getHeight()/denom);
        graphRenderer_.paint(g);
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

    public static void main( String[] args ) {
        final HabitatSimulator sim = new HabitatSimulator();

        sim.setPaused(true);
        JFrame f = new AnimationFrame( sim );
    }
}