package com.becker.game.common.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * This is the application frame wrapper for the game programs.
 * It contains a GamePanel corresponding to the game you have selected to play.
 * If you specify a game class as an argument, then you do not get a menu of all possible games to play.
 *
 * @see com.becker.game.common.ui.GamePanel
 * @author Barry Becker
 */
public class GameApp implements ActionListener
{

    private GamePanel gamePanel_ = null;
    private JFrame frame_ = null;

    private JMenuItem openItem_;
    private JMenuItem saveItem_;
    private JMenuItem saveImageItem_;
    private JMenuItem exitItem_;


    static {
        if (GameContext.getUseSound()) {
            GameContext.log(3, "GameApp static init." );
            GUIUtil.setStandAlone((GUIUtil.getBasicService() != null));
        }
    }

    /**
     * Game application constructor
     */
    private GameApp()
    {
        GUIUtil.setCustomLookAndFeel();
        
        frame_ = new JFrame();
        frame_.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame_.setBounds(200, 200, 600, 500);
        // display the frame
        frame_.setVisible(true);
        frame_.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }


    /**
     * Show the game panel for the specifued game
     * @param gameName name of the game to show in the frame.
     */
    private void showGame(String gameName)
    {
        System.out.println("*** About to get plugin for "+gameName);
        String className = PluginManager.getInstance().getPlugin(gameName).getPanelClass();
        Class gameClass = Util.loadClass(className);

        // this will load the resources for the specified game.
        GameContext.loadGameResources(gameName);

        if (gamePanel_ != null) {
            frame_.getContentPane().remove(gamePanel_);
        }

        try {
            gamePanel_ = (GamePanel)gameClass.newInstance();
            gamePanel_.init(frame_);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        frame_.getContentPane().add(gamePanel_);
        frame_.setTitle(gamePanel_.getTitle());
        frame_.setVisible(true);
    }

    /**
     * Add a top level menu to allow changing to a different game from the one currently displayed.
     */
    private void addMenuBar()
    {
        JMenu fileMenu = new JMenu(GameContext.getLabel("FILE"));
        JMenu gameMenu= new JMenu(GameContext.getLabel("GAME"));
        fileMenu.setBorder(BorderFactory.createEtchedBorder());
        gameMenu.setBorder(BorderFactory.createEtchedBorder());

        openItem_ =  createMenuItem(GameContext.getLabel("OPEN"));
        saveItem_ =  createMenuItem(GameContext.getLabel("SAVE"));
        saveImageItem_ =  createMenuItem(GameContext.getLabel("SAVE_IMAGE"));
        exitItem_ = createMenuItem("Exit");
        fileMenu.add(openItem_);
        fileMenu.add(saveItem_);
        fileMenu.add(saveImageItem_);
        fileMenu.add(exitItem_);

        Iterator pluginIt = PluginManager.getInstance().getPlugins().iterator();

        while (pluginIt.hasNext()) {
            GamePlugin p = (GamePlugin) pluginIt.next();
            String gameNameLabel = (String)(p.getLabel());
            gameMenu.add(createMenuItem(gameNameLabel));
        }

        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        menubar.add(gameMenu);

        frame_.getRootPane().setJMenuBar(menubar);
    }


    private JMenuItem createMenuItem(String name)
    {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }

    /**
     * called when the user has selected a different game to play from the game menu
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {
        JMenuItem item = (JMenuItem) e.getSource();
        if (item == openItem_)  {
            gamePanel_.openGame();
        }
        else if (item == saveItem_) {
            gamePanel_.saveGame();
        }
        else if (item == saveImageItem_) {
            GUIUtil.saveSnapshot(gamePanel_, GameContext.getHomeDir());
        }
        else if (item == exitItem_) {
            System.exit(0);
        }
        else {
            showGame(PluginManager.getInstance().getPluginFromLabel(item.getText()).getName());
        }
    }

    /**
     * Static method to start up the game playing application.
     * The arguments allowed are :
     *  gameName : one of the supported games (eg "go", "checkers", "pente", etc).
     *      If unspecified, the default is DEFAULT_GAME.
     *  locale : The locale (language) to run in. If unspecified, the locale will be "ENGLISH".
     *
     * @param args optionally the game to play and or the locale
     */
    public static void main(String[] args) {

        // do webstart check and set appropriately
        GUIUtil.setStandAlone((GUIUtil.getBasicService() != null));

        
        String defaultGame = PluginManager.getInstance().getDefaultPlugin().getName();
        String gameName;
        if (args.length == 0) {
            gameName = defaultGame;
        }
        else if (args.length == 1) {
            // if there is only one arg assume it is the name of the game
            gameName = args[0];
        }
        else {
            CommandLineOptions options = new CommandLineOptions(args);

            if (options.contains("help")) {
                System.out.println("Usage: -game <game> [-locale <locale>]");
            }
            // create a game panel of the appropriate type based on the name of the class passed in.
            // if no game is specified as an argument, then we show a menu for selecting a game
            gameName = options.getValueForOption("game", defaultGame);
            
            if (options.contains("locale")) {
                // then a locale has been specified
                String localeName = options.getValueForOption("locale", "ENGLISH");
                LocaleType locale = GameContext.getLocale(localeName, true);
                GameContext.setLocale(locale);
            }
        }

        GameApp gameApp = new GameApp();

        gameApp.addMenuBar();
        gameApp.showGame(gameName);
    }

}
