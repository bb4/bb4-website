package com.becker.game.common;

import com.becker.sound.MusicMaker;
import com.becker.ui.Log;

import javax.swing.*;
import java.util.*;
import java.lang.reflect.Array;

/**
 * Manage game context info such as logging, debugging, resources, and profiling.
 * @@ we could also use this class to manage cofiguration information from a config file.
 * @@ Move more things here. Pehaps use java properties?
 *
 * @author Barry Becker
 */
public final class GameContext
{
    private static Set commonMessageKeys_;

    // logger object
    private static Log logger_ = null;

    // use sound effects if true
    private static boolean useSound_ = false;

    // this is a singleton. It generates the sounds
    private static MusicMaker musicMaker_ = null;

    private static final String MESSAGE_2PLAYER_BUNDLE_PREFIX = "com.becker.game.twoplayer.";
    private static final String MESSAGE_NPLAYER_BUNDLE_PREFIX = "com.becker.game.multiplayer.";
    private static final String COMMON_MESSAGE_BUNDLE = "com.becker.game.common.resources.coreMessages";
    private static ResourceBundle commonMessages_;
    private static ResourceBundle gameMessages_;

    private static final LocaleType DEFAULT_LOCALE = LocaleType.ENGLISH;
    private static LocaleType currentLocale_ = DEFAULT_LOCALE;

    static {

        GameContext.log(1, "initing sound." );


        if ( useSound_ ) {
            GameContext.getMusicMaker().stopAllSounds();
            GameContext.getMusicMaker().startNote( MusicMaker.SEASHORE, 40, 2, 3 );
        }
    }

    public static final String GAME_ROOT = "com/becker/game/";

    // if greater than 0 then debug mode is on.
    // the higher the number, the more info that is printed.
    private static final int DEBUG = 0;

    // now the variable forms of the above defaults
    private static int debug_ = DEBUG;

    // if true then profiling performance statistics will be printed to the console while running.
    private static final boolean PROFILING = false;
    private static boolean profiling_ = PROFILING;

    // fall back on this if the "user.home" property is not set.
    private static final String DEFAULT_HOME_DIR = "d:/";




    /**
     * @return the level of debugging in effect
     */
    public static int getDebugMode()
    {
        return debug_;
    }

    /**
     * @param debug
     */
    public static void setDebugMode( int debug )
    {
        debug_ = debug;
    }

    /**
     * @return true if profiling stats are being shown after every move
     */
    public static boolean isProfiling()
    {
        return profiling_;
    }

    /**
     * @param prof whether or not to turn on profiling
     */
    public static void setProfiling( boolean prof )
    {
        profiling_ = prof;
    }


    /**
     * @param logger the logging device. Determines where the output goes.
     */
    public static void setLogger( Log logger )
    {
        logger_ = logger;
    }

    /**
     * @return the logging device to use.
     */
    public static Log getLogger()
    {
        return logger_;
    }

    /**
     * log a message using the internal logger object
     */
    public static void log( int logLevel, String message )
    {
        if ( logger_ != null )
            logger_.println( logLevel, getDebugMode(), message );
    }

    /**
     * @param useSound if true, then sound effects will be used when moving
     */
    public static void setUseSound( boolean useSound )
    {
        if ( useSound_ )
            GameContext.getMusicMaker().stopAllSounds();
        useSound_ = useSound;
    }

    /**
     * @return  true if sound is not turned off.
     */
    public static boolean getUseSound()
    {
        return useSound_;
    }

    /**
     * @return the sound to make after each move
     */
    public static String getPreferredTone()
    {
        //return MusicMaker.TAIKO_DRUM;
        return MusicMaker.METALLIC_SNARE;
        //return MusicMaker.DROPS;
    }

    /**
     * @return use this to add cute sound effects.
     */
    public static MusicMaker getMusicMaker()
    {
        if ( musicMaker_ == null ) {
            musicMaker_ = new MusicMaker();
        }
        return musicMaker_;
    }

    /**
     * @return home directory. Assumes running as an pp.
     */
    public static String getHomeDir()
    {
        String home =  System.getProperty("user.home");
        if (home == null)
            home = DEFAULT_HOME_DIR;
        GameContext.log(1, "home = "+home );
        return home;
    }



    public static LocaleType getDefaultLocaleType()
    {
        return DEFAULT_LOCALE;
    }

    private static String gameName_, className_;
    /**
     * This method causes the appropriate message bundle to
     * be loaded for the game specified.
     * @param gameName the current game
     */
    public static void loadGameResources(String gameName, String className)
    {
        // @@ why do I need this hack?
        gameName_ = gameName;
        className_ = className;

        String path = className.substring(0, className.lastIndexOf(".ui."));

        String resourcePath = path +".resources."+gameName+"Messages";
        System.out.println("searching for "+ resourcePath);

        try {
            gameMessages_ = ResourceBundle.getBundle(
                    resourcePath, currentLocale_.getLocale());
        }
        catch (MissingResourceException e) {
               System.out.println("could not find "+resourcePath);
        }
    }

    public static void loadGameResources() {
        loadGameResources(gameName_, className_);
    }

    /**
     * set the current locale and load the cutpoints for it.
     * @param locale
     */
    public static void setLocale(LocaleType locale)
    {
        currentLocale_ = locale;
        gameMessages_ = null;
        initCommonMessages(currentLocale_);
        JComponent.setDefaultLocale(currentLocale_.getLocale());
    }

    public static Locale getLocale() {
        return currentLocale_.getLocale();
    }

    /**
     * @param key
     * @return  the localized message label
     */
    public static String getLabel(String key)
    {
        if (commonMessages_ == null)  {
            initCommonMessages(currentLocale_);
        }
        if (commonMessageKeys_.contains(key))  {
            return commonMessages_.getString(key);
        }
        else {

            //@@ hack!
            if (gameMessages_ == null) loadGameResources();

            assert (gameMessages_ != null) :
                    "gameMessages_ has not yet been initialized. you need to call loadGameResources first.";

            String label = key; // default
            try {
               label = gameMessages_.getString(key);
            }
            catch (MissingResourceException e) {
               GameContext.log(0,  e.getMessage() );
            }
            return label;
        }
    }

    private static void initCommonMessages(LocaleType locale)
    {
        // load the common resources at startup
        commonMessages_ =
            ResourceBundle.getBundle(COMMON_MESSAGE_BUNDLE, locale.getLocale());
        commonMessageKeys_ = new HashSet();
        Enumeration enumXXX = commonMessages_.getKeys();
        while (enumXXX.hasMoreElements()) {
            commonMessageKeys_.add(enumXXX.nextElement());
        }
        JComponent.setDefaultLocale(locale.getLocale());
    }

    /**
     * Iterate through all the message keys in the message
     * bundles for all locales and verify that they all have the
     * same keys. If any are missing for a given locale they need to be added.
     * @@ currently we only check the common common bundle, but we should do all.
     */
    private static void verifyConsistentMessageBundles()
    {
        GameContext.log(1,"verifying consistency of message bundles... ");
        // an array of hashSets of the keys for each bundle
        ArrayList messageKeySets = new ArrayList();
        LocaleType[] locales = LocaleType.values();
        for (int i=0; i<locales.length; i++) {
            ResourceBundle bundle = ResourceBundle.getBundle(COMMON_MESSAGE_BUNDLE,
                                        locales[i].getLocale());
            HashSet keySet = new HashSet();
            Enumeration enumXXX = bundle.getKeys();
            while (enumXXX.hasMoreElements()) {
                String key = (String)enumXXX.nextElement();
                //System.out.println(locales.getValue(i).getName()+" "+key);
                keySet.add(key);
            }
            messageKeySets.add(keySet);
            GameContext.log(1, "keySet size for "+(locales[i]).getLocale() +"="+keySet.size());
        }
        // now that we have the keysets report on their consistency.
        // assume that the first is the default (en)
        boolean allConsistent = true;
        HashSet defaultKeySet = (HashSet)messageKeySets.get(0);
        // first check that all the non-default locales do not contain keys
        // that the default locale does not have (less common).
        for (int i=1; i<locales.length; i++) {
            HashSet keySet = (HashSet)messageKeySets.get(i);
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                if (!defaultKeySet.contains(key)) {
                    GameContext.log(0, COMMON_MESSAGE_BUNDLE+" for locale "+locales[i]
                            +" contains the key, "+key+", that is not in the default locale (en).");
                    allConsistent = false;
                }
            }
        }
        // now check that the default does not have keys not found in the
        // non-default locales (more common case).
        // @@ Actually this doesn't really work because when you do a getKeys on
        // a locale it also returns keys that are in the default locale, but not
        // the specific locale. I guess this is so you have a default to fall
        // back on, but it will make it harder to do consistency checking on the
        // bundles.
        Iterator it = defaultKeySet.iterator();
        while (it.hasNext())  {
            String key = (String)it.next();
            for (int i=1; i<locales.length; i++) {
                HashSet keySet = (HashSet)messageKeySets.get(i);
                if (!keySet.contains(key)) {
                    GameContext.log(0, COMMON_MESSAGE_BUNDLE+" for locale "+locales[i]
                            +" does not contain the key "+key);
                    allConsistent = false;
                }
            }
        }
        if (allConsistent)
            GameContext.log(0, "The bundles for all the locales are consistent.");
        else
            GameContext.log(0, "Inconsistent bundles. Please correct the above items.");
    }

    public static void main(String[] args)
    {
        verifyConsistentMessageBundles();
    }

    /**
     * Looks up an {@link LocaleType}
     * @throws Error if the name is not a member of the enumeration
     */
    public static LocaleType get(final String name, final boolean finf) {
        LocaleType type = LocaleType.ENGLISH;  // the default

        try {
            type = LocaleType.valueOf(name);
        }
        catch (Error e) {
            log(0,  "***************" );
            log(0, name +" is not a valid locale. We currently only support: ");
            LocaleType[] values = LocaleType.values();
            for (int i=0; i<values.length; i++)
                log(0, values[i].toString() );
            log(0,  "Defaulting to English." );
            log(0, "***************" );
        }
        return type;
    }
}
