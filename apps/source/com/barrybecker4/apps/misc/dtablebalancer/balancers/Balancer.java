// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer.balancers;

import com.barrybecker4.apps.misc.dtablebalancer.Table;

/**
 * Thoughts on additional balancers
 *  - sqrt(mean)
 *  - sqrt(max)
 *  - sqrt(max + mean)/2)
 *  - iterate a couple of times to get better
 *
 *  In tests do each example crossed with each balancer
 *
 * @author Barry Becker
 */
public interface Balancer {

    void doBalancing(Table balance);
}
