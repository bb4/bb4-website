package com.becker.game.common;

import com.becker.common.Util;
import com.becker.sound.MusicMaker;
import com.becker.ui.Log;

import javax.swing.*;
import java.util.*;

/**
 * Manage game context info such as logging, debugging, resources, and profiling.
 * @@ we could also use this class to manage configuration information from a config file.
 * @@ Move more things here. Pehaps use java properties?
 *
 * @author Barry Becker
 */
public final class GameContext
{
    private static Set<String> commonMessageKeys_ = new HashSet<String>();

    // logger object
    private static Log logger_ = null;

    // use sound effects if true
    private static boolean useSound_ = true;

    // this is a singleton. It generates the sounds
    private static MusicMaker musicMaker_ = null;

    private static final String COMMON_MESSAGE_BUNDLE = "com.becker.game.common.resources.coreMessages";
    private static ResourceBundle commonMessages_ = null;
    private static ResourceBundle gameMessages_ = null;

    private static final LocaleType DEFAULT_LOCALE = LocaleType.ENGLISH;
    private static LocaleType currentLocale_ = DEFAULT_LOCALE;

    static {

        log(1, "initing sound." );

        if ( useSound_ ) {
            getMusicMaker().stopAllSounds();
            getMusicMaker().startNote( MusicMaker.SEASHORE, 40, 2, 3 );
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


    private GameContext() {}

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
            getMusicMaker().stopAllSounds();
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
     * @return use this to add cute sound effects.
     */
    public static synchronized MusicMaker getMusicMaker()
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
        String userHome = Util.USER_HOME;   // System.getProperty("user.home")
        
        String home =  userHome + "/projects/java_projects/trunk";
        log(1, "home = " + home );
        return home;
    }



    public static LocaleType getDefaultLocaleType()
    {
        return DEFAULT_LOCALE;
    }

    private static String gameName_ = null ;

    /**
     * This method causes the appropriate message bundle to
     * be loaded for the game specified.
     * @param gameName the current game
     */
    public static void loadGameResources(String gameName)
    {
        gameName_ = gameName;
        //System.out.println("loadGameResources gameName="+ gameName);
        //System.out.println("plugin = " + PluginManager.getInstance().getPlugin(gameName));
        String resourcePath = PluginManager.getInstance().getPlugin(gameName).getMsgBundleBase();
        log(2, "searching for "+ resourcePath);

        try {
            gameMessages_ = ResourceBundle.getBundle(
                    resourcePath, currentLocale_.getLocale());
        }
        catch (MissingResourceException e) {
            System.out.println("could not find "+resourcePath);
            e.printStackTrace();
        }
    }

    public static void loadGameResources() {
        loadGameResources(gameName_);
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

            if (gameMessages_ == null) {
                loadGameResources();
            }

            String label = key; // default
            try {
               label = gameMessages_.getString(key);
            }
            catch (MissingResourceException e) {
               log(0,  e.getMessage() );
            }
            return label;
        }
    }

    private static void initCommonMessages(LocaleType locale)
    {
        // load the common resources at startup
        commonMessages_ =
            ResourceBundle.getBundle(COMMON_MESSAGE_BUNDLE, locale.getLocale());
        //commonMessageKeys_ =
        Enumeration enum1 = commonMessages_.getKeys();
        while (enum1.hasMoreElements()) {
            commonMessageKeys_.add((String)enum1.nextElement());
        }
        JComponent.setDefaultLocale(locale.getLocale());
    }

    /**
     * Iterate through all the message keys in the message
     * bundles for all locales and verify that they all have the
     * same keys. If any are missing for a given locale they need to be added.
     * @@ currently we only check the common common bundle, but we should do all.
     * @@ This could be moved to a JUnit test.
     */
    private static void verifyConsistentMessageBundles()
    {
        log(1,"verifying consistency of message bundles... ");
        // an array of hashSets of the keys for each bundle
        List<Set> messageKeySets = new ArrayList<Set>();
        LocaleType[] locales = LocaleType.values();
        for (final LocaleType newVar : locales) {
            ResourceBundle bundle = ResourceBundle.getBundle(COMMON_MESSAGE_BUNDLE,
                    newVar.getLocale());
            Set<String> keySet = new HashSet<String>();
            Enumeration enum1 = bundle.getKeys();
            while (enum1.hasMoreElements()) {
                String key = (String) enum1.nextElement();
                //System.out.println(locales.getValue(i).getName()+" "+key);
                keySet.add(key);
            }
            messageKeySets.add(keySet);
            log(1, "keySet size for " + (newVar).getLocale() + '=' + keySet.size());
        }
        // now that we have the keysets report on their consistency.
        // assume that the first is the default (en)
        boolean allConsistent = true;
        Set defaultKeySet = messageKeySets.get(0);
        // first check that all the non-default locales do not contain keys
        // that the default locale does not have (less common).
        for (int i=1; i<locales.length; i++) {
            Set keySet = messageKeySets.get(i);
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                if (!defaultKeySet.contains(key)) {
                    log(0, COMMON_MESSAGE_BUNDLE+" for locale "+locales[i]
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
                Set keySet = messageKeySets.get(i);
                if (!keySet.contains(key)) {
                    log(0, COMMON_MESSAGE_BUNDLE+" for locale "+locales[i]
                            +" does not contain the key "+key);
                    allConsistent = false;
                }
            }
        }
        if (allConsistent)
            log(0, "The bundles for all the locales are consistent.");
        else
            log(0, "Inconsistent bundles. Please correct the above items.");
    }

    public static void main(String[] args)
    {
        verifyConsistentMessageBundles();
    }

    /**
     * Looks up an {@link LocaleType} for a given locale name.
     * @param finf fail if not found.
     * @throws Error if the name is not a member of the enumeration
     */
    public static LocaleType getLocale(String name, boolean finf) {
        LocaleType type; // english is the default
        try {
            type = LocaleType.valueOf(name);
        }
        catch (IllegalAccessError e) {
            log(0,  "***************" );
            log(0, name +" is not a valid locale. We currently only support: ");
            LocaleType[] values = LocaleType.values();
            for (final LocaleType newVar : values) {
                log(0, newVar.toString());
            }
            log(0,  "Defaulting to English." );
            log(0, "***************" );
            assert (!finf);
            throw e;
        }
        return type;
    }
}
