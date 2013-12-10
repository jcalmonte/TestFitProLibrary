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

import  com.ifit.sparky.fecp.interpreter.device.Device;

import junit.framework.TestCase;

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

        deviceObjOne = new Device();

        // assert default values
//        assertEquals(StatusId.DEV_NOT_SUPPORTED, statusObjOne.getStsId());
//        assertEquals(0, statusObjOne.getLength());
//        assertEquals(CommandId.NONE, statusObjOne.getCmdId());
//        assertEquals(DeviceId.NONE, statusObjOne.getDevId());

        //assert none default values
        //statusObjTwo = new Status(StatusId.DONE, 1, CommandId.CONNECT, DeviceId.TREADMILL);

//        assertEquals(StatusId.DONE, statusObjTwo.getStsId());
//        assertEquals(1, statusObjTwo.getLength());
//        assertEquals(CommandId.CONNECT, statusObjTwo.getCmdId());
//        assertEquals(DeviceId.TREADMILL, statusObjTwo.getDevId());
    }

}
