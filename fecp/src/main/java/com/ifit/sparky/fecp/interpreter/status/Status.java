/**
 * Handles all the status SuperClass items.
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * This class will handle the status items, mainly anything dealing with the reply from the system.
 */

package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

public class Status {

    private StatusId stsId;
    private int length;
    private CommandId cmdId;
    private DeviceId devId;

    /**
     * Default Constructor for the Status object.
     */
    public Status()
    {
        this.stsId = StatusId.DEV_NOT_SUPPORTED;
        this.length = 0;
        this.cmdId = CommandId.NONE;
        this.devId = DeviceId.NONE;
    }


}
