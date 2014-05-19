/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 5/16/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.communication;

import android.util.Log;

import com.ifit.sparky.fecp.error.ErrorReporting;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class TcpComm implements CommInterface {

    private Socket mSocket;
    private final int BUFF_SIZE = 64;
    private DataOutputStream mToMachine;
    private InputStream mFromMachine;
    private String mIpAddress;
    private int mPort;
    private int mSendTimeout;

    private LinkedList<DeviceConnectionListener> mConnectionListeners;

    public TcpComm(String ipAddress, int port, int defaultTimeout)
    {
        this.mIpAddress = ipAddress;
        this.mPort = port;
        this.mSendTimeout = defaultTimeout;
        if(this.mConnectionListeners == null)
        {
            this.mConnectionListeners = new LinkedList<DeviceConnectionListener>();
        }
    }

    /**
     * Initializes the connection to the communication items.
     */
    @Override
    public void initializeCommConnection() {
        //makes a connection across port
        try {
            this.mSocket = new Socket(this.mIpAddress, this.mPort);
            this.mSocket.setPerformancePreferences(1, 2, 0);//Latency is the highest priority
            //then connection speed, then bandwidth is lowest.
            this.mToMachine = new DataOutputStream(this.mSocket.getOutputStream());
            this.mFromMachine = this.mSocket.getInputStream();

            if(this.mSocket.isConnected())
            for (DeviceConnectionListener listener : this.mConnectionListeners) {
                listener.onDeviceConnected();
            }
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles multiple listeners so we can notify both ifit and the fecp controller.
     *
     * @param listener the listener for the callbacks
     */
    @Override
    public void addConnectionListener(DeviceConnectionListener listener) {
        this.mConnectionListeners.add(listener);
    }

    /**
     * Removes all the Connection listeners,
     */
    @Override
    public void clearConnectionListener() {
        this.mConnectionListeners.clear();
    }

    /**
     * sends the command and waits for the reply to handle the buffer
     *
     * @param buff the command buffer to send
     * @return a buffer with the message 0 if failed
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff) {

        this.sendAndReceiveCmd(buff, this.mSendTimeout);

        return null;
    }

    /**
     * Send and receive with a timeout
     *
     * @param buff    the buffer to send
     * @param timeout the max time you want to take till it is send
     * @return the buffer from the device 0 in the first byte for failed
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff, int timeout) {

        byte[] data;
        ByteBuffer resultBuffer;
        data = new byte[BUFF_SIZE];//shouldn't ever be longer
        int bytesRead = 0;
        resultBuffer = ByteBuffer.allocate(BUFF_SIZE);

        if(!this.mSocket.isConnected())
        {
            for (DeviceConnectionListener listener : this.mConnectionListeners) {
                listener.onDeviceDisconnected();
            }
        }
        if(this.mSocket.isClosed() || !this.mSocket.isConnected())
        {
            //attempt to reconnect
            this.initializeCommConnection();

        }
        buff.position(0);
        try {
            this.mSocket.setSoTimeout(timeout);
            this.mFromMachine.reset();
            //send data
            this.mToMachine.write(buff.array());

            //read from server
            //read the first 2 bytes
            bytesRead = this.mFromMachine.read(data, 0, BUFF_SIZE);//read the length
            resultBuffer.put(data);
            if(bytesRead == -1)
            {
                Log.d("BAD_TCP_READ", "invalid Read");
                return resultBuffer;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultBuffer;
    }

    /**
     * Needs to report error with the err
     *
     * @param errReporterCallBack needs to be called to handle errors
     */
    @Override
    public void setupErrorReporting(ErrorReporting errReporterCallBack) {
        //currently not implemented
    }

    /**
     * Used to determined if we should attempt to reconnect to the machine, or if nothing is going on.
     *
     * @param active true for communicating, false for no communication.
     */
    @Override
    public void setCommActive(boolean active) {
        //currently has no impact on communication,
    }
}
