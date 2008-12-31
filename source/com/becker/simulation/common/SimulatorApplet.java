package com.becker.simulation.common;

import com.becker.common.*;
import com.becker.common.util.Util;
import com.becker.ui.*;
import com.becker.ui.animation.*;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for all simulator applets.
 * Resizable applet for showing simulations.
 *
 * @author Barry Becker   Date: Sep 17, 2005
 */
public class SimulatorApplet extends ApplicationApplet {

    private static Simulator simulator_ = null;

    private static final boolean RUN_OPTIMIZATION = false;
    private static final String DEFAULT_SIMULATOR = "com.becker.simulation.snake.SnakeSimulator";

    public  SimulatorApplet() {
        simulator_ = null;
    }

    /**
     * Construct the applet
     * @param simulatorClassName  name of the simulator class to show.
     */
    public SimulatorApplet(String simulatorClassName) {

        simulator_ = createSimulationFromClassName(simulatorClassName);
    }

    public String getTitle() {
        return simulator_.getName();
    }

    /**
     * create and initialize the simulation
     */
    public JPanel createMainPanel()
    {
        //this.getParameterInfo();
        if (simulator_ == null) {

            String className = getParameter("panel_class");
            className = className == null ? DEFAULT_SIMULATOR : className;
            simulator_ = createSimulationFromClassName(className);
        }

        JPanel animPanel = new AnimationPanel( simulator_ );
        animPanel.add( simulator_.createTopControls(), BorderLayout.NORTH );
        JPanel dynamicControls = simulator_.createDynamicControls();
        if (dynamicControls != null) {
            animPanel.add( dynamicControls, BorderLayout.EAST );
        }

        simulator_.setVisible(true);
        //System.out.println("size="+simulator_.getPreferredSize());
        //content.setPreferredSize(simulator_.getPreferredSize());
        //content.repaint();
        setSize(simulator_.getPreferredSize());
        return animPanel;
    }

    private static  Simulator createSimulationFromClassName(String className) {
        if (className == null) {
            return null;
        }
        System.out.println("about to load: " + className);
        Class simulatorClass = ClassLoaderSingleton.loadClass(className);
        Simulator simulator = null;

        try {

            simulator = (Simulator) simulatorClass.newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return simulator;
    }

    /**
     * the applets start method
     */
    public void start()
    {
        if (RUN_OPTIMIZATION)
            simulator_.doOptimization();
    }

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    public void setSize( int width, int height )
    {
        resizablePanel_.setSize( width, height );
    }

    //------ Main method - to allow running as an application ---------------------
    public static void main( String[] args )
    {

        // create a simulator panel of the appropriate type based on the name of the class passed in.
        // if no simulator is specified as an argument, then we use the default.
        String simulatorClassName = (args.length > 0)? args[0] : DEFAULT_SIMULATOR;

        SimulatorApplet applet = new SimulatorApplet(simulatorClassName);
        GUIUtil.showApplet( applet, applet.getTitle() );
    }

}


