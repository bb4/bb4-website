package com.becker.common.format;

/**
 * @author Barry Becker
 */
public class CurrencyFormatter implements INumberFormatter {

    public CurrencyFormatter() {
    }

    public String format(double number) {
        String formattedNumber = FormatUtil.formatNumber(number);
        return  "$" + formattedNumber;
    }

    
    /** for testing */
    public static void main(String[] args) {
        CurrencyFormatter fmtr = new CurrencyFormatter();

        for (double value = 50.0; value < 1000.0; value*=1.1) {
            System.out.println(value + " formatted = " + fmtr.format(value));
        }
    }
}