package com.becker.game.common.ui;

import com.becker.common.Util;
import com.becker.ui.GUIUtil;
import com.becker.game.common.GameContext;
import com.becker.game.common.LocaleType;
import com.becker.java2d.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * This is the application frame wrapper for the game programs.
 * It contains a GamePanel corresponding to the game you have selected to play.
 * If you specify a game class as an argument, then you do not get a menu of all possible games to play.
 *
 * @@ use plugin architecutre where a file gives the names of the games and their associated classes.
 *
 * @see com.becker.game.common.ui.GamePanel
 * @author Barry Becker
 */
public class GameApp implements ActionListener
{
    // The default game is Go.
    private static final String DEFAULT_GAME = "go";

    private GamePanel gamePanel_ = null;
    private JFrame frame_ = null;

    private JMenuItem openItem_;
    private JMenuItem saveItem_;
    private JMenuItem saveImageItem_;
    private JMenuItem exitItem_;


    // provide a mapping from games to implementing panel classes
    private Map<String,String> hmGameClasses_;
    // provide mapping from cutpoints to game.
    private Map<String,String> hmGames_;

    static {
        if (GameContext.getUseSound()) {
            GameContext.log(3, "GameApp static init." );
            GUIUtil.setStandAlone((GUIUtil.getBasicService() != null));
        }
    }

    /**
     * Game application constructor
     */
    public GameApp()
    {
        init(); // must call after setting locale on GameContext
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


    private static final String CHECKERS_LABEL = GameContext.getLabel("CHECKERS");
    private static final String CHESS_LABEL = GameContext.getLabel("CHESS");
    private static final String PENTE_LABEL = GameContext.getLabel("PENTE");
    private static final String BLOCKADE_LABEL = GameContext.getLabel("BLOCKADE");
    private static final String GO_LABEL = GameContext.getLabel("GO");
    private static final String GALACTIC_LABEL = GameContext.getLabel("GALACTIC");
    private static final String POKER_LABEL = GameContext.getLabel("POKER");
    private static final String SET_LABEL = GameContext.getLabel("SET");

    /**
     * must do this initialization after we know the locale
     * (in other words it cannot be done statically)
     * @@ this should really come from a plugin file so that you do not need to change code to add a new game.
     */
    private void init()
    {
        hmGameClasses_ = new HashMap<String,String>();
        hmGames_ = new LinkedHashMap<String,String>();

        // these should get initialized from a text file resource so someone
        // adding a new game does not need to modify this file.
        hmGameClasses_.put("checkers", "com.becker.game.twoplayer.checkers.ui.CheckersPanel");
        hmGameClasses_.put("chess", "com.becker.game.twoplayer.chess.ui.ChessPanel");
        hmGameClasses_.put("pente", "com.becker.game.twoplayer.pente.ui.PentePanel");
        hmGameClasses_.put("blockade", "com.becker.game.twoplayer.blockade.ui.BlockadePanel");
        hmGameClasses_.put("go", "com.becker.game.twoplayer.go.ui.GoPanel");
        hmGameClasses_.put("galactic", "com.becker.game.multiplayer.galactic.ui.GalacticPanel");
        hmGameClasses_.put("poker", "com.becker.game.multiplayer.poker.ui.PokerPanel");
        hmGameClasses_.put("set", "com.becker.game.multiplayer.set.ui.SetPanel");

        hmGames_.put(CHECKERS_LABEL, "checkers");
        hmGames_.put(CHESS_LABEL, "chess");
        hmGames_.put(PENTE_LABEL, "pente");
        hmGames_.put(BLOCKADE_LABEL, "blockade");
        hmGames_.put(GO_LABEL, "go");
        hmGames_.put(GALACTIC_LABEL, "galactic");
        hmGames_.put(POKER_LABEL, "poker");
        hmGames_.put(SET_LABEL, "set");
    }

    /**
     * Show the game panel for the specifued game
     * @param gameName name of the game to show in the frame.
     */
    private void showGame(String gameName)
    {
        String className = hmGameClasses_.get(gameName);
        Class gameClass = Util.loadClass(className);

        // this will load the resources for the specified game.
        GameContext.loadGameResources(gameName, className);

        if (gamePanel_ != null) {
            //assert (frame_!=null) : "frame was null";
            //assert (frame_.getContentPane()!=null): "contentpane was null";
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

        Iterator keyIt = hmGames_.keySet().iterator();
        while (keyIt.hasNext()) {
            String sGameNameLabel = (String)keyIt.next();
            gameMenu.add(createMenuItem(sGameNameLabel));
        }

        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        menubar.add(gameMenu);

        frame_.getRootPane().setJMenuBar(menubar);
    }


    private JMenuItem createMenuItem(String gameName)
    {
        JMenuItem item = new JMenuItem(gameName);
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
            ImageUtil.saveSnapshot(gamePanel_, GameContext.getHomeDir());
        }
        else if (item == exitItem_) {
            System.exit(0);
        }
        else {
            showGame( hmGames_.get(item.getText()));
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

        // create a game panel of the appropriate type based on the name of the class passed in.
        // if no game is specified as an argument, then we show a menu for selecting a game
        String gameName = (args.length>0)? args[0] : DEFAULT_GAME;

        if (args.length>1) {
            // then a locale has been specified
            String localeName = args[1];
            LocaleType locale = GameContext.getLocale(localeName, true);
            GameContext.setLocale(locale);
        }

        GameApp gameApp = new GameApp();

        gameApp.addMenuBar();
        gameApp.showGame(gameName);
    }

}
