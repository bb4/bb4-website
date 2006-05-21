package com.becker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * NumberInputPanel is a panel that has a label on the left
 * and an input on the right that accepts only numbers
 *
 * @author Barry Becker
 */
public class NumberInput extends JPanel
{

    private JTextField numberField_;
    private double initialValue_;
    double min_;
    double max_;

    protected static final int TEXT_FIELD_WIDTH = 50;
    protected static final Dimension TEXT_FIELD_DIM = new Dimension( TEXT_FIELD_WIDTH, OptionsDialog.ROW_HEIGHT );


    /**
     * @param labelText label for the number input element
     * @param initialValue the value to use if nothing else if entered. shows in the ui.
     */
    public NumberInput( String labelText, int initialValue )
    {
       this( labelText, initialValue, null, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    public NumberInput( String labelText, double initialValue )
    {
       this( labelText, initialValue, null, -Double.MAX_VALUE, Double.MAX_VALUE, false);
    }

    /**
     * @param labelText label for the number input element
     * @param initialValue the value to use if nothing else if entered. shows in the ui.
     * @param toolTip the tooltip for the whole panel
     */
    public NumberInput( String labelText, double initialValue, String toolTip,
                        double minAllowed, double maxAllowed, boolean integerOnly )
    {

        initialValue_ = initialValue;
        min_ = minAllowed;
        max_ = maxAllowed;
        String initialVal = integerOnly? Integer.toString((int) initialValue) : Double.toString(initialValue);
        numberField_ = new JTextField(initialVal);
        numberField_.setMaximumSize( TEXT_FIELD_DIM );

        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
        setAlignmentX( Component.LEFT_ALIGNMENT );
        JLabel label = new JLabel( labelText );
        add( label );

        if (toolTip == null)
            numberField_.setToolTipText( "enter a number in the suggested range" );
        else
            numberField_.setToolTipText( toolTip );
        numberField_.setPreferredSize( new Dimension( 50, 15 ) );
        numberField_.setMinimumSize( new Dimension( 25, 15 ) );

        numberField_.addKeyListener( new NumberKeyAdapter(integerOnly));

        JPanel numPanel = new JPanel();
        numPanel.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
        numPanel.add( numberField_ );
        this.add(numPanel);

        if (toolTip!=null)
            this.setToolTipText(toolTip);
        else
            this.setToolTipText(labelText);
    }

    public void setWidth(int width) {
        numberField_.getMaximumSize().setSize(width, numberField_.getMaximumSize().getHeight());
    }

    public double getValue() {
        String text = numberField_.getText();
        if (text.length() == 0) {
            //numberField_.setText("" + initialValue_);
            //return initialValue_;
            return 0;
        }
        double v = Double.parseDouble(text);
        if (v < min_) {
            numberField_.setText(""+ min_);
        }
        else  if (v > max_) {
            numberField_.setText(""+ max_);
        }
        return v;
    }

    public int getIntValue() {
        String text = getNumberField().getText();
        if (text.length() == 0) {
            //numberField_.setText("" + (int) initialValue_);
            //return (int) initialValue_;
            return 0;
        }
        int v = Integer.parseInt(text);
        if (v < min_) {
            numberField_.setText(Integer.toString((int) min_));
        }
        else  if (v > max_) {
            numberField_.setText(Integer.toString((int) max_));
        }
        return v;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getNumberField().setEnabled(enabled);
    }

    private JTextField getNumberField() {
        return numberField_;
    }

    public synchronized void addKeyListener(KeyListener keyListener) {
        getNumberField().addKeyListener(keyListener);
    }


    /**
     * Handle number input. Give dynamic feedback if invalid.
     */
    private class NumberKeyAdapter extends KeyAdapter {

        boolean integerOnly_;


        protected NumberKeyAdapter(boolean integerOnly) {
            integerOnly_ = integerOnly;
        }

        public void keyTyped( KeyEvent key )  {
            char c = key.getKeyChar();
            if ( c >= 'A' && c <= 'z' ) {
                JOptionPane.showMessageDialog( null,
                        "no non-numeric characters allowed!", "Error", JOptionPane.ERROR_MESSAGE );
                // clear the input text since it is in error
                numberField_.setText( "" );
                key.consume(); // don't let it get entered
            }
            else if ((integerOnly_ && c == '.') || (min_ >= 0 && c == '-')) {
                JOptionPane.showMessageDialog( null,
                        "unexpected character: " + c, "Error", JOptionPane.ERROR_MESSAGE);
                key.consume();
            }
            else if ((c < '0' || c > '9') && (c != 8) && (c != '.') && (c != '-')) {  // 8=backspace
                JOptionPane.showMessageDialog( null,
                        "no non-numeric character ("+c+") not allowed!", "Error", JOptionPane.ERROR_MESSAGE );
                key.consume(); // don't let it get entered
            }

            String txt = numberField_.getText();
            if (txt.length() > 1)  {
                try {
                    if (integerOnly_ && txt.length() > 0) {
                        Integer.parseInt(txt);
                    }  else {
                        Double.parseDouble(txt);
                    }
                } catch (NumberFormatException e) {
                    // if an error occurred during parsing then revert to the initial value
                    numberField_.setText("" + initialValue_);
                    System.out.println("Warning: could not parse " + txt + " as a number. \n"
                                       + e.getMessage());
                }
            }

        }
    }
}
