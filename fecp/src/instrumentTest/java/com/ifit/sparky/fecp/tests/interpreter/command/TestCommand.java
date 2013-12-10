/**
 * Tests the Command Object.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * Tests the Command constructors, getters and setters, for any abnormal values.
 */
package com.ifit.sparky.fecp.tests.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

public class TestCommand extends TestCase {

    /**
     * Setups the TestRunner for Command.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for Command.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testConstructor_command() throws Exception{

        Command commandObjOne;

        commandObjOne = new Command();

        // assert default values

        assertEquals(CommandId.NONE, commandObjOne.getCmdId());
        assertEquals(DeviceId.NONE, commandObjOne.getDevId());

        assertEquals(StatusId.DEV_NOT_SUPPORTED, commandObjOne.getStatus().getStatusId());

//        assertEquals(0, statusObjOne.getLength());
//        assertEquals(DeviceId.NONE, statusObjOne.getDevId());

        //assert none default values
        //statusObjTwo = new Status(StatusId.DONE, 1, CommandId.CONNECT, DeviceId.TREADMILL);

//        assertEquals(StatusId.DONE, statusObjTwo.getStsId());
//        assertEquals(1, statusObjTwo.getLength());
//        assertEquals(CommandId.CONNECT, statusObjTwo.getCmdId());
//        assertEquals(DeviceId.TREADMILL, statusObjTwo.getDevId());
    }

}