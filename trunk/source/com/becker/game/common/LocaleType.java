package com.becker.game.common;

import com.becker.common.EnumeratedType;
import com.becker.game.common.GameContext;

import java.util.Locale;

/**
 * Enum for the suppoerted locales.
 *
 * @author Barry Becker
 */
public final class LocaleType  extends  EnumeratedType.BasicValue
{

    // Ordinals
    private static final int ENGLISH_CODE = 0;
    private static final int GERMAN_CODE = 1;
    // single eye if opponent plays first; 2 eyes if you play 1st
    private static final int JAPANESE_CODE = 2;
    // large internal space (>= 2 eyes) - even if opponent plays first
    private static final int VIETNAMESE_CODE = 3;

    static final String LOCALE_NAMES[] = { "ENGLISH", "GERMAN", "JAPANESE", "VIETNAMESE" };

    // The enumerated values
    // note the name is the label key rather than the label to avoid a checken egg problem.
    public static final LocaleType ENGLISH =
            new LocaleType(ENGLISH_CODE, LOCALE_NAMES[ENGLISH_CODE], new Locale("en", "US"));
    public static final LocaleType GERMAN =
            new LocaleType(GERMAN_CODE, LOCALE_NAMES[GERMAN_CODE], new Locale("de", "DE"));
    public static final LocaleType JAPANESE =
            new LocaleType(JAPANESE_CODE, LOCALE_NAMES[JAPANESE_CODE], new Locale("ja", "JP"));
    public static final LocaleType VIETNAMESE =
            new LocaleType(VIETNAMESE_CODE, LOCALE_NAMES[VIETNAMESE_CODE], new Locale("vi"));

    private Locale locale_ = null;

    /**
     * Contains all valid {@link com.becker.game.common.LocaleType}s.
     */
    private static final EnumeratedType enumeration = new EnumeratedType(
            new LocaleType[] {
                ENGLISH, GERMAN, JAPANESE, VIETNAMESE
            }
    );

    /**
     * constructor for eye type enum
     *
     * @param ordinal ordered integer value for the eye type enum
     * @param name string name of the eye type (eg "False Eye")
     */
    private LocaleType(final int ordinal, final String name, Locale locale) {
        super(name, ordinal, null);
        locale_ = locale;
    }


    public EnumeratedType getEnumeratedType() {
        return enumeration;
    }

    public static final EnumeratedType getAvailableLocales() {
         return enumeration;
    }

    public Locale getLocale()
    {
        return locale_;
    }
}

