/**
 * Creates a connection to the Fitpro system.
 * @author Levi.Balling
 * @date 5/16/2014
 * @version 1
 * sets up the communication to the FitPro system.
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.TcpComm;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;

public class FitProTcp extends FecpController {

    private int mPort;
    private String mIpAddress;

    /**
     * This is for Fecp connections that don't req
     *
     * @param callback callback for the system
     * @throws Exception
     */
    /**
     * This sets up the FitPro for the wifi connection over TCP
     * @param ipAddress the ip address
     * @param port port to communicate over
     * @param callback the connection status callback
     * @throws Exception
     */
    public FitProTcp(String ipAddress, int port, SystemStatusCallback callback) throws Exception {
        super(CommType.TCP_COMMUNICATION, callback);
        this.mPort = port;
        this.mIpAddress = ipAddress;
    }

    /**
     * @throws Exception
     */
    @Override
    public void initializeConnection() throws Exception {
        this.mCommController = new TcpComm(this.mIpAddress, this.mPort, 100);
        super.initializeConnection();
    }

    /**
     * Initializes the connection and sets up the communication
     *
     * @param listener this listens for changes in the connection
     */
    @Override
    public void initializeConnection(CommInterface.DeviceConnectionListener listener) throws Exception {

        this.mCommController = new TcpComm(this.mIpAddress, this.mPort, 100);
        super.initializeConnection(listener);
    }

    /**
     * initializes the connection of the Fitpro
     * @param listener connection disconnect listener
     * @param timeout default timeout for a waiting for the response.
     * @throws Exception
     */
    public void initializeConnection(CommInterface.DeviceConnectionListener listener, int timeout) throws Exception {

        this.mCommController = new TcpComm(this.mIpAddress, this.mPort, timeout);
        super.initializeConnection(listener);
    }


    /**
     * Gets the Port for the socket communication
     * @return which port is used
     */
    public int getPort() {
        return mPort;
    }

    /**
     * Gets the ip address of the connection
     * @return ip address
     */
    public String getIpAddress() {
        return mIpAddress;
    }

    /**
     * Sets the port for the connection, doesn't do anything after initializing the connection
     * @param port port for the connection
     */
    public void setPort(int port) {
        this.mPort = port;
    }

    /**
     * the ip address for the connection, doesn't do anything after initializing the connection.
     * @param ipAddress ip address of the FitPro
     */
    public void setIpAddress(String ipAddress) {
        this.mIpAddress = ipAddress;
    }
}
