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
import com.ifit.sparky.fecp.interpreter.command.InvalidCommandException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.Status;
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

    /**
     * Tests the different Constructors.
     * @throws Exception
     */
    public void testConstructor_command() throws Exception{

        Command commandObjOne;
        Status  stsObj;
        commandObjOne = new Command();

        // assert default values
        assertEquals(StatusId.DEV_NOT_SUPPORTED, commandObjOne.getStatus().getStsId());
        assertEquals(0, commandObjOne.getLength());
        assertEquals(CommandId.NONE, commandObjOne.getCmdId());
        assertEquals(DeviceId.NONE, commandObjOne.getDevId());

        //setup 2nd constructor
        commandObjOne = new Command(1, CommandId.CONNECT, DeviceId.TREADMILL);

        //assert second constructor
        assertEquals(StatusId.DEV_NOT_SUPPORTED, commandObjOne.getStatus().getStsId());
        assertEquals(1, commandObjOne.getLength());
        assertEquals(CommandId.CONNECT, commandObjOne.getCmdId());
        assertEquals(DeviceId.TREADMILL, commandObjOne.getDevId());

        //setup 3nd constructor
        stsObj = new Status(StatusId.DONE, 2, CommandId.CONNECT, DeviceId.TREADMILL);
        commandObjOne = new Command(stsObj, 3, CommandId.CONNECT, DeviceId.TREADMILL);

        //assert second constructor
        assertEquals(StatusId.DONE, commandObjOne.getStatus().getStsId());
        assertEquals(3, commandObjOne.getLength());
        assertEquals(CommandId.CONNECT, commandObjOne.getCmdId());
        assertEquals(DeviceId.TREADMILL, commandObjOne.getDevId());
    }

    /**
     * Tests the all the different possible Exceptions.
     * @throws Exception
     */
    public void testExceptions_command() throws Exception{

        Command commandObjOne;
        Status  stsObj;
        commandObjOne = new Command();

        //setup constructor Length exception
        try
        {
            //test all valid limits
            commandObjOne = new Command(commandObjOne.MAX_MSG_LENGTH
                    , CommandId.CONNECT, DeviceId.TREADMILL);
            commandObjOne.setLength(0);
            commandObjOne.setLength(commandObjOne.MAX_MSG_LENGTH);
        }
        catch (Exception ex)
        {
            fail();//
        }
        //test all the Constructor exception throws
        try
        {
            //noinspection UnusedAssignment
            commandObjOne = new Command(commandObjOne.MAX_MSG_LENGTH+1
                    , CommandId.CONNECT, DeviceId.TREADMILL);
            fail();
        }
        catch (Exception ex)
        {
            assertTrue(true);// passed
        }

        try
        {
            //noinspection UnusedAssignment
            commandObjOne = new Command(-1, CommandId.CONNECT, DeviceId.TREADMILL);
            fail();
        }
        catch (Exception ex)
        {
            assertTrue(true);// passed
        }

        try
        {
            //CommandId exception throw
            stsObj = new Status(StatusId.DONE, 1, CommandId.DISCONNECT, DeviceId.TREADMILL);
            //noinspection UnusedAssignment
            commandObjOne = new Command(stsObj, 0, CommandId.CONNECT, DeviceId.TREADMILL);
            fail();
        }
        catch (InvalidCommandException ex)
        {
            assertTrue(true);// passed
        }

        //test all function exceptions
        stsObj = new Status(StatusId.DONE, 1, CommandId.DISCONNECT, DeviceId.TREADMILL);
        commandObjOne = new Command(0, CommandId.CONNECT, DeviceId.TREADMILL);

        try
        {
            //CommandId exception throw
            commandObjOne.setStatus(stsObj);
            fail();
        }
        catch (InvalidCommandException ex)
        {
            assertTrue(true);// passed
        }

        try
        {
            //length exception throw
            commandObjOne.setLength(commandObjOne.MAX_MSG_LENGTH + 1);
            fail();
        }
        catch (Exception ex)
        {
            assertTrue(true);// passed
        }

        try
        {
            //length exception throw
            commandObjOne.setLength(-1);
            fail();
        }
        catch (Exception ex)
        {
            assertTrue(true);// passed
        }
    }

    /**
     * Tests the Getters and Setters.
     * @throws Exception
     */
    public void testGetterSetter_command() throws Exception{

        Command commandObjOne;
        Status  stsObj;
        commandObjOne = new Command();

        // set values
        //test Length
        commandObjOne.setLength(1);
        assertEquals(1, commandObjOne.getLength());

        //test CommandId
        commandObjOne.setCmdId(CommandId.CONNECT);
        assertEquals(CommandId.CONNECT,commandObjOne.getCmdId());

        //test Status
        stsObj = new Status(StatusId.DONE, 1, CommandId.CONNECT, DeviceId.TREADMILL);

        commandObjOne.setStatus(stsObj);
        assertEquals(StatusId.DONE, commandObjOne.getStatus().getStsId());

        //reset the command object
        commandObjOne = new Command();

        //test set Command by int
        commandObjOne.setCmdId(0x04);//Connect command
        assertEquals(CommandId.CONNECT, commandObjOne.getCmdId());

        //test set Device
        commandObjOne.setDevId(DeviceId.TREADMILL);//Connect command
        assertEquals(DeviceId.TREADMILL, commandObjOne.getDevId());

        //test set Device by id
        commandObjOne.setDevId(0x05);
        assertEquals(DeviceId.INCLINE_TRAINER, commandObjOne.getDevId());
    }

}