package com.becker.apps.misc.readnumber;

/**
 *
 * @author Barry Becker
 */
public enum TensNumber implements INumberEnum
{
    TWENTY("twenty", "t|w|e|n|t|ee"), 
    THIRTY("thirty", "th|i|r|t|ee"), 
    FOURTY("fourty", "f|o|r|t|ee"), 
    FIFTY("fifty", "f|i|f|t|ee"),  
    SIXTY("sixty", "s|i|k|s|t|ee"), 
    SEVENTY("seventy", "s|e|v|e|n|t|ee"), 
    EIGHTY("eighty", "aa|t|ee"), 
    NINETY("ninety", "nn|ii|n|t|ee");
    
    private String label_;
    private String pronunciation_;


    private TensNumber(String label, String pronunciation) {
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
