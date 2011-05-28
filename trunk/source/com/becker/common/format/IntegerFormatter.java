package com.becker.common.format;

/**
 * @author Barry Becker
 */
public class IntegerFormatter implements INumberFormatter {

    public String format(double number) {
        return FormatUtil.formatNumber((int) number);
    }
}