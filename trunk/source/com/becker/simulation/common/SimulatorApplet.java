package com.becker.simulation.common;


import com.becker.common.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 17, 2005
 */
public class SimulatorApplet extends JApplet {


    private static Simulator simulator_ = null;
    private ResizableAppletPanel resizablePanel_ = null;
    private static final boolean RUN_OPTIMIZATION = false;
    private static final String DEFAULT_SIMULATOR = "com.becker.simulation.snake.SnakeSimulator";

    public SimulatorApplet() {}

    public SimulatorApplet(String simulatorClassName) {
         simulator_ = createSimulationFromClassName(simulatorClassName);

        JFrame baseFrame = GUIUtil.showApplet( this, "Simulation Applet" );
        baseFrame.setSize( simulator_.getPreferredSize());
    }


    /**
     * the applet's init method
     */
    public void init()
    {

        this.getParameterInfo();
        if (simulator_ == null) {

            String className = getParameter("panel_class");
            className = className == null ? DEFAULT_SIMULATOR : className;
            simulator_ = createSimulationFromClassName(className);
        }

        JPanel animPanel = new AnimationPanel( simulator_ );
        animPanel.add( simulator_.createTopControls(), BorderLayout.NORTH );

        resizablePanel_ = new ResizableAppletPanel( animPanel );
        Container content = this.getContentPane();
        content.setLayout(new BorderLayout());

        content.add( resizablePanel_, BorderLayout.CENTER );
        simulator_.setVisible(true);
        content.setPreferredSize(simulator_.getPreferredSize());
        content.repaint();

    }

    private static  Simulator createSimulationFromClassName(String className) {
        if (className == null) {
            return null;
        }
        Class simulatorClass = Util.loadClass(className);
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
    }

}


