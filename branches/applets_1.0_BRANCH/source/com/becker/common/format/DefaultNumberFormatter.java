package com.becker.common.format;

import com.becker.common.util.Util;

/**
 * @author Barry Becker
 */
public class DefaultNumberFormatter implements INumberFormatter {

    public String format(double number) {
        return Util.formatNumber(number);
    }
}
