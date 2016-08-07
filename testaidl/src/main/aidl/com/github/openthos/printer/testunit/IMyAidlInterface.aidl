// IMyAidlInterface.aidl
package com.github.openthos.printer.testunit;

// Declare any non-default types here with import statements

interface IMyAidlInterface {

    int getPid();

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
