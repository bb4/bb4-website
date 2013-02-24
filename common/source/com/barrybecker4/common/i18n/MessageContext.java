/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.common.i18n;

import com.barrybecker4.common.ILog;

import javax.swing.*;
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
     * @param applicationResourcePath  application specific messages
     */
    public void setApplicationResourcePath(String applicationResourcePath)    {
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
     * If not found there, look in the application specific bundle.
     * @param key
     * @return  the localized message label
     */
    public String getLabel(String key)  {
        if (commonMessages_ == null)  {
            initCommonMessages(currentLocale_);
        }
        if (commonMessageKeys_.contains(key))  {
            return commonMessages_.getString(key);
        }
        else {

            if (applicationMessages_ == null) {
                loadAppResources();
            }

            String label = key; // default
            try {
               label = applicationMessages_.getString(key);
            }
            catch (MissingResourceException e) {
               log(0,  e.getMessage() );
            }
            return label;
        }
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

        assert (applicationResourcePath_ != null) :
                "Cannot load app resources until applicationResourcePath has been set.";
        try {
            applicationMessages_ = ResourceBundle.getBundle(
                    applicationResourcePath_, currentLocale_.getLocale());
        }
        catch (MissingResourceException e) {
            log(0, "could not find " + applicationResourcePath_);
            e.printStackTrace();
        }
        assert (applicationMessages_ != null) : "gameMessages were null after loading them from :"
                + applicationResourcePath_;
    }

    /**
     * Iterate through all the message keys in the message
     * bundles for all locales and verify that they all have the
     * same keys. If any are missing for a given locale they need to be added.
     * @@ currently we only check the common common bundle, but we should do all.
     * @@ This could be moved to a JUnit test.
     */
    private void verifyConsistentMessageBundles() {
        log(1, "verifying consistency of message bundles... ");
        // an array of hashSets of the keys for each bundle
        List<Set<String>> messageKeySets = new ArrayList<Set<String>>();
        LocaleType[] locales = LocaleType.values();
        for (final LocaleType newVar : locales) {
            ResourceBundle bundle = ResourceBundle.getBundle(commonResourcePath_,
                    newVar.getLocale());
            Set<String> keySet = new HashSet<String>();
            Enumeration enum1 = bundle.getKeys();
            while (enum1.hasMoreElements()) {
                String key = (String) enum1.nextElement();
                log(2, newVar.name() + " " + key);
                keySet.add(key);
            }
            messageKeySets.add(keySet);
            log(1, "keySet size for " + (newVar).getLocale() + '=' + keySet.size());
        }
        // now that we have the keysets report on their consistency.
        // assume that the first is the default (en)
        boolean allConsistent = true;
        Set<String> defaultKeySet = messageKeySets.get(0);
        // first check that all the non-default locales do not contain keys
        // that the default locale does not have (less common).
        for (int i=1; i<locales.length; i++) {
            Set<String> keySet = messageKeySets.get(i);
            for (String key : keySet) {
                if (!defaultKeySet.contains(key)) {
                    log(0, commonResourcePath_ + " for locale " + locales[i]
                            + " contains the key, " + key + ", that is not in the default locale (en).");
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
        for (String key : defaultKeySet) {
            for (int i = 1; i < locales.length; i++) {
                Set keySet = messageKeySets.get(i);
                if (!keySet.contains(key)) {
                    log(0, commonResourcePath_ + " for locale " + locales[i]
                            + " does not contain the key " + key);
                    allConsistent = false;
                }
            }
        }
        if (allConsistent)
            log(0, "The bundles for all the locales are consistent.");
        else
            log(0, "Inconsistent bundles. Please correct the above items.");
    }


    /**
     * Looks up an {@link LocaleType} for a given locale name.
     * @param finf fail if not found.
     * @return locale
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