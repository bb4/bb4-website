// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.socket;

import com.barrybecker4.ui.components.Appendable;
import com.barrybecker4.ui.components.ScrollingTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class ClientWorker implements Runnable {
    private Socket client_;
    private Appendable text_;

    ClientWorker(Socket client, Appendable text) {
        this.client_ = client;
        this.text_ = text;
    }

    public void run() {
        String line;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(client_.getInputStream()));
            out = new PrintWriter(client_.getOutputStream(), true);
        }
        catch (IOException e) {
            System.out.println("in or out failed");
            e.printStackTrace();
            System.exit(-1);
        }

        while (true) {
            try {
                line = in.readLine();
                 //Send data back to client
                out.println("RECIEVED:" + line);
                text_.append(line + '\n');
            }
            catch (IOException e) {
                System.out.println("Read failed");
                e.printStackTrace();
                break;
            }
        }
    }
}