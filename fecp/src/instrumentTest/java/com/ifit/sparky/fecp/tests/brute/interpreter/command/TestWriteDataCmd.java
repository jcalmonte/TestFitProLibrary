/**
 * Tests the Write Data command.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * Tests the constructors, adding and removal of data.
 * Confirms the command buffer is formatted correctly
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.util.TreeMap;

public class TestWriteDataCmd extends TestCase {

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
    public void testWriteDataCmd_constructor() throws Exception{

        WriteDataCmd cmd;
        TreeMap<BitFieldId, Object> map = new TreeMap<BitFieldId, Object>();
        cmd = new WriteDataCmd();

        // assert default values
        assertEquals(DeviceId.NONE, cmd.getDevId());
        assertEquals(5, cmd.getLength());//min length
        assertEquals(CommandId.WRITE_DATA, cmd.getCmdId());

        //test 2nd Constructor
        cmd = new WriteDataCmd(DeviceId.TREADMILL);

        // assert default values
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(5, cmd.getLength());//min length
        assertEquals(CommandId.WRITE_DATA, cmd.getCmdId());

        //test 3rd constructor
        map.put(BitFieldId.KPH, 10.5);//10.5 mph

        cmd = new WriteDataCmd(DeviceId.TREADMILL, map);

        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(8, cmd.getLength());
        assertEquals(CommandId.WRITE_DATA, cmd.getCmdId());
        assertTrue(cmd.containsBitField(BitFieldId.KPH));
    }

    /** Tests adding a bitfield and a value to the command.
     *
     * @throws Exception
     */
    public void testWriteDataCmd_addBitField() throws Exception{

        WriteDataCmd cmd;
        TreeMap<BitFieldId, Object> map = new TreeMap<BitFieldId, Object>();
        cmd = new WriteDataCmd(DeviceId.TREADMILL);

        assertEquals(CommandId.WRITE_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(5, cmd.getLength());//default min length
        assertFalse(cmd.containsBitField(BitFieldId.KPH));

        //add a single bitfield
        cmd.addBitField(BitFieldId.KPH, 10.5);

        assertEquals(CommandId.WRITE_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(8, cmd.getLength());
        assertTrue(cmd.containsBitField(BitFieldId.KPH));

        //test adding multiple in different sections
        map.put(BitFieldId.KPH, 11.5);
        map.put(BitFieldId.INCLINE, 11.50);
        cmd = new WriteDataCmd(DeviceId.TREADMILL);

        cmd.addBitField(map);//overwrites the Target mph
        assertEquals(CommandId.WRITE_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(10, cmd.getLength());//default min length + 1 section
        assertTrue(cmd.containsBitField(BitFieldId.KPH));
        assertTrue(cmd.containsBitField(BitFieldId.INCLINE));

        //add duplicates

        map.put(BitFieldId.KPH, 12.5);
        map.put(BitFieldId.INCLINE, 12.50);//%12.5
        map.put(BitFieldId.RESISTANCE, 10.50);//%10.5
        cmd.removeDataField(BitFieldId.KPH);
        cmd.addBitField(map);
        assertEquals(CommandId.WRITE_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(12, cmd.getLength());//2 sections, 3 short values
        assertTrue(cmd.containsBitField(BitFieldId.KPH));
        assertTrue(cmd.containsBitField(BitFieldId.INCLINE));
        assertTrue(cmd.containsBitField(BitFieldId.RESISTANCE));
    }

    /** Tests the removal of bitfield ids.
     *
     * @throws Exception
     */
    public void testWriteDataCmd_removeBitfields() throws Exception{

        //default constructor
        WriteDataCmd cmd;
        TreeMap<BitFieldId, Object> map = new TreeMap<BitFieldId, Object>();
        cmd = new WriteDataCmd(DeviceId.TREADMILL);

        assertEquals(5, cmd.getLength());//default min length
        assertFalse(cmd.containsBitField(BitFieldId.KPH));

        //remove empty list
        cmd.removeDataField(BitFieldId.KPH);

        assertEquals(5, cmd.getLength());//default min length
        assertFalse(cmd.containsBitField(BitFieldId.KPH));

        //test removing 1 of 2
        map.put(BitFieldId.KPH, 5.5);
        map.put(BitFieldId.INCLINE, 10.1);
        cmd = new WriteDataCmd(DeviceId.TREADMILL);

        cmd.addBitField(map);
        cmd.removeDataField(BitFieldId.KPH);

        assertEquals(8, cmd.getLength());//default min length + 1 section
        assertFalse(cmd.containsBitField(BitFieldId.KPH));
        assertTrue(cmd.containsBitField(BitFieldId.INCLINE));

        //removing duplicates and from a list
        map.put(BitFieldId.KPH, 11.5);
        map.put(BitFieldId.INCLINE, 12.5);

        cmd.removeDataField(map.keySet());
        assertEquals(5, cmd.getLength());//default min length + 1 section
        assertFalse(cmd.containsBitField(BitFieldId.KPH));
        assertFalse(cmd.containsBitField(BitFieldId.INCLINE));
    }

    /** Tests the getCommandCopy Message, and the formatting
     *
     * @throws Exception
     */
    public void testWriteDataCmd_getCmdMsg() throws Exception{

        //default constructor
        WriteDataCmd cmd;
        ByteBuffer buffer;

        //test empty command
        cmd = new WriteDataCmd(DeviceId.TREADMILL);

        assertEquals(5, cmd.getLength());//default min length
        buffer = cmd.getCmdMsg();

        buffer.position(0);
        assertEquals(DeviceId.TREADMILL.getVal(), buffer.get());
        assertEquals(cmd.getLength(), buffer.get());
        assertEquals(cmd.getCmdId().getVal(), (buffer.get() & 0xFF));
        assertEquals(0, buffer.get());//number of section bytes
        assertEquals(cmd.getLength(), buffer.capacity());
        //assume checksum is good

        //add bitfield
        cmd.addBitField(BitFieldId.KPH, 10.5);//105 in byte format
        buffer = cmd.getCmdMsg();

        buffer.position(0);
        assertEquals(DeviceId.TREADMILL.getVal(), buffer.get());
        assertEquals(cmd.getLength(), buffer.get());
        assertEquals(cmd.getCmdId().getVal(), (buffer.get() & 0xFF));
        assertEquals(1, buffer.get());
        assertEquals(1, buffer.get());//Section 0
        assertEquals(105, (buffer.getShort() & 0xFFFF));//targetMPH speed
        assertEquals(cmd.getLength(), buffer.capacity());
    }
}
