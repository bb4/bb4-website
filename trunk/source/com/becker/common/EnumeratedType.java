package com.becker.common;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <code>EnumeratedType</code> is a helper class for declaring a set of
 * symbolic constants which have names, ordinals, and possibly descriptions.
 * The ordinals do not have to be contiguous.
 *
 * <p>Typically, for a particular set of constants, you derive a class from this
 * interface, and declare the constants as <code>public static final</code>
 * members. Give it a private constructor, and a <code>public static final
 * <i>ClassName</i> instance</code> member to hold the singleton instance.</p>
 *
 * The need for this class will go away when I upgrade to java 1.5.
 *
 */
public class EnumeratedType implements Cloneable
{
    //map symbol names to values.
    private HashMap valuesByName_ = new HashMap();

    // the smallest ordinal value.
    private int min_ = Integer.MAX_VALUE;

    // the largest ordinal value.
    private int max_ = Integer.MIN_VALUE;

    // the variables below are only set AFTER makeImmutable() has been called

    /** An array mapping ordinals to {@link Value}s. It is biased by the
     * min value. It is built by {@link #makeImmutable}.
     */
    private Value[] ordinalToValueMap_;
    private static final String[] emptyStringArray = new String[0];

    /**
     * Creates a new empty, mutable enumeration.
     */
    public EnumeratedType()
    {
    }

    /**
     * Creates an enumeration, with an array of values, and freezes it.
     */
    public EnumeratedType( Value[] values )
    {
        for ( int i = 0; i < values.length; i++ ) {
            register( values[i] );
        }
        makeImmutable();
    }

    /**
     * Creates an enumeration, initialize it with an array of strings, and
     * freezes it.
     */
    public EnumeratedType( String[] names )
    {
        for ( int i = 0; i < names.length; i++ ) {
            register( new MyBasicValue( names[i], i, names[i] ) );
        }
        makeImmutable();
    }

    /**
     * Create an enumeration, initializes it with arrays of code/name pairs,
     * and freezes it.
     */
    public EnumeratedType( String[] names, int[] codes )
    {
        for ( int i = 0; i < names.length; i++ ) {
            register( new MyBasicValue( names[i], codes[i], names[i] ) );
        }
        makeImmutable();
    }

    /**
     *Create an enumeration, initializes it with arrays of code/name pairs,
     * and freezes it.
     */
    public EnumeratedType( String[] names, int[] codes, String[] descriptions )
    {
        for ( int i = 0; i < names.length; i++ ) {
            register( new MyBasicValue( names[i], codes[i], descriptions[i] ) );
        }
        makeImmutable();
    }

    /**
     * A <code>Value</code> represents a member of an enumerated type. If an
     * enumerated type is not based upon an explicit array of values, an
     * array of {@link BasicValue}s will implicitly be created.
     */
    public interface Value
    {
        /** Returns the name of this value, unique within the enumerated
         * type. */
        String getName();

        /** Returns the ordinal of this value, unique within the enumerated
         * type. */
        int getOrdinal();

        /** Returns the description of this value. */
        String getDescription();

        /** Returns the enumerated type this value belongs to, never null. */
        EnumeratedType getEnumeratedType();

        /** throws a runtime exception stating that
         * this value is not handled in this context. */
        void unexpected();
    }

    /**
     * <code>BasicValue</code> is basic implementation of {@link Value}.
     */
    public static abstract class BasicValue implements Value
    {
        public final String name_;
        public final int ordinal_;
        public final String description_;

        /**
         * @pre name != null
         */
        public BasicValue( String name, int ordinal, String description )
        {
            assert ( name != null) : "name != null";
            this.name_ = name;
            this.ordinal_ = ordinal;
            this.description_ = description;
            // Check identity condition: only this value can have this name
            // and this ordinal. During construction, the enumerated type
            // may be null, or the object may not be registered, or the
            // enumerated type may still be mutable, so it's not practical to
            // check.
            final EnumeratedType enumeratedType = getEnumeratedType();
            if ( enumeratedType != null ) {
                Value other = enumeratedType.getValue( name, false );
                if ( other != null ) {
                    assert ( other == this );
                }
                if ( enumeratedType.isImmutable() ) {
                    other = enumeratedType.getValue( ordinal );
                    if ( other != null ) {
                        assert ( other == this );
                    }
                }
            }
        }

        public String getName()
        {
            return name_;
        }

        public int getOrdinal()
        {
            return ordinal_;
        }

        public String getDescription()
        {
            return description_;
        }

        /**
         * Returns the value's name.
         */
        public String toString()
        {
            return name_;
        }

        /**
         * Returns whether this value is equal to a given string.
         *
         * @deprecated I bet you meant to write
         *   <code>value.name_.equals(s)</code> rather than
         *   <code>value.equals(s)</code>, didn't you?
         */
        public boolean equals( String s )
        {
            return super.equals( s );
        }

        public void unexpected()
        {
            getEnumeratedType().unexpected( this );
        }
    }

    protected Object clone()
    {
        EnumeratedType clone = null;
        try {
            clone = (EnumeratedType) super.clone();
        } catch (CloneNotSupportedException ex) {
            assert false : "must support clone" ;
        }
        clone.valuesByName_ = (HashMap) valuesByName_.clone();
        clone.ordinalToValueMap_ = null;
        return clone;
    }

    /**
     * Creates a mutable enumeration from an existing enumeration, which may
     * already be immutable.
     */
    public EnumeratedType getMutableClone()
    {
        return (EnumeratedType) clone();
    }

    /**
     * Associates a symbolic name with an ordinal value.
     *
     * @pre !isImmutable()
     * @pre value.getName() != null
     */
    public void register( Value value )
    {
        assert !isImmutable(): "!isImmutable()" ;
        assert (value.getName() != null) : "value.getName() != null";
        Value old = (Value) valuesByName_.put( value.getName(), value );
        assert (old==null) : "Enumeration already contained a value '" + old.getName() + "'";

        min_ = Math.min( min_, value.getOrdinal() );
        max_ = Math.max( max_, value.getOrdinal() );
    }

    /**
     * Freezes the enumeration, preventing it from being further modified.
     */
    public void makeImmutable()
    {
        ordinalToValueMap_ = new Value[1 + max_ - min_];
        for ( Iterator values = valuesByName_.values().iterator();
              values.hasNext(); ) {
            Value value = (Value) values.next();
            final int index = value.getOrdinal() - min_;
            assert (ordinalToValueMap_[index]==null): "Enumeration has more than one value with ordinal " + value.getOrdinal();
            ordinalToValueMap_[index] = value;
        }
    }

    public final boolean isImmutable()
    {
        return (ordinalToValueMap_ != null);
    }

    /**
     * Returns the smallest ordinal defined by this enumeration.
     */
    public final int getMin_()
    {
        return min_;
    }

    /**
     * Returns the largest ordinal defined by this enumeration.
     */
    public final int getMax_()
    {
        return max_;
    }

    /**
     * Returns whether <code>ordinal</code> is valid for this enumeration.
     * This method is particularly useful in pre- and post-conditions, for
     * example
     * <blockquote>
     * <pre>&#64;param axisCode Axis code, must be a {&#64;link AxisCode} value
     * &#64;pre AxisCode.instance.isValid(axisCode)</pre>
     * </blockquote>
     *
     * @param ordinal Suspected ordinal from this enumeration.
     * @return Whether <code>ordinal</code> is valid.
     */
    public final boolean isValid( int ordinal )
    {
        return getValue( ordinal ) != null;
    }

    /**
     * Returns the name associated with an ordinal; the return value
     * is null if the ordinal is not a member of the enumeration.
     *
     * @pre isImmutable()
     */
    public final String getName( int ordinal )
    {
        Value value = getValue( ordinal );
        if ( value == null ) {
            return null;
        }
        else {
            return value.getName();
        }
    }

    /**
     * Returns the description associated with an ordinal; the return value
     * is null if the ordinal is not a member of the enumeration.
     *
     * @pre isImmutable()
     */
    public final String getDescription( int ordinal )
    {
        final Value value = getValue( ordinal );
        if ( value == null ) {
            return null;
        }
        else {
            return value.getDescription();
        }
    }

    /**
     * Returns the ordinal associated with a name
     *
     * @throws Error if the name is not a member of the enumeration
     */
    public final int getOrdinal( String name )
    {
        return getValue( name, true ).getOrdinal();
    }

    /**
     * Returns the value associated with an ordinal; the return value
     * is null if the ordinal is not a member of the enumeration.
     *
     * @pre isImmutable()
     */
    public Value getValue( int ordinal )
    {
        assert isImmutable(): "isImmutable()" ;
        if ( (ordinal < min_) || (ordinal > max_) ) {
            return null;
        }
        return ordinalToValueMap_[ordinal - min_];
    }

    /**
     * Returns the ordinal associated with a name.
     * @param name Name of enumerated value
     * @param finf Whether to fail if no value has this name
     * @throws Error if the name is not a member of the enumeration <em>and</em>
     *   <code>finf</code> is <code>true</code>
     * @post !(finf && return == null)
     */
    public Value getValue( String name, boolean finf )
    {
        final Value value = (Value) valuesByName_.get( name );
        if ( value == null && finf ) {
            throw new Error( "Unknown enum name:  " + name );
        }
        return value;
    }

    /**
     * Returns the names in this enumeration, in no particular order.
     */
    public String[] getNames()
    {
        return (String[]) valuesByName_.keySet().toArray( emptyStringArray );
    }

    /**
     * Returns an exception indicating that the value is illegal. (The client
     * needs to throw the exception.)
     */
    public void badValue( int ordinal )
    {
        assert false: "bad value " + ordinal + " (" +
                getName( ordinal ) + ") for enumeration '" + getClass().getName() + "'" ;
    }

    /**
     * Returns an exception indicating that we didn't expect to find this value
     * here.
     */
    public void unexpected( Value value )
    {
        assert false: "Was not expecting value '" + value +
                "' for enumeration '" + getClass().getName() + "' in this context";
    }

    private class MyBasicValue extends BasicValue
    {
        public MyBasicValue( String name, int ordinal, String description )
        {
            super( name, ordinal, description );
        }

        public EnumeratedType getEnumeratedType()
        {
            return EnumeratedType.this;
        }
    }
}
