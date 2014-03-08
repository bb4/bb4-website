package com.barrybecker4.apps.misc.brian.cs2014projects;

import java.util.Scanner;

/**
 * @author Period 5
 */
public class CandP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner kbd = new Scanner(System.in);

        System.out.println("how many choices do you have to choose from:");

        int choices = kbd.nextInt();

        System.out.println("how many choices can you choose:");

        int selection = kbd.nextInt();


        System.out.println("P = " + choices + "!/(" + choices + "-" + selection + ")!");

        System.out.println("C = " + choices + "!/(" + selection + "!)("
                + choices + "-" + selection + ")!");
    }
}