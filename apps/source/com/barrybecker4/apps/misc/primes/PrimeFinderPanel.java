package com.barrybecker4.apps.misc.primes;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.ui.components.ScrollingTextArea;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Find the specified number of prime numbers and display the results in a
 * {@code ScrollingTextArea}.  While this is computing, update progress in a {@code JProgressBar}.
 *
 * @author Barry Becker
 */
public class PrimeFinderPanel extends JPanel {

    private ScrollingTextArea textArea;
    private JProgressBar progressBar;

    /** constructor */
    public PrimeFinderPanel() {
        textArea = new ScrollingTextArea(80, 160);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        setLayout(new BorderLayout());
        add(progressBar, BorderLayout.NORTH);
        add(textArea, BorderLayout.CENTER);
    }

    /**
     * Computes numPrimesToFind in a separate worker thread
     * @param numPrimesToFind  number of primes to compute.
     */
    public void startComputing(int numPrimesToFind) {

        PrimeNumbersTask task = new PrimeNumbersTask(textArea, numPrimesToFind);

        task.addPropertyChangeListener(
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equals(evt.getPropertyName())) {
                        int progress = (Integer) evt.getNewValue();
                        progressBar.setValue(progress);
                        progressBar.setString(progress + "% done");
                    }
                }
            }
        );

        task.execute();

        try {
            // prints the  number of prime numbers that were computed
            System.out.println("Number of primes found = " + FormatUtil.formatNumber(task.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
