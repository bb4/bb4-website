package com.becker.simulation.reactiondiffusion;

import com.becker.optimization.*;
import com.becker.simulation.common.*;
import com.becker.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Reaction diffusion simulator.
 * based on work by Joakim Linde and modified by Barry Becker.
 *
 */
public class RDSimulator extends Simulator
                         implements ActionListener, AdjustmentListener{

    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "reactiondiffusion/rdFrame";

    private GrayScott grayScott_;
    private RDRenderer renderer_;

    // if true it will save all the animation steps to files
    public static final boolean RECORD_ANIMATION = false;

    protected static final double TIME_STEP = 1.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;


    private static final int SLIDER_RANGE = 2000;
    private Scrollbar kSlider, fSlider;
    private Button restartButton;
    private Label fLabel, kLabel;

    public RDSimulator() {
        super("Reaction Diffusion");
        commonInit();
    }


    private void commonInit() {
        initCommonUI();

        grayScott_ = new GrayScott(200, 200, GrayScott.F0, GrayScott.K0, 0.01);
        renderer_ = new RDRenderer(grayScott_);

        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        //int s = (int) envRenderer_.getScale();
        //setPreferredSize(new Dimension(300, 300));
    }

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new RDOptionsDialog( frame_, this );
    }


    protected double getInitialTimeStep() {
        return TIME_STEP;
    }

    public double timeStep()
    {
        if ( !isPaused() ) {
            grayScott_.timeStep( timeStep_ );
        }
        return timeStep_;
    }


    public void setScale( double scale ) {
        //envRenderer_.setScale(scale);

    }
    public double getScale() {
        //return envRenderer_.getScale();
        return 0.01;
    }

    public JPanel createDynamicControls() {

        JPanel dControls = new JPanel(new GridBagLayout());

        restartButton = new Button("Restart");
        restartButton.addActionListener(this);

        int pos = (int) (GrayScott.K0 * SLIDER_RANGE / 0.3);
        kSlider = new Scrollbar(Scrollbar.HORIZONTAL, pos, 1, 0, SLIDER_RANGE);
        kSlider.addAdjustmentListener(this);

        kLabel = new Label("k =                         ");
        kLabel.setText("k = " + Util.formatNumber(GrayScott.K0));

        pos = (int) (GrayScott.F0 * SLIDER_RANGE / 0.3);
        fSlider = new Scrollbar(Scrollbar.HORIZONTAL, pos, 1, 0, SLIDER_RANGE);
        fSlider.addAdjustmentListener(this);

        fLabel = new Label("f =                         ");
        fLabel.setText("f = " + Util.formatNumber(GrayScott.F0));

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        c.gridy = 1;
        gridbag.setConstraints(fLabel, c);
        add(fLabel);

        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(fSlider, c);
        add(fSlider);

        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(kLabel, c);
        add(kLabel);

        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(kSlider, c);
        add(kSlider);

        c.gridy = 5;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(restartButton, c);
        add(restartButton);

        return dControls;
    }


    public void doOptimization()
    {
       System.out.println("not yet implemented");
    }

    public int getNumParameters() {
        return 0;
    }

    /**
     * *** implements the key method of the Optimizee interface
     *
     * evaluates the fitness.
     */
    public double evaluateFitness( ParameterArray params )
    {
        assert false : "not implemented yet";
        return 0.0;
    }

    public double getOptimalFitness() {
        return 0;
    }


    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        renderer_.render(g2 );
    }

    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }


    public void actionPerformed(ActionEvent e) {
        grayScott_.initialState();
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getAdjustable() == fSlider) {
            grayScott_.setF(fSlider.getValue() * 0.3 / SLIDER_RANGE);
            fLabel.setText("f = " + Util.formatNumber(grayScott_.getF()));
        }
        else {
            grayScott_.setK(kSlider.getValue() * 0.3 / SLIDER_RANGE);
            kLabel.setText("k = " + Util.formatNumber(grayScott_.getK()));
        }
    }

}
