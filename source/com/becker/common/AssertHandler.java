package com.becker.common;

/**
 * implement this interface if you want to do something when an assertion fails.
 * @see com.becker.common.Assert addHandler
 *
 * @author Barry Becker
 */

public interface AssertHandler
{
    public void assertFailed( RuntimeException rte );
}