/**
 * This is the command for Get Device Info.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * creates the command for getting the device info from the device.
 * All the devices must support this.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;

import java.nio.ByteBuffer;

public class InfoCmd extends Command implements CommandInterface{

    /**
     * default constructor
     */
    public InfoCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.GET_INFO);
        this.setStatus(new InfoSts(this.mDevId));
        this.setLength(4);
    }

    /**
     * default constructor
     */
    public InfoCmd(DeviceId devId) throws Exception
    {
        super(new InfoSts(devId),4,CommandId.GET_INFO,devId);
    }

    /**
     * Gets the command message for all commands that extend this class.
     * When they want to get the command they have to get the command.
     *
     * @return the Command structured to be ready to send over the usb.
     */
    @Override
    public ByteBuffer getCmdMsg() {

        ByteBuffer buff;

        buff = super.getCmdMsg();

        //get the checksum value
        buff.put(Command.getCheckSum(buff));

        return buff;
    }
}
