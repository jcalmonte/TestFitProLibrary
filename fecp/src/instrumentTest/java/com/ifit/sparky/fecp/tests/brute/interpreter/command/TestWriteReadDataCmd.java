/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.TreeMap;

public class TestWriteReadDataCmd  extends TestCase {

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
    public void testWriteReadDataCmd_constructor() throws Exception{
        WriteReadDataCmd cmd;
        TreeMap<BitFieldId, Object> map = new TreeMap<BitFieldId, Object>();
        ArrayList<BitFieldId> idList = new ArrayList<BitFieldId>();
        cmd = new WriteReadDataCmd();

        // assert default values
        assertEquals(DeviceId.NONE, cmd.getDevId());
        assertEquals(6, cmd.getLength());//min length
        assertEquals(CommandId.WRITE_READ_DATA, cmd.getCmdId());

        //test 2nd Constructor
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        // assert default values
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(6, cmd.getLength());//min length
        assertEquals(CommandId.WRITE_READ_DATA, cmd.getCmdId());

        //test 3rd constructor
        map.put(BitFieldId.TARGET_MPH, 10.5);//10.5 mph

        cmd = new WriteReadDataCmd(DeviceId.TREADMILL, map);

        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(9, cmd.getLength());
        assertEquals(CommandId.WRITE_READ_DATA, cmd.getCmdId());
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));

        //test 4th constructor
        map.put(BitFieldId.TARGET_MPH, 10.5);//10.5 mph
        idList.add(BitFieldId.CURRENT_MPH);
        idList.add(BitFieldId.CURRENT_INCLINE);

        cmd = new WriteReadDataCmd(DeviceId.TREADMILL, map, idList);

        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(10, cmd.getLength());
        assertEquals(CommandId.WRITE_READ_DATA, cmd.getCmdId());
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));
        assertTrue(cmd.readContainsBitField(BitFieldId.CURRENT_MPH));
        assertTrue(cmd.readContainsBitField(BitFieldId.CURRENT_INCLINE));
    }


    /** Tests the the Add Write data function.
     *
     * @throws Exception
     */
    public void testWriteReadDataCmd_addWriteData() throws Exception{
        WriteReadDataCmd cmd;
        TreeMap<BitFieldId, Object> map = new TreeMap<BitFieldId, Object>();
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        assertEquals(CommandId.WRITE_READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(6, cmd.getLength());//default min length
        assertFalse(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));

        //add a single bitfield
        cmd.addWriteData(BitFieldId.TARGET_MPH, 10.5);

        assertEquals(9, cmd.getLength());
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));

        //test adding multiple in different sections
        map.put(BitFieldId.TARGET_MPH, 11.5);
        map.put(BitFieldId.TARGET_INCLINE, 11.50);
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        cmd.addWriteData(map);
        assertEquals(11, cmd.getLength());//default min length + 1 section
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_INCLINE));

        //add duplicates

        map.put(BitFieldId.TARGET_MPH, 12.5);
        map.put(BitFieldId.TARGET_INCLINE, 12.50);//%12.5
        map.put(BitFieldId.TARGET_RESISTANCE, 10.50);//%10.5
        cmd.removeWriteDataField(BitFieldId.TARGET_MPH);
        cmd.addWriteData(map);
        assertEquals(14, cmd.getLength());//2 sections, 3 short values
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_INCLINE));
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_RESISTANCE));

    }

    /** Tests the removal of bitfield ids.
     *
     * @throws Exception
     */
    public void testWriteReadDataCmd_removeWriteData() throws Exception{

        //default constructor
        WriteReadDataCmd cmd;
        TreeMap<BitFieldId, Object> map = new TreeMap<BitFieldId, Object>();
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        assertEquals(6, cmd.getLength());//default min length
        assertFalse(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));

        //remove empty list
        cmd.removeWriteDataField(BitFieldId.TARGET_MPH);

        assertEquals(6, cmd.getLength());//default min length
        assertFalse(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));

        //test removing 1 of 2
        map.put(BitFieldId.TARGET_MPH, 5.5);
        map.put(BitFieldId.TARGET_INCLINE, 10.1);
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        cmd.addWriteData(map);
        cmd.removeWriteDataField(BitFieldId.TARGET_MPH);

        assertEquals(9, cmd.getLength());//default min length + 1 section
        assertFalse(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));
        assertTrue(cmd.writeContainsBitField(BitFieldId.TARGET_INCLINE));

        //removing duplicates and from a list
        map.put(BitFieldId.TARGET_MPH, 11.5);
        map.put(BitFieldId.TARGET_INCLINE, 12.5);

        cmd.removeWriteDataField(map.keySet());
        assertEquals(6, cmd.getLength());//default min length + 1 section
        assertFalse(cmd.writeContainsBitField(BitFieldId.TARGET_MPH));
        assertFalse(cmd.writeContainsBitField(BitFieldId.TARGET_INCLINE));
    }


    /** Tests the the Add Write data function.
     *
     * @throws Exception
     */
    public void testWriteReadDataCmd_addReadData() throws Exception{
        WriteReadDataCmd cmd;
        ArrayList<BitFieldId> idList = new ArrayList<BitFieldId>();
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        assertEquals(CommandId.WRITE_READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(6, cmd.getLength());//default min length
        assertFalse(cmd.readContainsBitField(BitFieldId.TARGET_MPH));

        //add a single bitfield
        cmd.addReadBitField(BitFieldId.TARGET_MPH);

        assertEquals(7, cmd.getLength());
        assertTrue(cmd.readContainsBitField(BitFieldId.TARGET_MPH));

        //test adding multiple in different sections
        idList.add(BitFieldId.TARGET_MPH);
        idList.add(BitFieldId.TARGET_INCLINE);
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        cmd.addReadBitField(idList);
        assertEquals(7, cmd.getLength());//default min length + 1 section
        assertTrue(cmd.readContainsBitField(BitFieldId.TARGET_MPH));
        assertTrue(cmd.readContainsBitField(BitFieldId.TARGET_INCLINE));

        //add duplicates

        idList.add(BitFieldId.TARGET_MPH);
        idList.add(BitFieldId.TARGET_INCLINE);
        idList.add(BitFieldId.TARGET_RESISTANCE);
        cmd.removeReadDataField(BitFieldId.TARGET_MPH);
        cmd.addReadBitField(idList);
        assertEquals(8, cmd.getLength());//2 sections, 3 short values
        assertTrue(cmd.readContainsBitField(BitFieldId.TARGET_MPH));
        assertTrue(cmd.readContainsBitField(BitFieldId.TARGET_INCLINE));
        assertTrue(cmd.readContainsBitField(BitFieldId.TARGET_RESISTANCE));
    }

    /** Tests the removal of bitfield ids.
     *
     * @throws Exception
     */
    public void testWriteReadDataCmd_removeReadData() throws Exception{

        //default constructor
        WriteReadDataCmd cmd;
        ArrayList<BitFieldId> idList = new ArrayList<BitFieldId>();
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        assertEquals(6, cmd.getLength());//default min length
        assertFalse(cmd.readContainsBitField(BitFieldId.TARGET_MPH));

        //remove empty list
        cmd.removeReadDataField(BitFieldId.TARGET_MPH);

        assertEquals(6, cmd.getLength());//default min length
        assertFalse(cmd.readContainsBitField(BitFieldId.TARGET_MPH));

        //test removing 1 of 2
        idList.add(BitFieldId.TARGET_MPH);
        idList.add(BitFieldId.TARGET_INCLINE);
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        cmd.addReadBitField(idList);
        cmd.removeReadDataField(BitFieldId.TARGET_MPH);

        assertEquals(7, cmd.getLength());//default min length + 1 section
        assertFalse(cmd.readContainsBitField(BitFieldId.TARGET_MPH));
        assertTrue(cmd.readContainsBitField(BitFieldId.TARGET_INCLINE));

        //removing duplicates and from a list
        idList.add(BitFieldId.TARGET_MPH);
        idList.add(BitFieldId.TARGET_INCLINE);

        cmd.removeReadDataField(idList);
        assertEquals(6, cmd.getLength());//default min length + 1 section
        assertFalse(cmd.readContainsBitField(BitFieldId.TARGET_MPH));
        assertFalse(cmd.readContainsBitField(BitFieldId.TARGET_INCLINE));
    }


    /** Tests the Message make for the command
     *
     * @throws Exception
     */
    public void testWriteReadDataCmd_getCmdMsg() throws Exception{
        //default constructor
        WriteReadDataCmd cmd;
        ByteBuffer buffer;

        //test empty command
        cmd = new WriteReadDataCmd(DeviceId.TREADMILL);

        assertEquals(6, cmd.getLength());//default min length
        buffer = cmd.getCmdMsg();

        buffer.position(0);
        assertEquals(DeviceId.TREADMILL.getVal(), buffer.get());
        assertEquals(cmd.getLength(), buffer.get());
        assertEquals(cmd.getCmdId().getVal(), (buffer.get() & 0xFF));
        assertEquals(0, buffer.get());//number of Write section bytes
        assertEquals(0, buffer.get());//number of Read section bytes
        assertEquals(cmd.getLength(), buffer.capacity());
        //assume checksum is good

        //add Write bitfield
        cmd.addWriteData(BitFieldId.TARGET_MPH, 10.5);//105 in byte format
        buffer = cmd.getCmdMsg();
        assertEquals(9, cmd.getLength());

        buffer.position(0);
        assertEquals(DeviceId.TREADMILL.getVal(), buffer.get());
        assertEquals(cmd.getLength(), buffer.get());
        assertEquals(cmd.getCmdId().getVal(), (buffer.get() & 0xFF));
        assertEquals(1, buffer.get());//number of Write bytes
        assertEquals(1, buffer.get());//Section 0
        assertEquals(105, (buffer.getShort() & 0xFFFF));//targetMPH speed
        assertEquals(0, buffer.get());//number of Read bytes
        assertEquals(cmd.getLength(), buffer.capacity());

        //add Read bitfield
        cmd.addReadBitField(BitFieldId.CURRENT_MPH);//105 in byte format
        buffer = cmd.getCmdMsg();
        assertEquals(10, cmd.getLength());

        buffer.position(0);
        assertEquals(DeviceId.TREADMILL.getVal(), buffer.get());
        assertEquals(cmd.getLength(), buffer.get());
        assertEquals(cmd.getCmdId().getVal(), (buffer.get() & 0xFF));
        assertEquals(1, buffer.get());//number of Write bytes
        assertEquals(1, buffer.get());//Section 0
        assertEquals(105, (buffer.getShort() & 0xFFFF));//targetMPH speed
        assertEquals(1, buffer.get());//number of Read bytes
        assertEquals(2, buffer.get());//Section 0
        assertEquals(cmd.getLength(), buffer.capacity());
    }

}
