package com.becker.game.common;

import java.util.Locale;

/**
 * Enum for the suppoerted locales.
 *
 * @author Barry Becker
 */
public enum LocaleType
{
    // currently supported locales
    ENGLISH ( new Locale("en", "US")),
    GERMAN ( new Locale("de", "DE")),
    JAPANESE ( new Locale("ja", "JP")),
    VIETNAMESE( new Locale("vi"));

    private Locale locale_;


    /**
     * constructor for eye type enum
     *

     * @param locale the locale corresponding to the enum value.
     */
    private LocaleType( Locale locale) {
        locale_ = locale;
    }


    public Locale getLocale()
    {
        return locale_;
    }


}

