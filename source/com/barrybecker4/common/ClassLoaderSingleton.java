/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.common;

/**
 * Allows getting a class loader from a static context.
 *
 * @author Barry Becker
 */
public class ClassLoaderSingleton {
    private static ClassLoaderSingleton cls_;
    private static ClassLoader loader_;

    /** private constructor */
    private ClassLoaderSingleton() {
        loader_ = Thread.currentThread().getContextClassLoader();
    }

    public static synchronized ClassLoader getClassLoader() {
        if (cls_==null) {
            cls_ = new ClassLoaderSingleton();
        }
        return loader_;
    }

    /**
     * @param className the class to load.
     * @return  the loaded class.
     */
    public static Class loadClass(String className){
        return loadClass(className, null);
    }

    /**
     * @param className  the class to load.
     * @param defaultClassName  the backup class to load if className does not exist.
     * @return  the loaded class.
     */
    public static Class loadClass(String className, String defaultClassName) {
        Class theClass = null;
        try {
            theClass = Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Unable to find the class "+ className+". Check your classpath.");
            if (defaultClassName == null) {
                e.printStackTrace();
                return null;
            }
            System.out.println("Attempting to load "+defaultClassName+" instead.");
            try {
                theClass = Class.forName(defaultClassName);
            } catch (ClassNotFoundException cne) {
                 cne.printStackTrace();
            }
        }
        return theClass;
    }
}

