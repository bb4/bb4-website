package com.becker.misc.string;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some useful static string manipulation methods.
 * 
 * // Duy,
 *    Notice that I named my class according to its function 
 * and put it in a resonable package. Generic methods are public
 * so other application code cade reuse them. Alsways consider this
 * in your design. The main method can be used to do the testing 
 * for the class to make sure that everything is functioning as expected.
 * Note the use of meaningful variable names and methods.
 * Each method should be small and do only one thing.
*/
public class StringUtil
{
    private static final String OLD_TEXT = "hate";
    private static  final String NEW_TEXT = "love";
    
    /**
     * @param originalText to do replacement in
     * @param oldString string to be replaced
     * @param newString replacement text for oldString.
     * @return originalTest with first occurence of oldString replaced with newString.
     */
    public static final String replaceFirstOccurrence(String originalText, String oldString, String newString)
    {
        // I just extracted the implementation of String.replaceFirst and put it here. 
        // that may be considered cheating...
        Pattern pattern = Pattern.compile(oldString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(originalText);
           
        StringBuffer sb = new StringBuffer();
        matcher.reset();
        if (matcher.find())
        {
            matcher.appendReplacement(sb, newString);
        }
        matcher.appendTail(sb);
        return sb.toString();        
  
        // this will work too, but you are not allowed to use this method.
        //return   originalText.replaceFirst(oldString, newString);      
    }
    
    /**
     * Run through a rigorous set of test to verify all specified requirements and edge cases.
     */
    private static final void runReplacementRegressionTests()
    {
            String[] inputCases = {
                "f", 
                "",
                "ff hate",
                "hate",
                "HATE",
                "love",
                "sadlkfjHaTeasdflkjsad",
                "ate tate rate hate blate",
                "hate hate hate hate",
                "hattehatehatehate",
                "love hate hate foo"
            };
            String[] expectedOutput = {
                "f", 
                "",
                "ff love", 
                "love",
                "love",
                "love",
                "sadlkfjloveasdflkjsad",
                "ate tate rate love blate",
                "love hate hate hate",
                "hattelovehatehate",
                "love love hate foo"
            };
   
            for (int i = 0; i < inputCases.length;  i++)
            {
                String result = replaceFirstOccurrence(inputCases[i], OLD_TEXT, NEW_TEXT);
                if (result.equals(expectedOutput[i]))
                {
                    System.out.println("test case " + i + " passed.");
                }
                else 
                {
                    System.out.println("input:\n"+ inputCases[i] + "\n was converted into:\n"
                            + result + "\nbut we expected: \n" + expectedOutput[i]);
                }
            }            
    }
    
    /**
     * Let the user enter add hoc input from the keyboard interactively
     * until they get tired of it.
     */
    private static final void runUserInputTest()
    {
        boolean keepGoing = true;
        while (keepGoing) {
            Scanner keyboard = new Scanner(System.in); 

            System.out.println("Enter a line of text:");
            String lineOfText = keyboard.nextLine();
           
            String replacedlineOfText = replaceFirstOccurrence(lineOfText, OLD_TEXT, NEW_TEXT);
            System.out.println("I have rephrased that line to read:");
            System.out.println(replacedlineOfText);

            if (replacedlineOfText.equals(lineOfText))
            {
                System.out.println("There should be at least one occurrence of " + OLD_TEXT + " in your input text." );
            }

            System.out.println("Try again? (Y/N):");
            String response = keyboard.nextLine();
            keepGoing = (response.toUpperCase().equals("Y"));
        }
    }
    
	/**
     * Purpose:	Write a program that reads a line of text and then change the
				first word of hate to love.      
	*/
	public static void main (String[] args)
    {
        runReplacementRegressionTests();
        
        runUserInputTest();  
    }
}
