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

}
