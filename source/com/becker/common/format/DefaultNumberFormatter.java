package com.becker.common.format;

/**
 * @author Barry Becker
 */
public class DefaultNumberFormatter implements INumberFormatter {

    public String format(double number) {
        return FormatUtil.formatNumber(number);
    }
}
