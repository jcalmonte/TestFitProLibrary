/**
 * This is the command for Get supported Commands
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * creates the command for getting the commands supported by a single device
 * All the devices must support this.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetCmdsSts;

import java.nio.ByteBuffer;

public class GetCmdsCmd extends Command implements CommandInterface{

    private static final int MIN_CMD_LENGTH = 4;

    /**
     * default constructor
     */
    public GetCmdsCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.GET_SUPPORTED_COMMANDS);
        this.setStatus(new GetCmdsSts(this.getDevId()));
        this.setLength(MIN_CMD_LENGTH);
    }

    /**
     * constructor for the getCommands
     * @param devId the device id for the command
     * @throws Exception
     */
    public GetCmdsCmd(DeviceId devId) throws Exception
    {
        super(new GetCmdsSts(devId), MIN_CMD_LENGTH, CommandId.GET_SUPPORTED_COMMANDS, devId);
    }

    @Override
    public Command getCommandCopy() throws Exception{
        return new GetCmdsCmd(this.getDevId());
    }

    /**
     * Gets the command message for all commands that extend this class.
     * When they want to get the command they have to get the command.
     *
     * @return the Command structured to be ready to send over the usb.
     */
    @Override
    public ByteBuffer getCmdMsg() throws Exception{

        ByteBuffer buff;

        buff = super.getCmdMsg();
        //get the checksum value
        buff.put(Command.getCheckSum(buff));
        return buff;
    }
}
