/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * NumberInputPanel is a panel that has a label on the left
 * and an input on the right that accepts only numbers
 *
 * @author Barry Becker
 */
public class NumberInput extends JPanel {

    private JTextField numberField_;
    private double initialValue_;
    private double min_;
    private double max_;

    /**
     * Often the initial value cannot be set when initializing the content of a dialog.
     * This uses a default of 0 until the real default can be set with setInitialValue.
     * @param labelText label for the number input element
     */
    public NumberInput( String labelText) {
       this( labelText, 0, null, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    /**
     * @param labelText label for the number input element
     * @param initialValue the value to use if nothing else if entered. shows in the ui.
     */
    public NumberInput( String labelText, int initialValue ) {
       this( labelText, initialValue, null, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    public NumberInput( String labelText, double initialValue ) {
       this( labelText, initialValue, null, -Double.MAX_VALUE, Double.MAX_VALUE, false);
    }

    /**
     * @param labelText label for the number input element
     * @param initialValue the value to use if nothing else if entered. shows in the ui.
     * @param toolTip the tooltip for the whole panel
     */
    public NumberInput( String labelText, double initialValue, String toolTip,
                        double minAllowed, double maxAllowed, boolean integerOnly ) {

        initialValue_ = initialValue;
        setMin(minAllowed);
        setMax(maxAllowed);
        String initialVal = integerOnly? Integer.toString((int) initialValue) : Double.toString(initialValue);
        numberField_ = new JTextField(initialVal);

        setLayout( new BorderLayout());

        setAlignmentX( Component.LEFT_ALIGNMENT );

        JLabel label = new JLabel( labelText );
        add( label, BorderLayout.WEST );

        if (toolTip == null)
            numberField_.setToolTipText( "enter a number in the suggested range" );
        else
            numberField_.setToolTipText( toolTip );
        numberField_.setPreferredSize( new Dimension( 50, 20 ) );

        numberField_.addKeyListener( new NumberKeyAdapter(integerOnly));

        JPanel numPanel = new JPanel();
        numPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

        numPanel.add( numberField_ );

        add(numPanel, BorderLayout.EAST);

        if (toolTip != null)
            this.setToolTipText(toolTip);
        else
            this.setToolTipText(labelText);
    }

    public double getValue() {
        String text = numberField_.getText();
        if (text.length() == 0) {
            return 0;
        }
        double v = Double.parseDouble(text);
        if (v < getMin()) {
            numberField_.setText(""+ getMin());
            v = getMin();
        }
        else  if (v > getMax()) {
            numberField_.setText(""+ getMax());
            v = getMax();
        }
        return v;
    }

    public int getIntValue() {
        String text = getNumberField().getText();
        if (text.length() == 0) {
            return 0;
        }
        int v = Integer.parseInt(text);
        if (v < getMin()) {
            numberField_.setText(Integer.toString((int) getMin()));
        }
        else  if (v > getMax()) {
            numberField_.setText(Integer.toString((int) getMax()));
        }
        return v;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getNumberField().setEnabled(enabled);
    }

    private JTextField getNumberField() {
        return numberField_;
    }

    @Override
    public synchronized void addKeyListener(KeyListener keyListener) {
        getNumberField().addKeyListener(keyListener);
    }

    public void setInitialValue(double value)
    {
        this.initialValue_ = value;
    }

    public double getMin() {
        return min_;
    }

    public void setMin(double min) {
        this.min_ = min;
    }

    public double getMax() {
        return max_;
    }

    public void setMax(double max) {
        this.max_ = max;
    }


    /**
     * Handle number input. Give dynamic feedback if invalid.
     */
    private class NumberKeyAdapter extends KeyAdapter {

        boolean integerOnly_;


        protected NumberKeyAdapter(boolean integerOnly) {
            integerOnly_ = integerOnly;
        }

        @Override
        public void keyTyped( KeyEvent key )  {
            char c = key.getKeyChar();
            if ( c >= 'A' && c <= 'z' ) {
                JOptionPane.showMessageDialog( null,
                        "no non-numeric characters allowed!", "Error", JOptionPane.ERROR_MESSAGE );
                // clear the input text since it is in error
                numberField_.setText( "" );
                key.consume(); // don't let it get entered
            }
            else if ((integerOnly_ && c == '.') || (getMin() >= 0 && c == '-')) {
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
