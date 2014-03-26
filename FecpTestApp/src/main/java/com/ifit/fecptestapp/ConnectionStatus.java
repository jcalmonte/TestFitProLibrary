/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 3/25/2014
 * @version 1
 * Details.
 */
package com.ifit.fecptestapp;

import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;

public class ConnectionStatus implements SystemStatusCallback {

    private boolean mConnected;

    /**
     * initializes the Connection status of the system. and has callbacks for the system.
     */
    public ConnectionStatus()
    {
        this.mConnected = false;
    }
    /**
     * this method is called when the system is connected.
     */
    @Override
    public void systemConnected() {
        this.mConnected = true;
    }

    /**
     * this method is called when the system is disconnected.
     */
    @Override
    public void systemDisconnected() {

        this.mConnected = false;
    }

    /**
     * Gets the status of the Connection
     * @return true if connected false if disconnected
     */
    public boolean isConnected() {
        return mConnected;
    }
}
