// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.givechange;

import java.util.HashMap;

/**
 * @author Barry Becker
 */
public class CoinMap extends HashMap<Coin, Integer> {

    public void add(Coin coin, int number) {
        if (containsKey(coin)) {
            put(coin, get(coin) + number);
        }
        else {
            put(coin, number);
        }
    }

    public void remove(Coin coin, int number) {
         assert this.get(coin) >= number;
         put(coin, get(coin) - number);
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        for (int i = Coin.values().length - 1; i >= 0; i--) {
            Coin coin = Coin.values()[i];
            if (containsKey(coin)) {
                int num = get(coin);
                if (num > 0) {
                    bldr.append(num)
                        .append(" ")
                        .append(num == 1 ? coin.getName() : coin.getPluralName() )
                        .append("   ");
                }
            }
        }
        return bldr.toString();
    }
}
