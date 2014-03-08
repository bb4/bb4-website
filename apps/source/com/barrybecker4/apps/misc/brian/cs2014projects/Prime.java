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

        ArrayList<Integer> primes = new ArrayList<Integer>();
        Scanner kbd = new Scanner(System.in);
        int number = kbd.nextInt();

        for (int i = 2; i <= number; i++) {
            while (number % i == 0) {
                primes.add(i);
                number /= i;
            }
        }

        for (int j = 0; j < primes.size(); j++) {
            System.out.print(primes.get(j));
        }
    }

}