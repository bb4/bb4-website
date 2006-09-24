package com.becker.rmi.client;

import com.becker.common.*;

import java.math.*;
import java.rmi.*;
import java.net.*;

/**
 * This client side rmi program to compute Pi to n digits accepts 3 arguments
 * arg[0] - hostname on which the rmi server runs
 * arg[1] - port to connect to on that server.
 * arg[2] - n the number of digits to compute Pi to.
 */
public class ComputePi {

    private ComputePi() {};

    private static void handleException(Exception e) {
         System.err.println("ComputePi exception: " + e.getMessage());
            e.printStackTrace();
    }

    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        try {
            String hostname = args[0];
            int port = Integer.parseInt(args[1]);
            String name = "//" + hostname + ':' + port + "/ComputeEngine";
            Compute comp = (Compute) Naming.lookup(name);
            int numDigits = Integer.parseInt(args[2]);
            Pi task = new Pi( numDigits );
            BigDecimal pi = (BigDecimal) (comp.executeTask( task ));
            System.out.println("Here is Pi computed to "+numDigits+" digits of precision"+pi);
        } catch (NumberFormatException e) {
            handleException(e);
        } catch (NotBoundException e) {
            handleException(e);
        } catch (MalformedURLException e) {
            handleException(e);
        } catch (RemoteException e) {
           handleException(e);
        }

    }
}
