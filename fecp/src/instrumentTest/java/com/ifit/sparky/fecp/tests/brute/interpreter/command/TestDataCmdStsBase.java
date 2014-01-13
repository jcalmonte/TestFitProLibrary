/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.DataBaseCmd;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestDataCmdStsBase extends TestCase {

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
    public void testDataCmdSts_Constructor() throws Exception{

        DataBaseCmd dataBase;


        dataBase = new DataBaseCmd();
        //default constructor
        assertEquals(0, dataBase.getNumOfDataBytes());


        assertEquals(0, dataBase.get);

        assertEquals(DeviceId.NONE, dataBase.getDevId());


    }


}
