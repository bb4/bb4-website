// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.waxomatic;

abstract class WaxTask implements Runnable {

    protected Car car;

    WaxTask(Car c) { car = c; }

}