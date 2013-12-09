/**
 * Tests the CommandId enum object.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * This will handle all the different enums and validate that they are correct.
 */
package com.ifit.sparky.fecp.tests.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.CommandId;

import junit.framework.TestCase;

public class TestCommandId extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEnum() {
        CommandId idOne = CommandId.NONE;
        assertEquals(CommandId.NONE, idOne);
        assertEquals(0x00, idOne);
    }

}
