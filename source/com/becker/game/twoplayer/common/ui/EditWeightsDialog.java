package com.becker.game.twoplayer.common.ui;

import com.becker.game.common.GameWeights;
import com.becker.game.common.GameContext;
import com.becker.optimization.Parameter;
import com.becker.optimization.ParameterArray;
import com.becker.ui.GradientButton;
import com.becker.ui.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Allow for editing the polynomial weights used in the static
 * evaluation function
 *
 * @author Barry Becker
 */
class EditWeightsDialog extends OptionsDialog implements ActionListener
{
    private JScrollPane scrollPane_ = null;
    private JPanel weightsPanel_ = null;

    private final ParameterArray weights_;
    private final GameWeights gameWeights_;
    private JTextField[] weightFields_ = null;

    private GradientButton okButton_;

    private static final Dimension LABEL_DIM = new Dimension( 200, 20 );
    private static final Dimension FIELD_DIM = new Dimension( 100, 20 );
    private static final Dimension WEIGHT_PANEL_DIM = new Dimension( 900, 25 );

    // constructor
    public EditWeightsDialog( Frame parent, ParameterArray weights, GameWeights gameWeights )
    {
        super( parent );

        // make a copy of the weights so we can cancel if desired
        weights_ = weights;  // this does not make a copy.
        gameWeights_ = gameWeights;

        initUI();
    }

    public String getTitle()
    {
        return GameContext.getLabel("EDIT_WEIGHTS");
    }

    protected void initUI()
    {
        mainPanel_.setLayout( new BoxLayout( mainPanel_, BoxLayout.Y_AXIS ) );

        JLabel instructLabel = new JLabel( GameContext.getLabel("EDIT_WTS_BELOW") );
        weightsPanel_ = createWeightsPanel( weights_ );
        scrollPane_ = new JScrollPane( weightsPanel_ );

        mainPanel_.add( instructLabel );
        mainPanel_.add( scrollPane_ );
        mainPanel_.add( createButtonsPanel() );

        this.getContentPane().add( mainPanel_ );
        pack();
    }

    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("ACCEPT_WTS") );
        initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL_EDITS") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }

    private JPanel createWeightsPanel( ParameterArray weights )
    {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createEtchedBorder() );
        int len = weights.size();
        weightFields_ = new JTextField[len];

        for ( int i = 0; i < len; i++ ) {
            JPanel weightPanel = new JPanel( new FlowLayout() );
            weightPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
            weightPanel.setMaximumSize( WEIGHT_PANEL_DIM );
            JLabel lab = new JLabel( gameWeights_.getName( i )+" [0.0 - "+gameWeights_.getMaxWeight(i)+"]");
            lab.setToolTipText( gameWeights_.getDescription( i ) );
            lab.setAlignmentX( Component.LEFT_ALIGNMENT );
            lab.setPreferredSize( LABEL_DIM );
            weightFields_[i] = new JTextField( Double.toString( weights_.get(i).value ) );
            weightFields_[i].setAlignmentX( Component.LEFT_ALIGNMENT );
            weightFields_[i].setPreferredSize( FIELD_DIM );
            weightPanel.add( lab );
            weightPanel.add( weightFields_[i] );
            //weightPanel.setBorder(BorderFactory.createRaisedBevelBorder());
            p.add( weightPanel );
        }
        p.add( Box.createVerticalGlue() ); // fill extra space at the bottom
        return p;
    }

    private void ok()
    {
        //System.out.println("size: "+r.intValue()+", "+c.intValue());
        int len = weights_.size();
        String sErrors = "";
        for ( int i = 0; i < len; i++ ) {
            double v = Double.parseDouble( weightFields_[i].getText() );
            Parameter p = weights_.get(i);
            if ( v<p.minValue)
                sErrors += v+" is too small for "+p.name+". The min vlaue of "+p.minValue+" will be used.\n";
            else if (v>p.maxValue)
                sErrors += v+" is too big for "+p.name+". The max value of "+p.maxValue+" will be used.\n";
            else
                weights_.get(i).value = v;
        }
        if (sErrors.length() >1)
            JOptionPane.showMessageDialog(this, sErrors, "Parameters Out of Range", JOptionPane.WARNING_MESSAGE);
    }


    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if ( source == okButton_ ) {
            ok();
            dispose();
        }
        else if ( source == cancelButton_ ) {
            cancel();
        }
    }
}

