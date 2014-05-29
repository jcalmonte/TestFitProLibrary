/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 5/29/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.communication;

import java.net.InetSocketAddress;

public class TcpConnectionDevice extends ConnectionDevice {

    protected InetSocketAddress mIpAddress;

    /**
     * Default constructor of the Tcp Connection Device
     */
    public TcpConnectionDevice()
    {
        super();
        this.mCommType = CommType.TCP;
        this.mIpAddress = new InetSocketAddress("192.168.1.1", 8090);
    }

    /**
     * Creates a Tcp Connection device with ipaddress and port number
     * @param ipAddress ip address
     * @param portNumber port number
     */
    public TcpConnectionDevice(String ipAddress, int portNumber)
    {
        super();
        this.mCommType = CommType.TCP;
        this.mIpAddress = new InetSocketAddress(ipAddress, portNumber);
    }

    /**
     * Creates a Tcp Connection device with the InetSocketAddress
     * @param ipAddress InetSocketAddress object
     */
    public TcpConnectionDevice(InetSocketAddress ipAddress)
    {
        super();
        this.mCommType = CommType.TCP;
        this.mIpAddress = ipAddress;
    }

    /**
     * Gets the Ipaddress object
     * @return Ip socket address
     */
    public InetSocketAddress getIpAddress() {
        return mIpAddress;
    }

    /**
     * Sets the Ip Socket Address
     * @param ipAddress the Ip Socket Address
     */
    public void setIpAddress(InetSocketAddress ipAddress) {
        this.mIpAddress = ipAddress;
    }
}
