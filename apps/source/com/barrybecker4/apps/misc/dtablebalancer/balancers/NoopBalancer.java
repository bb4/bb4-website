// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer.balancers;

import com.barrybecker4.apps.misc.dtablebalancer.Table;

/**
 * The approach used here is to set the row or column height/width to the average of the mean and max values normalized
 * by the overall width/height.
 *
 * @author Barry Becker
 */
public class NoopBalancer implements Balancer {

    @Override
    public void doBalancing(Table table) {
        // intentionally do nothing
    }
}
