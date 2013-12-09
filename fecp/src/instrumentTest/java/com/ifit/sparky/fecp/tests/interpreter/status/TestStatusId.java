/**
 * Tests the StatusId enum Object.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * To make sure the StatusId works properly we will use it to test comparisons, get descriptions
 * and more.
 */
package com.ifit.sparky.fecp.tests.interpreter.status;

import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

public class TestStatusId extends TestCase {

        @Override
        protected void setUp() throws Exception {
            super.setUp();
        }

        @Override
        protected void tearDown() throws Exception {
            super.tearDown();
        }

        public void testEnum() {
            StatusId idOne = StatusId.DEV_NOT_SUPPORTED;
            assertEquals(StatusId.DEV_NOT_SUPPORTED, idOne);
            assertEquals(0x00, idOne);
        }

}
