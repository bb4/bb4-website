package com.becker.game.twoplayer.go;

import com.becker.common.util.FileUtil;
import com.becker.game.common.*;
import java.io.*;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.igoweb.kgs.client.gtp.GtpClient;
import org.igoweb.kgs.client.gtp.Options;



/**
 * Use this class to connect to KGS using lib/kgsGtp.jar and have my Tesujisoft go engine play on KGS.
 * kgsGtp is a java program that connects Go playing programs to the KGS Go server.
 * http://senseis.xmp.net/?KgsGtp
 * It uses GTP to communicate with your engine, giving commands like "play w cd5" and receives the engine's responses.
 *
 *  @author Barry Becker
 */
public class GtpKgsTesujisoftGoClient
{

    public static void main(String[] args)
    {
        try
        {
            String logFile = FileUtil.getHomeDir() + "/temp/" + "log.txt";
            
            File file = new File(logFile);
            PrintStream log = new PrintStream(new FileOutputStream(file));
           log.println("log=" + logFile);

            final GtpTesujisoftGoServer gtpTSGoServer = new GtpTesujisoftGoServer(System.in, System.out, log);

            Properties props = new Properties();
            FileInputStream inStream = new FileInputStream(FileUtil.getHomeDir()
                    + "/source/" +  GameContext.GAME_ROOT + "twoplayer/go/resources/tesujiBot.properties");
            props.load(inStream);
            log.println("props=" + props.toString());
            inStream.close();

            log.println("step 0");
            SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                     {
                        try {
                            gtpTSGoServer.mainLoop();
                        } catch (IOException ex) {
                            Logger.getLogger(GtpKgsTesujisoftGoClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            });

            Options kgsOptions = new Options(props, logFile);
            log.println("step 1");
            GtpClient gtpClient = new GtpClient(System.in, System.out, kgsOptions);            

            log.println("step 2");
            gtpClient.go();            
            log.println("step 3");

            log.close();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(-1);
        }
    }
}

