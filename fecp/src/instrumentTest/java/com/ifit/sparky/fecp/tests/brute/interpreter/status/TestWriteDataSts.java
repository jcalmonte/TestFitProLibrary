/**
 * Tests the Write data Status Class.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * Tests the handling of the message.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteDataSts;
import com.ifit.sparky.fecp.tests.brute.interpreter.command.TestCommandBuilder;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestWriteDataSts extends TestCase {

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
    public void testWriteDataSts_constructor() throws Exception{

        WriteDataSts sts;

        sts = new WriteDataSts(DeviceId.TREADMILL);

        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, sts.getStsId());
        assertEquals(CommandId.WRITE_DATA, sts.getCmdId());
        assertEquals(5, sts.getLength());//min length
    }
    /** Tests the interpretation of the buffer
     *
     * @throws Exception
     */
    public void testWriteDataSts_MessageHandling() throws Exception{

        WriteDataSts sts;
        ByteBuffer buff;
        TestCommandBuilder builder = new TestCommandBuilder();

        sts = new WriteDataSts(DeviceId.INCLINE_TRAINER);

        //message with no values in it
        buff = builder.buildBuffer(sts.getDevId(), 5,sts.getCmdId(),StatusId.DONE);

        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        // not much variation from this.
        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(StatusId.DONE, sts.getStsId());
        assertEquals(CommandId.WRITE_DATA, sts.getCmdId());
        assertEquals(5, sts.getLength());
    }
}
