package com.becker.apps.misc.readnumber;

/**
 * Large number gorups.
 *  a VIGINTILLION, for example,  is 10 ^ 63.
 * 
 * @author Barry Becker
 */
public enum GroupNumber implements INumberEnum
{
    
    THOUSHAND("thousand", "th|o|w|s|a|n|d"), 
    MILLION("million", "m|i|ll|y|o|n"), 
    BILLION("billion", "b|i|ll|y|o|n"), 
    TRILLION("trillion", "t|r|i|ll|y|o|n"), 
    QUADRILLION("quadrillion", "k|wh|a|dd|dd|r|i|l|y|o|n"), 
    PENTILLION("pentillion", "p|e|n|t|t|i|ll|y|o|n"), 
    SEXTILLION("sextillion", "s|e|k|s|t|i|ll|y|o|n"), 
    SEPTILLION("septillion", "s|e|p|t|t|i|ll|y|o|n"), 
    OCTILLION("octillion", "o|k|t|t|i|ll|y|o|n"), 
    NONILLION("nonillion", "n|o|n|i|ll|y|o|n"), 
    DECILLION("decillion", "d|e|s|s|i|ll|y|o|n"), 
    UNDECILLION("undecillion", "u|n|dd|e|s|i|ll|y|o|n"), 
    DUODECILLION("duodecillion", "d|ou|oo|d|e|s|i|ll|y|o|n"), 
    TREDECILLION("tredecillion", "t|r|aa|d|e|s|i|ll|y|o|n"), 
    QUATTUORDECILLION("quattuordecillion", "k|w|o|t|ou|oo|r|d|e|s|i|ll|y|o|n"), 
    QUINDECILLION("quindecillion", "k|w|i|n|d|e|s|i|ll|y|o|n"), 
    SEPTENDECILLION("septendecillion", "s|e|p|t|e|n|d|e|s|i|ll|y|o|n"), 
    OCTODECILLION("octodecillion", "o|k|t|oo|d|e|s|i|ll|y|o|n"), 
    NOVEMDECILLION("novemdecillion", "n|oo|v|e|m|d|e|s|i|ll|y|o|n"), 
    VIGINTILLION("vigintillion", "v|i|j|i|n|t|i|ll|y|o|n");
    
    /* add these some day
   unvigintillion
   dovigintillion
   trevigintillion
   quattuorvigintillion
   quinvigintillion
   sexvigintillion
   septenvigintillion
   octovigintillion
   novemvigintillion
   trigintillion
   untrigintillion
   dotrigintillion
   tretrigintillion
   quattuortrigintillion
   quintrigintillion
   sextrigintillion
   septentrigintillion
   octotrigintillion
   novemtrigintillion
     */

    private String label_;
    private String pronunciation_;


    /**
     * constructor for eye type enun.
     */
    private GroupNumber(String label, String pronunciation) {
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
