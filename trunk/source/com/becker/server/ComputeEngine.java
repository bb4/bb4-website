package com.becker.server;

import com.becker.common.*;

import java.rmi.*;
import java.rmi.server.*;
import java.io.IOException;

public class ComputeEngine extends UnicastRemoteObject
                           implements Compute
{
    //private static final int PORT = 2020;
    //private static final String NAME = "rmi://becker-hm2:"+ PORT +"/ComputeEngine";
    private int port_ = 2020;
    private String hostname_ = "becker-hm2";
    private static final String CLASSPATH = Util.PROJECT_DIR + "rmi_server";

    public ComputeEngine() throws RemoteException {
        super();
    }

    public Object executeTask(Task t) {
        return t.execute();
    }

    private static void startHTTPServer(int port, String classpath)
    {
        try {
            // we have to use a different pot than the rmiregistry uses or else we get an exception
            // stating that that port is already in use.
            new ClassFileServer(port, classpath);
            System.out.println( "ClassFileServer started on port "+port );
        }
        catch (IOException ioe)    {
            System.out.println( "Unable to create ClassFileServer for port "+port+ " and classpath "+classpath);
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String name = "rmi://"+hostname+":"+ port +"/ComputeEngine";

        /*
         *  start registry
         */
        try {
           java.rmi.registry.LocateRegistry.createRegistry(port);
           System.out.println( "RMI registry successfully started on port "+port );
        }
        catch (RemoteException e) {
            System.err.println("Error running rmiregistry: " + e.getMessage());
            e.printStackTrace();
        }

        /*
         * start the httpServer needed by RMI to ship classes over the wire
         */
        startHTTPServer(port+1, CLASSPATH);

        try {
            Compute engine = new ComputeEngine();
            Naming.rebind(name, engine);
            System.out.println("ComputeEngine bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
