/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.common.i18n;

import java.util.Locale;

/**
 * Enum for the supported locales.
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
public enum LocaleType {

    // Currently supported locales
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
    LocaleType( Locale locale) {
        locale_ = locale;
    }


    public Locale getLocale() {
        return locale_;
    }

}

