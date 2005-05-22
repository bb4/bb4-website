package com.becker.misc.brian;



/**
 * Brian's First Program
 */

public final class FirstProgram
{



    public static void main( String[] args )
    {

        int count = 1;

        do {


            System.out.print("Brian ("+count+")  ");

            count = count + 1;

            if (count % 25 == 0) {
                System.out.println();
            }


        }  while (count <= 10000);

        System.out.println("DONE!");

    }
}
