package com.barrybecker4.apps.misc.brian.cs2014projects;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Period 3
 */
public class Prime {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ArrayList<Integer> factors = new ArrayList<Integer>();
        Scanner kbd = new Scanner(System.in);
        System.out.println("Find the prime factorization of what number24?");
        int number = kbd.nextInt();

        for (int i = 2; i <= number; i++) {
            while (number % i == 0) {
                factors.add(i);
                number /= i;
            }
        }

        System.out.println( "prime factorization = " + factors);
    }

}