package com.becker.common;

/**
 * Enables you to get a class loader from a static context.
 *
 * @author Barry Becker
 */

public class ClassLoaderSingleton {
    private static ClassLoaderSingleton cls_;
    private static ClassLoader loader_;

    // private constructor
    private ClassLoaderSingleton()
    {
        loader_ = Thread.currentThread().getContextClassLoader();
    }

    public static synchronized ClassLoader getClassLoader()
    {
        if (cls_==null) {
            cls_ = new ClassLoaderSingleton();
        }
        return loader_;
    }
}

