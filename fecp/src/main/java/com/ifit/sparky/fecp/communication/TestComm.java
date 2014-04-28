/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 4/28/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.error.ErrorReporting;

import java.nio.ByteBuffer;

public class TestComm implements CommInterface {

    public TestComm()
    {
        //
    }
    /**
     * sends the command and waits for the reply to handle the buffer
     *
     * @param buff the command buffer to send
     * @return a buffer with the message 0 if failed
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff) {
        return null;
    }

    /**
     * Send and receive with a timeout
     *
     * @param buff    the buffer to send
     * @param timeout the max time you want to take till it is send
     * @return the buffer from the device 0 in the first byte for failed
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff, int timeout) {
        return null;
    }

    /**
     * Needs to report error with the err
     *
     * @param errReporterCallBack needs to be called to handle errors
     */
    @Override
    public void setupErrorReporting(ErrorReporting errReporterCallBack) {

    }
}
