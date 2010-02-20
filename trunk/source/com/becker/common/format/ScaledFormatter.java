package com.becker.common.format;

import com.becker.common.util.Util;

/**
 * @author Barry Becker
 */
public class ScaledFormatter implements INumberFormatter {

    private double scale_ = 1.0;
    private boolean exponentialScale_ = false;
    private boolean isCurrency_ = false;

    public ScaledFormatter(double scale, boolean exponentialScale, boolean isCurrency) {
        scale_ = scale;
        exponentialScale_ = exponentialScale;
        isCurrency_ = isCurrency;
    }

    public String format(double number) {

        double scaledNumber = scale_ * number;
        if (exponentialScale_)
            scaledNumber = Math.pow(10, scaledNumber);
        String formattedNumber = Util.formatNumber(scaledNumber);
        return isCurrency_ ? "$" + formattedNumber : formattedNumber;
    }

    public static void main(String[] args) {
        ScaledFormatter fmtr = new ScaledFormatter(0.1, true, true);

        for (double value = 50.0; value < 1000.0; value*=1.1) {
            System.out.println(value + " formatted = " + fmtr.format(value));
        }

    }
}