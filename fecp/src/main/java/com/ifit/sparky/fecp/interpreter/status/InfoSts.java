/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InvalidCommandException;
import com.ifit.sparky.fecp.interpreter.device.*;

import java.nio.ByteBuffer;

public class InfoSts extends Status implements StatusInterface {

    DeviceInfo mInfo;
    public InfoSts(DeviceId devId) throws Exception
    {
        //Min length is 14 bytes
        super(StatusId.DEV_NOT_SUPPORTED, 14, CommandId.GET_INFO, devId);
        this.mInfo = new DeviceInfo();
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception
    {
        super.handleStsMsg(buff);

        //now parse the data
        if(this.mStsId == StatusId.DONE)
        {
            this.mInfo.interpretInfo(buff);
        }
    }

    public DeviceInfo getInfo()
    {
        return this.mInfo;
    }
}
