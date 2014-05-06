/**
 * Interface callback for if the system connects or disconnects.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * If there is a disconnect the method will be called.
 */
package com.ifit.sparky.fecp.interpreter;

import com.ifit.sparky.fecp.SystemDevice;

public interface SystemStatusCallback {

    /**
     * this method is called when the system is disconnected.
     */
    void systemDisconnected();

    /**
     * This is called after system is connected
     * @param dev the System device that is connected.
     */
    void systemDeviceConnected(SystemDevice dev);

}
