/**
 * Since the purpose of this file isn't to format messages, but to validate the FecpCmdHandler.
 * @author Levi.Balling
 * @date 2/6/14
 * @version 1
 * We will only send data, this class will hold onto the data till it is overwritten to.
 */
package com.ifit.sparky.fecp.tests.brute;

import com.ifit.sparky.fecp.communication.CommInterface;

import java.nio.ByteBuffer;


public class TestToolDumyCom implements CommInterface {


    public TestToolDumyCom()
    {

    }
    private ByteBuffer mSendBuffer;
    @Override
    public void sendCmdBuffer(ByteBuffer buff) {
        mSendBuffer = buff.duplicate();
    }

    @Override
    public ByteBuffer getStsBuffer() {
        return this.mSendBuffer;
    }

    /**
     * sends the command and waits for the reply to handle the buffer
     *
     * @param buff the command buffer to send
     * @return
     */
    @Override
    public ByteBuffer sendAndRecieveCmd(ByteBuffer buff) {
        this.mSendBuffer = buff.duplicate();
        try {

            Thread.sleep(50);
        }
        catch (Exception ex)
        {

        }
        return this.mSendBuffer;
    }
}
