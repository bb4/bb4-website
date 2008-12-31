package com.becker.misc.readnumber;

/**
 *
 * @author Barry Becker
 */
public enum SimpleNumber implements INumberEnum
{

    ONE("one", "w|o|n"),
    TWO("two", "t|ou|ou"),
    THREE("three", "th|r|ee"),
    FOUR("four", "f|or"),
    FIVE("five", "f|ii|v"),
    SIX("six", "s|i|k|s"),
    SEVEN("seven", "s|e|v|e|n"),
    EIGHT("eight", "aa|t"),
    NINE("nine", "nn|ii|n"),
    TEN("ten", "t|e|n"),
    ELEVEN("eleven", "e|l|e|v|e|n"),
    TWELVE("twelve", "t|w|e|l|v"),
    THIRTEEN("thirteen", "th|th|i|r|t|ee|n"),
    FOURTEEN("fourteen", "f|or|t|ee|n"),
    FIFTEEN("fifteen", "f|f|i|f|v|t|ee|n"),
    SIXTEEN("sixteen", "s|i|k|s|t|ee|n"),
    SEVENTEEN("seventeen", "s|e|v|e|n|t|ee|n"),
    EIGHTEEN("eighteen", "aa|t|ee|n"),
    NINETEEN("nineteen", "nn|ii|n|t|ee|n");


    private String label_;
    private String pronunciation_;

    public static final String HUNDRED = "hundred";
    public static final String HUNDRED_PRONOUNCE = "h|u|n|d|r|e|d";


    /**
     * constructor for eye type enum
     *
     * @param labelstring name of the eye type (eg "False Eye")
     */
    private SimpleNumber(String label, String pronunciation) {
        label_ = label;
        pronunciation_ = pronunciation;
    }


    public String toString() {
        return label_;
    }

    public String getPronunciation() {
        return pronunciation_;
    }

}
