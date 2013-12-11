/**
 * Tests the Device Object.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * Tests the Device constructors, getters and setters, for any abnormal values.
 */
package com.ifit.sparky.fecp.tests.interpreter.device;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import  com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashSet;

public class TestDevice extends TestCase {

    /**
     * Setups the TestRunner for Device.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for Device.
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
    public void testConstructor_device() throws Exception{

        Device deviceObjOne;
        HashSet<Command> cmdSet = new HashSet<Command>();
        ArrayList<Device> deviceList = new ArrayList<Device>();
        HashSet<BitFieldId> bitSet = new HashSet<BitFieldId>();
        ArrayList<Byte> byteList = new ArrayList<Byte>();
        Byte b1;
        Byte b2;
        BitfieldDataConverter converter;

        deviceObjOne = new Device();

        assertEquals(DeviceId.NONE,deviceObjOne.getDevId());
        assertEquals(0, deviceObjOne.getCommandSet().size());
        assertEquals(0, deviceObjOne.getSubDeviceList().size());
        assertEquals(0, deviceObjOne.getSupportedBitfields().size());

        deviceObjOne = new Device(0x04);

        assertEquals(DeviceId.TREADMILL,deviceObjOne.getDevId());
        assertEquals(0, deviceObjOne.getCommandSet().size());
        assertEquals(0, deviceObjOne.getSubDeviceList().size());
        assertEquals(0, deviceObjOne.getSupportedBitfields().size());

        deviceObjOne = new Device(0x04);

        assertEquals(DeviceId.TREADMILL,deviceObjOne.getDevId());
        assertEquals(0, deviceObjOne.getCommandSet().size());
        assertEquals(0, deviceObjOne.getSubDeviceList().size());
        assertEquals(0, deviceObjOne.getSupportedBitfields().size());

        deviceObjOne = new Device(DeviceId.INCLINE_TRAINER);

        assertEquals(DeviceId.INCLINE_TRAINER,deviceObjOne.getDevId());
        assertEquals(0, deviceObjOne.getCommandSet().size());
        assertEquals(0, deviceObjOne.getSubDeviceList().size());
        assertEquals(0, deviceObjOne.getSupportedBitfields().size());

        //add commands
        cmdSet.add(new Command(1, CommandId.CONNECT, DeviceId.TREADMILL));
        cmdSet.add(new Command(2, CommandId.DISCONNECT, DeviceId.TREADMILL));

        //add subDevices
        deviceList.add(new Device());
        deviceList.add(new Device());

        //add bitfields
        bitSet.add(BitFieldId.TARGET_MPH);
        bitSet.add(BitFieldId.CURRENT_MPH);

        b1 = 0x0B;
        b2 = 0x01;
        byteList.add(b1);
        byteList.add(b2);

        deviceObjOne = new Device(cmdSet, deviceList, bitSet, DeviceId.TREADMILL);

        assertEquals(DeviceId.TREADMILL,deviceObjOne.getDevId());
        assertEquals(2, deviceObjOne.getCommandSet().size());
        assertEquals(2, deviceObjOne.getSubDeviceList().size());
        assertEquals(2, deviceObjOne.getSupportedBitfields().size());
        assertEquals(1, deviceObjOne.getSupportedReadBitfields().size());
        assertEquals(1, deviceObjOne.getSupportedWriteBitfields().size());
        for(BitFieldId id : bitSet)
        {
            converter =id.getData(byteList);
            if(converter.getClass() == SpeedConverter.class)
            {
                assertEquals(26.7, ((SpeedConverter)converter).getSpeed());
            }
        }
        deviceList.add(new Device());
        cmdSet.add(new Command(3, CommandId.CALIBRATE, DeviceId.INCLINE_TRAINER));

        deviceObjOne = new Device(cmdSet, deviceList, bitSet, 0x05);

        assertEquals(DeviceId.INCLINE_TRAINER,deviceObjOne.getDevId());
        assertEquals(3, deviceObjOne.getCommandSet().size());
        assertEquals(3, deviceObjOne.getSubDeviceList().size());
        assertEquals(2, deviceObjOne.getSupportedBitfields().size());
        assertEquals(1, deviceObjOne.getSupportedReadBitfields().size());
        assertEquals(1, deviceObjOne.getSupportedWriteBitfields().size());
    }

    /** Tests the getters and Setters.
     *
     * @throws Exception
     */
    public void testGettersSetters_device() throws Exception{

        Device deviceObjOne;
        Command cmdObjOne;
        ArrayList<Command> cmdList = new ArrayList<Command>();
        ArrayList<Device> deviceList = new ArrayList<Device>();

        deviceObjOne = new Device();

        //test Set Device ID
        deviceObjOne.setDevId(DeviceId.TREADMILL);

        assertEquals(DeviceId.TREADMILL, deviceObjOne.getDevId());

        deviceObjOne.setDevId(0x05);

        assertEquals(DeviceId.INCLINE_TRAINER, deviceObjOne.getDevId());

        //test Add Command
        cmdObjOne = new Command(2, CommandId.CONNECT, DeviceId.INCLINE_TRAINER);

        deviceObjOne.addCommand(cmdObjOne);
        assertEquals(1,deviceObjOne.getCommandSet().size());
        assertEquals(CommandId.CONNECT, deviceObjOne.getCommandSet().get(CommandId.CONNECT).getCmdId());
        //test getCommand
        assertEquals(CommandId.CONNECT, deviceObjOne.getCommand(0x04).getCmdId());
        assertEquals(CommandId.CONNECT, deviceObjOne.getCommand(CommandId.CONNECT).getCmdId());

        //test add Commands
        cmdList.add(new Command(3, CommandId.CALIBRATE, DeviceId.INCLINE_TRAINER));
        cmdList.add(new Command(4, CommandId.DISCONNECT, DeviceId.INCLINE_TRAINER));
        deviceObjOne.addCommands(cmdList);

        assertEquals(3,deviceObjOne.getCommandSet().size());
        assertEquals(4, deviceObjOne.getCommandSet().get(CommandId.DISCONNECT).getLength());
        assertEquals(3, deviceObjOne.getCommandSet().get(CommandId.CALIBRATE).getLength());

        //test add SubDevices
        deviceObjOne.addSubDevice(new Device(DeviceId.TREADMILL));//added a treadmill
        assertEquals(1, deviceObjOne.getSubDeviceList().size());
        //test get subDevice
        assertEquals(DeviceId.TREADMILL, deviceObjOne.getSubDevice(0x04).getDevId());
        assertEquals(DeviceId.TREADMILL, deviceObjOne.getSubDevice(DeviceId.TREADMILL).getDevId());

        //test addAllSubDevices
        deviceList.add(new Device(DeviceId.MAIN));
        deviceList.add(new Device(DeviceId.NONE));

        deviceObjOne.addAllSubDevice(deviceList);
        assertEquals(3, deviceObjOne.getSubDeviceList().size());
    }

    /** Tests the Bitfield related getters and setters
     *
     * @throws Exception
     */
    public void testBitfieldGetSet_device()throws Exception
    {

    }


    /** Tests the getters and Setters.
     *
     * @throws Exception
     */
    public void testExceptions_device() throws Exception{

        Device deviceObjOne;
        //just need to test that if you add a command that already is in the list.
        //It throws an exception
        try
        {
            //test
            deviceObjOne = new Device();
            deviceObjOne.addCommand(new Command(2,CommandId.CONNECT, DeviceId.INCLINE_TRAINER));
            assertTrue(true);
            deviceObjOne.addCommand(new Command(2,CommandId.CONNECT, DeviceId.INCLINE_TRAINER));
            fail();
        }
        catch (Exception ex)
        {
            assertTrue(true);
        }
    }
}
