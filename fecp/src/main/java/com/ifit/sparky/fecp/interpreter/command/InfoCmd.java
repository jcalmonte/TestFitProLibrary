/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class InfoCmd extends Command implements CommandInterface{

    /**
     * default constructor
     */
    public InfoCmd()
    {
        super();
    }

    /**
     * default constructor
     */
    public InfoCmd(DeviceId devId) throws Exception
    {
        super(4,CommandId.GET_INFO,devId);
    }

    /**
     * Gets the command message for all commands that extend this class.
     * When they want to get the command they have to get the command.
     *
     * @return the Command structured to be ready to send over the usb.
     */
    @Override
    public ByteBuffer getCmdMsg() {
        ByteBuffer buff = ByteBuffer.allocate(4);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.position(0);
        buff.put((byte)this.mDevId.getVal());
        buff.put((byte)this.mLength);
        buff.put((byte)this.mCmdId.getVal());
        //get the checksum value
        buff.put(Command.getCheckSum(buff));

        return buff;
    }
}
