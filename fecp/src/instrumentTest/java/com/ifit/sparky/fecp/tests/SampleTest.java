package com.ifit.sparky.fecp.tests;

import com.ifit.sparky.fecp.HelloWorld;

import junit.framework.TestCase;

/**
 * Created by the handsome tyler.whipple on 11/22/13.
 * If only he talked to women instead of spending
 * his days and nights programming...
 */
public class SampleTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testStuff() {
        HelloWorld hey = new HelloWorld();

        assertEquals(true, true);
        assertEquals(3, hey.add());
    }
}
