package com.becker.common;

/**
 * Enables you to get a class loader from a static context.
 *
 * @author Barry Becker
 */

public class ClassLoaderSingleton {
    private static ClassLoaderSingleton cls_ = null;
    private static ClassLoader loader_ = null;

    // private constructor
    private ClassLoaderSingleton()
    {
        loader_ = this.getClass().getClassLoader();
    }

    public static ClassLoader getClassLoader()
    {
        if (cls_==null)
            cls_ = new ClassLoaderSingleton();
        return loader_;
    }
}

