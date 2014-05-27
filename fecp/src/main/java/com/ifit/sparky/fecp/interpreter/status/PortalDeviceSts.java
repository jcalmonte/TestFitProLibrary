/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 5/27/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

public class PortalDeviceSts extends Status {


    private static final int STS_LENGTH = 6;
    private SystemDevice mSysDev;
    public PortalDeviceSts(DeviceId devId) throws Exception
    {
        super(StatusId.DEV_NOT_SUPPORTED, 0, CommandId.PORTAL_DEV_LISTEN, devId);//0 length due to unknown

    }

    public SystemDevice getmSysDev() {
        return mSysDev;
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception {
        //raw data that is the system device
        buff.position(0);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buff.array());
        ObjectInput inObject = null;
        inObject = new ObjectInputStream(inputStream);

        this.mSysDev = (SystemDevice)inObject.readObject();
        inputStream.close();
        inObject.close();
    }
}
