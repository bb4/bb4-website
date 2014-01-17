package com.barrybecker4.apps.misc.primes;

import com.barrybecker4.ui.components.Appendable;

import javax.swing.SwingWorker;
import java.util.List;

/**
 * Finds a big set of prime numbers in a separate worker thread so the UI does not freeze.
 * The UI is updated periodically using the publish method.
 *
 * @author Barry Becker
 */
class PrimeNumbersTask extends SwingWorker<Integer, Integer> {

    private Appendable textArea;
    private int numbersToFind;
    private PrimeNumberGenerator generator;
    int numFoundSoFar;


    PrimeNumbersTask(Appendable appendable, int numbersToFind) {
        this.textArea = appendable;
        this.numbersToFind = numbersToFind;
        generator = new PrimeNumberGenerator();
        numFoundSoFar = 0;
    }

    @Override
    public Integer doInBackground() {
        while (numFoundSoFar < numbersToFind && ! isCancelled()) {
            long number = generator.getNextPrimeNumber();
            numFoundSoFar ++;
            publish((int)number);
            setProgress(100 * numFoundSoFar / numbersToFind);
        }

        return numFoundSoFar;
    }

    @Override
    protected void process(List<Integer> chunks) {
        StringBuilder bldr = new StringBuilder();

        for (int number : chunks) {
            bldr.append(number).append('\n');
        }
        textArea.append(bldr.toString());
    }
}


