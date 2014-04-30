/**
 * Interface for all communication types (e.g. usb, uart, blue tooth).
 * @author Ryan.Tensmeyer
 * @date 12/10/13
 * @version 1
 * Release Date
 * @date 12/10/13
 */

package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.error.ErrorReporting;

import java.nio.ByteBuffer;

public interface CommInterface {

    public interface DeviceConnectionListener{
        void onDeviceConnected();
        void onDeviceDisconnected();
    }

    void setConnectionListener(DeviceConnectionListener listener);

    /**
     * sends the command and waits for the reply to handle the buffer
     * @param buff the command buffer to send
     * @return a buffer with the message 0 if failed
     */
    ByteBuffer sendAndReceiveCmd(ByteBuffer buff);

    /**
     * Send and receive with a timeout
     * @param buff the buffer to send
     * @param timeout the max time you want to take till it is send
     * @return the buffer from the device 0 in the first byte for failed
     */
    ByteBuffer sendAndReceiveCmd(ByteBuffer buff, int timeout);

    /**
     * Needs to report error with the err
     * @param errReporterCallBack needs to be called to handle errors
     */
    void setupErrorReporting(ErrorReporting errReporterCallBack);



}
