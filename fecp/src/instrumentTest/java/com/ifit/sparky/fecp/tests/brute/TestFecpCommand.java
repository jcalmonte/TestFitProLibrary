/**
 * Tests the Fecp Command, and makes sure it is working properly.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * Tests all the major aspects of the fecp command. and everything dealing with it.
 */
package com.ifit.sparky.fecp.tests.brute;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;

public class TestFecpCommand extends TestCase {

    /**
     * Setups the TestRunner for Status.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    /**
     * Closes the TestRunner for Status.
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
    public void testConstructor_fecpCommand() throws Exception
    {
        FecpCommand fecpCmd;
        Device dev;
        Command cmd;

        //test default constructor
        fecpCmd = new FecpCommand();

        assertEquals(DeviceId.NONE, fecpCmd.getDevice().getInfo().getDevId());
        assertEquals(CommandId.NONE, fecpCmd.getCommand().getCmdId());
        assertEquals(null, fecpCmd.getCallback());
        assertEquals(0, fecpCmd.getTimeout());
        assertEquals(0, fecpCmd.getFrequency());
        assertEquals(0, fecpCmd.getCmdSentCounter());
        assertEquals(0, fecpCmd.getCmdReceivedCounter());

        //test 2nd constructor
        dev = new Device(DeviceId.INCLINE_TRAINER);
        cmd = new InfoCmd(DeviceId.INCLINE_TRAINER);
        dev.addCommand(cmd);
        fecpCmd = new FecpCommand(dev, cmd, null);

        assertEquals(DeviceId.INCLINE_TRAINER, fecpCmd.getDevice().getInfo().getDevId());
        assertEquals(CommandId.GET_INFO, fecpCmd.getCommand().getCmdId());
        assertEquals(null, fecpCmd.getCallback());
        assertEquals(0, fecpCmd.getTimeout());
        assertEquals(0, fecpCmd.getFrequency());
        assertEquals(0, fecpCmd.getCmdSentCounter());
        assertEquals(0, fecpCmd.getCmdReceivedCounter());

        //test the 3rd constructor
        fecpCmd = new FecpCommand(dev, cmd, null, 1);

        assertEquals(DeviceId.INCLINE_TRAINER, fecpCmd.getDevice().getInfo().getDevId());
        assertEquals(CommandId.GET_INFO, fecpCmd.getCommand().getCmdId());
        assertEquals(null, fecpCmd.getCallback());
        assertEquals(1, fecpCmd.getTimeout());
        assertEquals(0, fecpCmd.getFrequency());
        assertEquals(0, fecpCmd.getCmdSentCounter());
        assertEquals(0, fecpCmd.getCmdReceivedCounter());

        //test the 4th constructor
        fecpCmd = new FecpCommand(dev, cmd, null, 1, 2);

        assertEquals(DeviceId.INCLINE_TRAINER, fecpCmd.getDevice().getInfo().getDevId());
        assertEquals(CommandId.GET_INFO, fecpCmd.getCommand().getCmdId());
        assertEquals(null, fecpCmd.getCallback());
        assertEquals(1, fecpCmd.getTimeout());
        assertEquals(2, fecpCmd.getFrequency());
        assertEquals(0, fecpCmd.getCmdSentCounter());
        assertEquals(0, fecpCmd.getCmdReceivedCounter());
    }

    /** Tests the Setters.
     *
     * @throws Exception
     */
    public void testSetters_fecpCommand() throws Exception
    {
        FecpCommand fecpCmd;
        Device dev;
        Command cmd;

        //setup default values
        fecpCmd = new FecpCommand();

        assertEquals(DeviceId.NONE, fecpCmd.getDevice().getInfo().getDevId());
        assertEquals(CommandId.NONE, fecpCmd.getCommand().getCmdId());
        assertEquals(null, fecpCmd.getCallback());
        assertEquals(0, fecpCmd.getTimeout());
        assertEquals(0, fecpCmd.getFrequency());
        assertEquals(0, fecpCmd.getCmdSentCounter());
        assertEquals(0, fecpCmd.getCmdReceivedCounter());

        dev = new Device(DeviceId.INCLINE_TRAINER);
        cmd = new InfoCmd(DeviceId.INCLINE_TRAINER);
        dev.addCommand(cmd);

        //test setDevice
        fecpCmd.setDevice(dev);
        assertEquals(DeviceId.INCLINE_TRAINER, fecpCmd.getDevice().getInfo().getDevId());
        assertEquals(CommandId.NONE, fecpCmd.getCommand().getCmdId());

        //test setCommand
        fecpCmd.setCommand(cmd);
        assertEquals(CommandId.GET_INFO, fecpCmd.getCommand().getCmdId());

        //test SetCallback
        //only have null currently
        fecpCmd.setCallback(null);
        assertEquals(null, fecpCmd.getCallback());

        //test setTimeout
        assertEquals(0, fecpCmd.getTimeout());
        fecpCmd.setTimeout(123);
        assertEquals(123, fecpCmd.getTimeout());

        //test setFrequency
        assertEquals(0, fecpCmd.getFrequency());
        fecpCmd.setFrequency(321);
        assertEquals(321, fecpCmd.getFrequency());

        //test setSentCounter
        assertEquals(0, fecpCmd.getCmdSentCounter());
        fecpCmd.setCmdSentCounter(121);
        assertEquals(121, fecpCmd.getCmdSentCounter());

        //test set received counter
        assertEquals(0, fecpCmd.getCmdReceivedCounter());
        fecpCmd.setCmdReceivedCounter(212);
        assertEquals(212, fecpCmd.getCmdReceivedCounter());
    }
}
