/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.brian;

import com.barrybecker4.common.format.FormatUtil;


/**
 * Brian's Second Program
 */

public final class SecondProgram  {

    private SecondProgram() {}



    public static void main( String[] args ) {

        int count = 1;

        long t = System.currentTimeMillis();

        do {


            String sqrt =  FormatUtil.formatNumber(Math.sqrt(count));
            //String square = FormatUtil.formatNumber(count * count);
            //String power2 = FormatUtil.formatNumber(Math.pow(2, count));

            System.out.println("The square root of "+ count +" = "+ sqrt);
            //                   " \t   while " + count + " * " + count +" = " + square +
            //                   " \t   and 2 ^ "+count +" = "+ power2);

            count = count + 1;


        }  while (count <= 1000);

        double numSeconds = (System.currentTimeMillis() - t) / 1000.0;
        System.out.println("Time =" + numSeconds);

        System.out.println("DONE!");

    }
}
