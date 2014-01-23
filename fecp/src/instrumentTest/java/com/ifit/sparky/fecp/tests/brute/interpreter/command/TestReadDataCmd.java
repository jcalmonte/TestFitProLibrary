/**
 * Testing tool for handling the read data command.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Tests the different methods that are apart of the Read Data Command class.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.ReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TestReadDataCmd extends TestCase {

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
    public void testReadDataCmd_Constructor() throws Exception{


        //default constructor
        ReadDataCmd cmd = new ReadDataCmd();
        ArrayList<BitFieldId> idList = new ArrayList<BitFieldId>();

        assertEquals(CommandId.READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.NONE, cmd.getDevId());
        assertEquals(5, cmd.getLength());//default min length

        //test second constructor
        cmd = new ReadDataCmd(DeviceId.TREADMILL);

        assertEquals(CommandId.READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(5, cmd.getLength());//default min length

        //test third constructor with an empty list
        cmd = new ReadDataCmd(DeviceId.TREADMILL, idList);

        assertEquals(CommandId.READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(5, cmd.getLength());//default min length

        //test third constructor with an populated list
        idList.add(BitFieldId.KPH);
        idList.add(BitFieldId.INCLINE);
        cmd = new ReadDataCmd(DeviceId.TREADMILL, idList);

        assertEquals(CommandId.READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(6, cmd.getLength());//default min length + 1 section

    }

    /** Tests the adding Bitfield ids.
     *
     * @throws Exception
     */
    public void testReadDataCmd_addingBitfields() throws Exception{

        //default constructor
        ReadDataCmd cmd;
        ArrayList<BitFieldId> idList = new ArrayList<BitFieldId>();

        cmd = new ReadDataCmd(DeviceId.TREADMILL);

        assertEquals(CommandId.READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(5, cmd.getLength());//default min length
        assertFalse(cmd.containsBitField(BitFieldId.KPH));

        //add a single bitfield
        cmd.addBitField(BitFieldId.KPH);

        assertEquals(CommandId.READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(6, cmd.getLength());//default min length
        assertTrue(cmd.containsBitField(BitFieldId.KPH));

        //test
        idList.add(BitFieldId.KPH);
        idList.add(BitFieldId.INCLINE);
        cmd = new ReadDataCmd(DeviceId.TREADMILL);

        cmd.addBitField(idList);
        assertEquals(CommandId.READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(6, cmd.getLength());//default min length + 1 section
        assertTrue(cmd.containsBitField(BitFieldId.KPH));
        assertTrue(cmd.containsBitField(BitFieldId.INCLINE));

        //add duplicates

        idList.add(BitFieldId.KPH);
        idList.add(BitFieldId.INCLINE);
        cmd.addBitField(idList);
        assertEquals(CommandId.READ_DATA, cmd.getCmdId());
        assertEquals(DeviceId.TREADMILL, cmd.getDevId());
        assertEquals(6, cmd.getLength());//default min length + 1 section
        assertTrue(cmd.containsBitField(BitFieldId.KPH));
        assertTrue(cmd.containsBitField(BitFieldId.INCLINE));
    }

    /** Tests the removal of bitfield ids.
     *
     * @throws Exception
     */
    public void testReadDataCmd_removeBitfields() throws Exception{

        //default constructor
        ReadDataCmd cmd;
        ArrayList<BitFieldId> idList = new ArrayList<BitFieldId>();

        cmd = new ReadDataCmd(DeviceId.TREADMILL);

        assertEquals(5, cmd.getLength());//default min length
        assertFalse(cmd.containsBitField(BitFieldId.KPH));

        //remove empty list
        cmd.removeDataField(BitFieldId.KPH);

        assertEquals(5, cmd.getLength());//default min length
        assertFalse(cmd.containsBitField(BitFieldId.KPH));


        //test removing 1 of 2
        idList.add(BitFieldId.KPH);
        idList.add(BitFieldId.INCLINE);
        cmd = new ReadDataCmd(DeviceId.TREADMILL);

        cmd.addBitField(idList);
        cmd.removeDataField(BitFieldId.KPH);

        assertEquals(6, cmd.getLength());//default min length + 1 section
        assertFalse(cmd.containsBitField(BitFieldId.KPH));
        assertTrue(cmd.containsBitField(BitFieldId.INCLINE));

        //removing duplicates and from a list
        idList.add(BitFieldId.KPH);
        idList.add(BitFieldId.INCLINE);

        cmd.removeDataField(idList);
        assertEquals(5, cmd.getLength());//default min length + 1 section
        assertFalse(cmd.containsBitField(BitFieldId.KPH));
        assertFalse(cmd.containsBitField(BitFieldId.INCLINE));
    }

    /** Tests the getCommand Message, and the formatting
     *
     * @throws Exception
     */
    public void testReadDataCmd_getCmdMsg() throws Exception{

        //default constructor
        ReadDataCmd cmd;
        ByteBuffer buffer;

        //test empty command
        cmd = new ReadDataCmd(DeviceId.TREADMILL);

        assertEquals(5, cmd.getLength());//default min length
        buffer = cmd.getCmdMsg();

        buffer.position(0);
        assertEquals(DeviceId.TREADMILL.getVal(), buffer.get());
        assertEquals(cmd.getLength(), buffer.get());
        assertEquals(cmd.getCmdId().getVal(), (buffer.get() & 0xFF));
        assertEquals(0, buffer.get());
        assertEquals(cmd.getLength(), buffer.capacity());
        //assume checksum is good

        //add bitfield
        cmd.addBitField(BitFieldId.KPH);
        buffer = cmd.getCmdMsg();

        buffer.position(0);
        assertEquals(DeviceId.TREADMILL.getVal(), buffer.get());
        assertEquals(cmd.getLength(), buffer.get());
        assertEquals(cmd.getCmdId().getVal(), (buffer.get() & 0xFF));
        assertEquals(1, buffer.get());
        assertEquals(1, buffer.get());//Section 0
        assertEquals(cmd.getLength(), buffer.capacity());
    }
}
