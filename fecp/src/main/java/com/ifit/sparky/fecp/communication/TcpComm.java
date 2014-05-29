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
import com.ifit.sparky.fecp.interpreter.command.GetSysInfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpComm implements CommInterface {

    private Socket mSocket;
    private final int BUFF_SIZE = 64;
    private DataOutputStream mToMachine;
    private InputStream mFromMachine;
    private String mIpAddress;
    private int mPort;
    private int mSendTimeout;
    private ScanSystemListener mScanListener;//returns with list of devices or none

    private CopyOnWriteArrayList<DeviceConnectionListener> mConnectionListeners;

    public TcpComm(String ipAddress, int port, int defaultTimeout)
    {
        this.mIpAddress = ipAddress;
        this.mPort = port;
        this.mSendTimeout = defaultTimeout;
        if(this.mConnectionListeners == null)
        {
            this.mConnectionListeners = new CopyOnWriteArrayList<DeviceConnectionListener>();
        }
    }

    /**
     * Initializes the connection to the communication items.
     */
    @Override
    public void initializeCommConnection() {
        //makes a connection across port
        try {
            this.mSocket = new Socket();
            this.mSocket.connect(new InetSocketAddress(this.mIpAddress, this.mPort), this.mSendTimeout);
//            this.mSocket = new Socket(this.mIpAddress, this.mPort);
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

        return this.sendAndReceiveCmd(buff, this.mSendTimeout);
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
            //this.mSocket.setSoTimeout(timeout);
//            this.mFromMachine.reset();
            //copy Data to a 64 byte array
            buff.get(data,0, buff.capacity());//copy all of the elements available

            //send data
            try {

                this.mToMachine.write(data);
                Arrays.fill(data, (byte) 0);
                Thread.sleep(5);

                //read from server
                //read the first byte
                bytesRead = this.mFromMachine.read(data, 0, 1);//read the device
                if(bytesRead == -1)
                {
                    Log.d("BAD_TCP_READ", "invalid Read");
                    return resultBuffer;
                }
                buff.position(0);
                if(data[0] == (byte)0x03 && buff.get() == (byte)0x03)//custom handle for special objects.
                {
                    //Portal Listen command prep for receiving System Object
                    //read the next 4 bytes
                    bytesRead = this.mFromMachine.read(data, 1, 4);//read the device
                    ByteBuffer tempSizeBuff = ByteBuffer.allocate(4);
                    tempSizeBuff.order(ByteOrder.BIG_ENDIAN);
                    tempSizeBuff.put(data,1,4);
                    tempSizeBuff.position(0);
                    int dataSize = tempSizeBuff.getInt();
                    byte[] sysObjectData = new byte[dataSize];
                    int timeoutCounter = 0;//try for ten times
                    bytesRead = 0;
                    while(bytesRead < dataSize && timeoutCounter < 10) {
                        int readCount = this.mFromMachine.read(sysObjectData, bytesRead, dataSize-bytesRead);
                        if(readCount != -1)
                        {
                            bytesRead += readCount;
                        }

                        if(bytesRead != dataSize)
                        {
                            Thread.sleep(5);
                        }
                        timeoutCounter++;
                    }



                    if(bytesRead != dataSize)
                    {
                        Log.d("BAD_TCP_READ", "invalid Read Size expected " + dataSize + " actual " + bytesRead);
                        return resultBuffer;
                    }
                    resultBuffer = ByteBuffer.allocate(dataSize);
                    resultBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    resultBuffer.position(0);
                    resultBuffer.put(sysObjectData, 0, dataSize);
                    if(bytesRead == -1)
                    {
                        Log.d("BAD_TCP_READ", "invalid Read");
                        return resultBuffer;

                    }


                    return resultBuffer;


                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //read the first 2 bytes
            bytesRead = this.mFromMachine.read(data, 1, BUFF_SIZE-1);//read the length
            resultBuffer.put(data);


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

    /**
     * This allows the user to scan for all of the different devices, when finished scanning it will
     * Call the listener to allow them to select with
     *
     * @param listener listener to be called after scanning is complete.
     */
    @Override
    public void scanForSystems(ScanSystemListener listener) {

        //scans all of the different Ip addresses for any valid ones, then scans the default port for any
        //this is a multi threaded opperation
        this.mScanListener = listener;
        Thread scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //gets all the ip address available in this network.
                InetAddress currentIpAddress = getCurrentIpAddress();
                if(currentIpAddress == null)
                {
                    mScanListener.onScanFinish(new ArrayList<ConnectionDevice>());//return an empty array list
                    return;
                }

                byte[] rawIpAddress = currentIpAddress.getAddress();
                //get starter string
                if(rawIpAddress.length < 3)
                {
                    return;
                }
                String maskedIpStr = rawIpAddress[0] +"." + rawIpAddress[1] +"." + rawIpAddress[2] +".";
                //generate a list ip address besides this ip address to check is valid ip address

                int excludeNum = rawIpAddress[3];

                ArrayList<IpScanner> ipScanners = new ArrayList<IpScanner>();
                ArrayList<Thread> scanThreads = new ArrayList<Thread>();

                //UNLEASE THE HOUNDS
                for(int i = 0; i < Byte.MAX_VALUE; i++)
                {
                    //create ip address string

                    if(i != excludeNum) {

                        IpScanner scanner = new IpScanner(new InetSocketAddress(maskedIpStr + i, 8090));
                        ipScanners.add(scanner);
                        Thread runThread = new Thread(scanner);
                        runThread.start();
                        scanThreads.add(runThread);

                    }
                }

                // CALL THE DOGS BACK, THEY HAVE GUNS
                try {
                    for (Thread thread : scanThreads) {
                        thread.join();

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mScanListener.onScanFinish(new ArrayList<ConnectionDevice>());//return an empty array list
                }

                //all the dogs are back and safe.
                //now we can send data to the valid addresses if they exist
                ArrayList<ConnectionDevice> possibleDevices = new ArrayList<ConnectionDevice>();
                for (IpScanner scanner : ipScanners) {
                    if(scanner.isValidDevice)
                    {
                        possibleDevices.add(scanner.mDev);
                    }
                }
                mScanListener.onScanFinish(possibleDevices);
            }
        });

        scanThread.start();

    }

    private InetAddress getCurrentIpAddress()
    {
        //gets all the ip address available in this network.
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return addr;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("No IP address", ex.getMessage());
                ex.printStackTrace();
        }
        return null;
    }

    private class IpScanner implements Runnable{

        private TcpConnectionDevice mDev;
        private boolean isValidDevice = false;
        public IpScanner(InetSocketAddress ipAddress)
        {
            this.mDev = new TcpConnectionDevice(ipAddress);
        }
        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            //uses the given Ip address to check if it is available
            try {

                //todo try with no ip checking
                if(this.mDev.mIpAddress.getAddress().isReachable(1000))
                {
                    //try to see if port is available
                    Socket testSocket = new Socket();
                    DataOutputStream sendStream;
                    InputStream readStream;
                    try {

                        testSocket.connect(this.mDev.mIpAddress, 3000);//start off with a 3 second timeout

                        //send message to get the System Info
                        try {
                            GetSysInfoCmd tempCmd = new GetSysInfoCmd(DeviceId.MAIN);
                            tempCmd.getCmdMsg();
                            sendStream = new DataOutputStream(testSocket.getOutputStream());
                            readStream = testSocket.getInputStream();

                            byte[] data;
                            ByteBuffer resultBuffer;
                            data = new byte[BUFF_SIZE];//shouldn't ever be longer
                            int bytesRead = 0;
                            resultBuffer = ByteBuffer.allocate(BUFF_SIZE);

                            ByteBuffer buff = tempCmd.getCmdMsg();
                            buff.position(0);
                            //copy Data to a 64 byte array
                            buff.get(data,0, buff.capacity());//copy all of the elements available

                            sendStream.write(data);
                            Arrays.fill(data, (byte) 0);
                            Thread.sleep(5);

                            bytesRead = readStream.read(data, 0, data.length);//read the device

                            resultBuffer = ByteBuffer.allocate(data.length);
                            resultBuffer.order(ByteOrder.LITTLE_ENDIAN);
                            resultBuffer.position(0);
                            resultBuffer.put(data, 0, data.length);

                            if(bytesRead != -1) {

                                GetSysInfoSts sysInfoSts =  (GetSysInfoSts)tempCmd.getStatus();//.handleStsMsg(resultBuffer);
                                sysInfoSts.handleStsMsg(resultBuffer);
                                if(sysInfoSts.getStsId() == StatusId.DONE)//valid option to connect to
                                {
                                    this.isValidDevice = true;
                                    this.mDev.setSysInfoVal(sysInfoSts);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {

                        e.printStackTrace();
                    }finally {
                        try {
                            testSocket.close();
                        } catch (IOException closeEx) {
                            Log.e("failed to Close", closeEx.getMessage());
                            closeEx.printStackTrace();
                        }
                    }
                }
            } catch (IOException ex) {

                ex.printStackTrace();
            }


        }
    }
}
