package com.becker.common;

//import com.becker.sound.SpeechSynthesizer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provide support for asserts.<P>
 * Asserts here are similar to C++ ASSERTs, except that they always execute
 *   (unlike C++ where they can be turned off).  The guarantee that
 *   they will be executed implies that you can put code logic in asserts,
 *   not just tests.<P>
 * Asserts should <B>only</B> be called in situations where it is clear
 *   that if they fire, there is a bug in the program code (not a user bug).<P>
 * An assertion firing is similar to a null pointer exception or index out
 *   of bounds in its severity.
 *
 * @//semi-deprecated use java 1.4 native assert instead so assertions can be turned off.
 * There are still some cases where this can be useful - like when you want the check to happen
 * even though asserts are off.
 */

public class Assert
{

    // call this if an assertion oiccurs
    private static ArrayList handlers_ = new ArrayList();

    /**
     * Check that the object is not null
     *
     * @param obj  an object
     */
    public static void notNull( Object obj )
    {
        if ( obj == null ) {
            RuntimeException rte = new RuntimeException();
            callHandler( rte );
            //throw rte;
        }
    }

    /**
     * Check that the object is not null
     *
     * @param  obj   an object
     * @param  s     message for exception
     */
    public static void notNull( Object obj, String s )
    {
        if ( obj == null ) {
            RuntimeException rte = new RuntimeException( s );
            callHandler( rte );
            //throw rte;
        }
    }

    /**
     * Check that the object is null
     *
     * @param  obj   an object
     */
    public static void isNull( Object obj )
    {
        if ( obj != null ) {
            RuntimeException rte = new RuntimeException();
            callHandler( rte );
            //throw rte;
        }
    }

    /**
     * Check that the object is not null
     *
     * @param  obj   an object
     * @param  s     message for exception
     */
    public static void isNull( Object obj, String s )
    {
        if ( obj != null ) {
            RuntimeException rte = new RuntimeException( s );
            callHandler( rte );
            //throw rte;
        }
    }

    /**
     * Check that the boolean is true
     *
     * @param  b if b is not true then throw a runtime exception
     */
    public static void isTrue( boolean b )
    {
        if ( !b ) {
            RuntimeException rte = new RuntimeException();
            callHandler( rte );
            //throw new RuntimeException();
        }
    }

    /**
     * Check that the boolean is true
     *
     * @param  b  if b is not true then throw a runtime exception
     * @param  s  message for exception
     */
    public static void isTrue( boolean b, String s )
    {
        if ( !b ) {
            RuntimeException rte = new RuntimeException( s );
            callHandler( rte );
            //throw new RuntimeException(s);
        }
    }

    /**
     * Check that the boolean is false
     *
     * @param  b if b is not true then throw a runtime exception
     */
    public static void isFalse( boolean b )
    {
        if ( b ) {
            RuntimeException rte = new RuntimeException();
            callHandler( rte );
            //throw new RuntimeException();
        }
    }

    /**
     * Check that the boolean is false
     *
     * @param  b if b is not true then throw a runtime exception
     * @param  s  message for exception
     */
    public static void isFalse( boolean b, String s )
    {
        if ( b ) {
            RuntimeException rte = new RuntimeException( s );
            callHandler( rte );
            //throw new RuntimeException(s);
        }
    }

    /**
     * Throw a runtime exception
     *
     * @param s message for exception
     */
    public static void exception( String s )
    {
        RuntimeException rte = new RuntimeException( s );
        callHandler( rte );
        throw rte;
    }

    /**
     * just show the current stack trace without actually aborting
     */
    public static void printStackTrace()
    {
        RuntimeException rte = new RuntimeException();
        rte.printStackTrace();
    }

    /**
     * add a handler to be called when an assertion fails.
     * @param handler the handler. If null then it is removed.
     */
    public static void addHandler( AssertHandler handler )
    {
        handlers_.add( handler );
    }

    /**
     * remove a handler to be called when an assertion fails
     * @param handler the handler. If null then it is removed.
     */
    public static void removeHandler( AssertHandler handler )
    {
        handlers_.remove( handler );
    }

    /**
     * remove all handlers to be called when an assertion fails
     */
    public static void removeAllHandlers()
    {
        handlers_.clear();
    }

    /**
     * Call the error handler if there is one.
     * @param
     */
    public static void callHandler( RuntimeException rte )
    {
        //SpeechSynthesizer speech = new SpeechSynthesizer();
        //speech.sayPhoneWord( "e|rr|err" );
        if ( handlers_.size() > 0 ) {
            Iterator it = handlers_.iterator();
            while ( it.hasNext() ) {
                AssertHandler handler = (AssertHandler) it.next();
                handler.assertFailed( rte );
            }
        }
        throw rte;
    }
}



