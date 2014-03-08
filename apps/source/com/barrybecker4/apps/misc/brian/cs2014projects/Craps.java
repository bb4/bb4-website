package com.barrybecker4.apps.misc.brian.cs2014projects;

import java.util.Scanner;

/**
 * @author Period 3
 */
public class Craps {

    public static void main(String[] args) {

        Scanner kbd = new Scanner(System.in);
        int dice1 = 0;
        int dice2 = 0;
        int point;
        int bet;
        int bank = 2000;

        while (bank < 100000000) {
            System.out.println("how much would you like to bet");
            bet = kbd.nextInt();

            while (bet > bank) {
                System.out.println("you bet too much try again");
                bet = kbd.nextInt();
            }

            dice1 = Dice.Dice(6);
            dice2 = Dice.Dice(6);

            System.out.println(dice1 + dice2);

            if (dice1 + dice2 == 7 || dice1 + dice2 == 11) {
                System.out.println("you win $" + bet);
                bank = bank + bet;
            }

            if (dice1 + dice2 == 2 || dice1 + dice2 == 3 || dice1 + dice2 == 12) {
                System.out.println("YOU LOSE! $" + bet);
                bank = bank - bet;
            }

            point = dice1 + dice2;

            while (dice1 + dice2 != 7) {
                dice1 = Dice.Dice(6);
                dice2 = Dice.Dice(6);
                System.out.println(dice1 + dice2);
                if (dice1 + dice2 == point) {
                    System.out.println("you win " + bet);
                    bank = bank + bet;
                    break;
                }

                if (dice1 + dice2 == 7) {
                    System.out.println("YOU LOSE! " + bet + "$");
                    bank = bank - bet;
                    break;
                }
            }
            System.out.println("your balance is " + bank + "$");
        }

        System.out.print("you have too much money dont lose it gambling!");
    }
}