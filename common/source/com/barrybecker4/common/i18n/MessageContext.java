/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.common.i18n;

import com.barrybecker4.common.ILog;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Manage access to localize message bundles.
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
public final class MessageContext {

    public static final LocaleType DEFAULT_LOCALE = LocaleType.ENGLISH;

    /** logger object. Use console by default. */
    private ILog logger_;

    /** now the variable forms of the above defaults */
    private int debug_ = 0;

    private Set<String> commonMessageKeys_;

    private final String commonResourcePath_;
    private String applicationResourcePath_;

    private ResourceBundle commonMessages_ = null;
    private ResourceBundle applicationMessages_ = null;

    private LocaleType currentLocale_ = DEFAULT_LOCALE;


    /**
     * Constructor
     * @param commonResourcePath  common messages
     */
    public MessageContext(String commonResourcePath) {

        commonResourcePath_ = commonResourcePath;
    }

    /**
     * @param applicationResourcePath application specific messages
     */
    public void setApplicationResourcePath(String applicationResourcePath) {
        applicationResourcePath_ = applicationResourcePath;
        applicationMessages_ = null;
    }

    public void setDebugMode(int debugMode) {
        debug_ = debugMode;
    }

    /**
     * @param logger the logging device. Determines where the output goes.
     */
    public void setLogger( ILog logger ) {
        assert logger != null;
        logger_ = logger;
    }


    private void log(int logLevel, String message) {
        if (logger_ == null) {
            throw new RuntimeException("You need to set a logger on the MessageContext before you can call log.");
        }
       logger_.print(logLevel, debug_, message);
    }

    /**
     * Set or change the current locale.
     * @param localeName name locale to use (something like ENGLISH, GERMAN, etc)
     */
    public void setLocale(String localeName) {
        setLocale(getLocale(localeName, true));
    }

    /**
     * Set or change the current locale.
     * @param locale locale to use
     */
    public void setLocale(LocaleType locale) {
        currentLocale_ = locale;
        applicationMessages_ = null;
        initCommonMessages(currentLocale_);
        JComponent.setDefaultLocale(currentLocale_.getLocale());
    }

    public Locale getLocale() {
        return currentLocale_.getLocale();
    }

    /**
     * Look first in the common message bundle.
     * If not found there, look in the application specific bundle if there is one.
     * @param key
     * @return  the localized message label
     */
    public String getLabel(String key)  {
        String label = key;
        if (commonMessages_ == null)  {
            initCommonMessages(currentLocale_);
        }
        if (commonMessageKeys_.contains(key))  {
            return commonMessages_.getString(key);
        }
        if (applicationResourcePath_ != null) {

            if (applicationMessages_ == null) {
                loadAppResources();
            }

            try {
               label = applicationMessages_.getString(key);
            }
            catch (MissingResourceException e) {
               log(0,  e.getMessage() );
            }

        }
        return label;
    }

    private void initCommonMessages(LocaleType locale) {
        commonMessages_ =
            ResourceBundle.getBundle(commonResourcePath_, locale.getLocale());
        Enumeration enum1 = commonMessages_.getKeys();

        commonMessageKeys_ = new HashSet<String>();
        while (enum1.hasMoreElements()) {
            commonMessageKeys_.add((String)enum1.nextElement());
        }
        JComponent.setDefaultLocale(locale.getLocale());
    }

    /**
     * This method causes the appropriate message bundle to
     * be loaded for the game specified.
     */
    private void loadAppResources() {

        try {
            applicationMessages_ =
                    ResourceBundle.getBundle(applicationResourcePath_, currentLocale_.getLocale());
        }
        catch (MissingResourceException e) {
            log(0, "could not find " + applicationResourcePath_);
            e.printStackTrace();
        }
        assert (applicationMessages_ != null) : "gameMessages were null after loading them from :"
                + applicationResourcePath_;
    }

    /**
     * Looks up an {@link LocaleType} for a given locale name.
     * @param finf fail if not found.
     * @return locale the name of a local. Something like ENGLISH, GERMAN, etc
     * @throws Error if the name is not a member of the enumeration
     */
    public LocaleType getLocale(String name, boolean finf) {
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