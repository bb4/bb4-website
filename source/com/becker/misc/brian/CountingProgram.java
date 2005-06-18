package com.becker.misc.brian;

import com.becker.common.Util;


/**
 * Brian's Second Program
 */

public final class CountingProgram  {



    public static void main( String[] args ) {

        int count = 1;




        long t = System.currentTimeMillis();

        do {

            System.out.println("The square root of "+ count +" = "+ Util.formatNumber(Math.sqrt(count)));

            count = count + 1;


        }  while (count <= 1000);

        System.out.println("Time =" + ((System.currentTimeMillis() - t)/1000));

        System.out.println("DONE!");


    }
}
