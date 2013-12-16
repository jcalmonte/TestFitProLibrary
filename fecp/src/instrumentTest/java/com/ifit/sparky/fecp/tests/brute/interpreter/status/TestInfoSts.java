/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.DeviceInfo;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TestInfoSts extends TestCase {

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
    public void testConstructor_infoStatus() throws Exception{

        InfoSts sts;


        sts = new InfoSts(DeviceId.TREADMILL);

        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, sts.getStsId());
        assertEquals(CommandId.GET_INFO, sts.getCmdId());
        assertEquals(14, sts.getLength());//min length
    }

    /** Tests the interpretation of the buffer
     *
     * @throws Exception
     */
    public void testBufferReading_infoStatus() throws Exception{

        InfoSts sts;
        ByteBuffer buff;

        sts = new InfoSts(DeviceId.TREADMILL);
        buff = ByteBuffer.allocate(15);//needs to be precise
        buff.order(ByteOrder.LITTLE_ENDIAN);

        //initialize buffer command
        buff.put((byte)DeviceId.INCLINE_TRAINER.getVal());//device ID
        buff.put((byte)15);//Length
        buff.put((byte)sts.getCmdId().getVal());// Command ID
        buff.put((byte)StatusId.DONE.getVal());//Status ID
        //SW version
        buff.put((byte)1);
        //HW version
        buff.put((byte)2);
        //Serial Number
        buff.putInt(3);

        //Manufacture ID
        buff.putShort((short) 4);

        //number of supported bitfields
        buff.put((byte)1);

        //bitfield bytes
        buff.put((byte)1);//just the target MPH 
        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        // assert default values
        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(StatusId.DONE, sts.getStsId());

    }
}
