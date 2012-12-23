// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online.server.connection;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.List;


/**
 * A client worker is created for each client player connection to this server.
 */
class UpdateWorker implements Runnable {

    private ObjectInputStream inputStream;
    private List<OnlineChangeListener> changeListeners;
    private volatile boolean isConnected = true;

    UpdateWorker(ObjectInputStream input, List<OnlineChangeListener> changeListeners) {
        this.inputStream = input;
        this.changeListeners = changeListeners;
    }

    public void run() {

        while (isConnected) {
            try {
                processNextCommand();
            }
            catch (SocketException e) {
                exceptionOccurred("Read failed (probably because player closed client).", e);
            }
            catch (IOException e) {
                exceptionOccurred("Read failed.", e);
            }
            catch (ClassNotFoundException e) {
                exceptionOccurred("Class not found.", e);
            }
        }
        GameContext.log(0, "UpdateWorker terminated.");
    }

    private void processNextCommand() throws IOException, ClassNotFoundException {
        GameCommand cmd = (GameCommand) inputStream.readObject();
        GameContext.log(1, "Connection: got an update of the table from the server:\n" + cmd);

        for (OnlineChangeListener aChangeListeners_ : changeListeners) {
            aChangeListeners_.handleServerUpdate(cmd);
        }
    }

    private void exceptionOccurred(String msg, Throwable e) {
         GameContext.log(0, msg);
         //e.printStackTrace();
         isConnected = false;
    }

    @Override
    protected void finalize() {
        try {
           super.finalize();
           inputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
