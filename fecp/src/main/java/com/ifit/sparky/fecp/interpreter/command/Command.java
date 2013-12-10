/**
 * Handles all the command SuperClass items.
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This class will handle the command items, mainly anything dealing with the sending to the system.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.Status;

public class Command {

    private Status mStatus;
    private int mLength;
    private CommandId mCmdId;
    private DeviceId mDevId;

    public Command()
    {
        this.mStatus = new Status();
        this.mLength = 0;
        this.mCmdId = CommandId.NONE;
        this.mDevId = DeviceId.NONE;
    }

}
