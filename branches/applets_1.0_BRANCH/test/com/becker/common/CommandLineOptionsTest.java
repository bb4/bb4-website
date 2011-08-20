package com.becker.common;


import com.becker.common.CommandLineOptions;
import junit.framework.TestCase;

import java.util.Map;

/**
 * @author Barry Becker
 */
public class CommandLineOptionsTest extends TestCase {

    public void testCommandLineOptionsToString() {

        String[] testArgs = {"-a", "b", "-c", "dog", "-e", "-f", "-type", "foo", "-h"};
        CommandLineOptions options = new CommandLineOptions(testArgs);
        assertEquals("unexpected", "{f=null, e=null, c=dog, a=b, type=foo, h=null}", options.toString());
    }
}