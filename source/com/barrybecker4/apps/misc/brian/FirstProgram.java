/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.brian;

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
