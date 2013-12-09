/**
 * Tests all the items in the status SuperClass
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * This class will test all the items of the status superclass. This includes the enums, invalid
 * inputs, and valid inputs
 */
package com.ifit.sparky.fecp.tests.interpreter.status;

import android.util.StateSet;

import com.ifit.sparky.fecp.interpreter.status.Status;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;


/**
 * Created by Levi.Balling on 12/6/13.
 */
public class TestStatus extends TestCase{

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testConstructor_status() throws Exception{

        Status statusObjOne;
        Status statusObjTwo;

        statusObjOne = new Status();

        // assert default values
        assertEquals(StatusId.DEV_NOT_SUPPORTED, statusObjOne.getStsId());
        assertEquals(0, statusObjOne.getLength());
        assertEquals(CommandId.NONE, statusObjOne.getCmdId());
        assertEquals(DeviceId.NO_DEVICE, statusObjOne.getDeviceId());

        //assert none default values
        statusObjTwo = new Status(StatusId.DONE, 1, CommandId.CONNECT, DeviceId.TREADMILL);

        assertEquals(StatusId.DONE, statusObjTwo.getStsId());
        assertEquals(1, statusObjTwo.getLength());
        assertEquals(CommandId.CONNECT, statusObjTwo.getStsId());
        assertEquals(DeviceId.TREADMILL, statusObjTwo.getDevicId());
    }

    /** This test method is to throw errors when the values are out of the limits
     * @throws Exception
     */
    public void testConstructorExceptions_status() throws Exception{

    //assign invalid values, and check exceptions

    }

    /** Tests the setters for the status
     * @throws Exception
     */
    public void testSetters_status() throws Exception{

        Status statusObjOne;

        statusObjOne = new Status();
        //test setters
        statusObjOne.setStsId(StatusId.DONE);
        statusObjOne.setLength(1);
        statusObjOne.setCmdId(CommandId.CONNECT);
        statusObjOne.setDeviceId(DeviceId.TREADMILL);

        assertEquals(StatusId.DONE, statusObjOne.getStsId());
        assertEquals(1, statusObjOne.getLength());
        assertEquals(CommandId.CONNECT, statusObjOne.getStsId());
        assertEquals(DeviceId.TREADMILL, statusObjOne.getDevicId());
    }


}
