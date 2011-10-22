package com.becker.game.common;

import com.becker.common.ILog;
import com.becker.common.i18n.LocaleType;
import com.becker.common.i18n.MessageContext;
import com.becker.game.common.plugin.GamePlugin;
import com.becker.game.common.plugin.PluginManager;
import com.becker.sound.MusicMaker;
import com.becker.ui.util.Log;
import java.util.Random;

/**
 * Manage game context info such as logging, debugging, resources, and profiling.
 * @@ Perhaps use java properties or config file?
 *
 * @author Barry Becker
 */
public final class GameContext {

    /** logger object. Use console by default. */
    private static ILog logger_ = new Log();

    /**
     * Use sound effects if true.
     * Probably need to turn this off when deploying in applet form to avoid security errors.
     */
    private static final boolean useSound_ = false;

    /** this is a singleton. It generates the sounds. */
    private static MusicMaker musicMaker_ = null;

    /** Make sure that the program runs in a reproducible way by always starting from the same random seed. */
    private static Random RANDOM = new Random(0);


    static {
        log(1, "initing sound." );

        if ( useSound_ ) {
            getMusicMaker().stopAllSounds();
            getMusicMaker().startNote( MusicMaker.SEASHORE, 40, 2, 3 );
        }
    }

    public static final String GAME_ROOT = "com/becker/game/";

    /** if greater than 0, then debug mode is on. the higher the number, the more info that is printed.  */
    private static final int DEBUG = 0;

    /** now the variable forms of the above defaults */
    private static int debug_ = DEBUG;

    /** if true, then profiling performance statistics will be printed to the console while running.  */
    private static final boolean PROFILING = false;
    private static boolean profiling_ = PROFILING;

    private static final String COMMON_MESSAGE_BUNDLE = "com.becker.game.common.resources.coreMessages";
    private static MessageContext messageContext_ = new MessageContext(COMMON_MESSAGE_BUNDLE);


    /** private constructor for singleton. */
    private GameContext() {}

    /**
     * @return the level of debugging in effect
     */
    public static int getDebugMode() {
        return debug_;
    }

    /**
     * @param debug
     */
    public static void setDebugMode( int debug ) {
        debug_ = debug;
    }

    /**
     * @return true if profiling stats are being shown after every move
     */
    public static boolean isProfiling() {
        return profiling_;
    }

    /**
     * @param prof whether or not to turn on profiling
     */
    public static void setProfiling( boolean prof ) {
        profiling_ = prof;
    }


    /**
     * @param logger the logging device. Determines where the output goes.
     */
    public static void setLogger( ILog logger ) {
        assert logger != null;
        logger_ = logger;
    }

    /**
     * @return the logging device to use.
     */
    public static ILog getLogger() {
        return logger_;
    }

    /**
     * log a message using the internal logger object
     */
    public static void log( int logLevel, String message ) {
            logger_.print( logLevel, getDebugMode(), message );
    }

    /**
     * @return  true if sound is not turned off.
     */
    public static boolean getUseSound() {
        return useSound_;
    }

    /**
     * @return use this to add cute sound effects.
     */
    public static synchronized MusicMaker getMusicMaker() {
        if ( musicMaker_ == null ) {
            musicMaker_ = new MusicMaker();
        }
        return musicMaker_;
    }


    /**
     * This method causes the appropriate message bundle to
     * be loaded for the game specified.
     * @param gameName the current game
     */
    public static void loadGameResources(String gameName) {
        log(1, "loadGameResources gameName=" + gameName);
        GamePlugin plugin = PluginManager.getInstance().getPlugin(gameName);
        log(1, "plugin = " + plugin);
        log(2, "gameName=" + gameName + " plugin=" + plugin);
        String resourcePath = plugin.getMsgBundleBase();
        log(2, "searching for "+ resourcePath);

        messageContext_.setLogger(logger_);
        messageContext_.setDebugMode(debug_);
        messageContext_.setApplicationResourcePath(resourcePath);
    }

    /**
     * set the current locale and load the cutpoints for it.
     * @param locale
     */
    public static void setLocale(LocaleType locale) {
        messageContext_.setLocale(locale);
    }

    /**
     * @param key
     * @return  the localized message label
     */
    public static String getLabel(String key) {
        return messageContext_.getLabel(key);
    }

    /**
     * Looks up an {@link LocaleType} for a given locale name.
     * @param name name of the locale to get localeType for
     * @param finf fail if not found.
     * @return locale
     * @throws Error if the name is not a member of the enumeration
     */
    public static LocaleType getLocale(String name, boolean finf) {

        return messageContext_.getLocale(name, finf);
    }

    public static Random random() {
        return RANDOM;
    }

    public static void setRandomSeed(int seed) {
        RANDOM = new Random(seed);
    }
}
