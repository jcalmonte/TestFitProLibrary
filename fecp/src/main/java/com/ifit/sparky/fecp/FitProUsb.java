/**
 * Creates a connection to the Fitpro system.
 * @author Levi.Balling
 * @date 5/16/2014
 * @version 1
 * sets up the communication to the FitPro system.
 */
package com.ifit.sparky.fecp;

import android.content.Context;
import android.content.Intent;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.UsbComm;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;

public class FitProUsb extends FecpController {

    private Context mContext;
    private Intent mIntent;

    /**
     * Sets up the controller, and all the facets dealing with the controller, specifically USB
     * @param context  the application context
     * @param intent Intent that is used to handle the communication
     * @throws Exception
     */
    public FitProUsb(Context context, Intent intent) throws Exception {
        super(CommType.USB_COMMUNICATION, null);
        this.mContext = context;
        this.mIntent = intent;
    }

    /**
     * Sets up the controller, and all the facets dealing with the controller, specifically USB
     * @param context  the application context
     * @param intent Intent that is used to handle the communication
     * @param callback the callback for connection and disconnections   @throws java.lang.Exception if the device is invalid
     * @throws Exception invalid parameters
     */
    public FitProUsb(Context context, Intent intent, SystemStatusCallback callback ) throws Exception {
        super(CommType.USB_COMMUNICATION, callback);
        this.mContext = context;
        this.mIntent = intent;
    }


    /**
     * Initializes the connection and sets up the communication
     *
     * @param listener this listens for changes in the connection
     */
    @Override
    public void initializeConnection(CommInterface.DeviceConnectionListener listener) throws Exception {
        this.mCommController = new UsbComm(this.mContext, this.mIntent, 100);
        super.initializeConnection(listener);
    }

    /**
     * @throws Exception
     */
    @Override
    public void initializeConnection() throws Exception {
        this.mCommController = new UsbComm(this.mContext, this.mIntent, 100);
        super.initializeConnection();
    }

}
