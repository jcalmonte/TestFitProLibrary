/**
 * Simple Status to handle the confirmation of the data sent.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * This will confirm the data sent to the device.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.Serializable;

public class WriteDataSts extends Status implements StatusInterface, Serializable {

    private static final int MIN_STS_LENGTH = 5;

    /**
     * default constructor simple handle reply
     * @param devId the device id of the status
     * @throws Exception
     */
    public WriteDataSts(DeviceId devId) throws Exception
    {
        super(StatusId.DEV_NOT_SUPPORTED, MIN_STS_LENGTH, CommandId.WRITE_DATA, devId);
    }
}
