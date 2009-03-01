package com.becker.apps.misc.brian;



/**
 * Brian's First Program.
 */

public final class FirstProgram
{



    public static void main( String[] args )
    {

        int count = 1;

        do {


            System.out.println("Brian ("+count+")  ");

            count = count + 1;

        }  while (count <= 10000);

        System.out.println("DONE!");

    }
}
