// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer.balancers;

import com.barrybecker4.apps.misc.dtablebalancer.Table;

/**
 * @author Barry Becker
 */
public interface Balancer {

    void doBalancing(Table balance);
}
