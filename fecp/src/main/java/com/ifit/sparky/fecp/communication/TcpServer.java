/**
 * This will allow quick access to data directly from the device.
 * @author Levi.Balling
 * @date 5/23/2014
 * @version 1
 * This will handle data with as little footprint on both apps, and the communication interface.
 */
package com.ifit.sparky.fecp.communication;

import android.util.Log;

import com.ifit.sparky.fecp.FecpCmdHandleInterface;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.SystemConfiguration;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.RawDataCmd;
import com.ifit.sparky.fecp.interpreter.status.RawDataSts;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TcpServer implements CommInterface.DeviceConnectionListener {

    private ServerSocket mServerSock;
    private Thread mServerThread;
    private int mServerPort = 8090;//default
    private FecpCmdHandleInterface mCmdHandler;
    private SystemDevice mSysDev;
    private boolean mCommLogging = false;
    private final int COMM_THREAD_PRIORITY = -7;

    public TcpServer(FecpCmdHandleInterface cmdHandler, SystemDevice sysDev)
    {
        this.mCmdHandler = cmdHandler;
        this.mSysDev = sysDev;
        this.mServerThread = new Thread(new ServerThread());
        this.mCmdHandler.getCommController().addConnectionListener(this);

    }

    public TcpServer(FecpCmdHandleInterface cmdHandler, int portNumber)
    {
        this.mServerPort = portNumber;
        this.mCmdHandler = cmdHandler;
        this.mServerThread = new Thread(new ServerThread());
        this.mCmdHandler.getCommController().addConnectionListener(this);
    }

    /**
     * Starts the TCP server Thread if it hasn't already started
     * @return true it started successfully, false if it is already running or
     */
    public boolean startServer()
    {
        if(this.mServerThread.isAlive())
        {
            return false;
        }
        this.mServerThread.start();

        return true;
    }

    /**
     * Stops the TCP server Thread
     * @return true if stopped successfully, false if failed to disconnect
     */
    public boolean stopServer()
    {
        if(this.mServerThread.isAlive())
        {
            try {
                this.mServerSock.close();//close the socket
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        return false;
    }

    @Override
    public void onDeviceConnected() {
        //don't start the server waiting for validation on the machine type
    }

    @Override
    public void onDeviceDisconnected() {
        //stop the server thread
        try {
            this.mServerSock.close();//can't have a connection if there is no device
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerThread implements Runnable{

        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            Socket socket = null;
            try {
                mServerSock = new ServerSocket(mServerPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = mServerSock.accept();
                    socket.setKeepAlive(false);
                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class CommunicationThread implements Runnable, OnCommandReceivedListener {


        private Socket clientSocket;
        private BufferedOutputStream mToClient;

        private FecpCommand mRawFecpCmd;
        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;
            try {
                this.clientSocket.setSendBufferSize(1024);
                this.clientSocket.setReceiveBufferSize(1024);
                this.clientSocket.setTcpNoDelay(true);//disable Nagle's Algorithm
                this.mToClient = new BufferedOutputStream(this.clientSocket.getOutputStream());
                this.clientSocket.setSoTimeout(5000);//timeout after 5 secs

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            int runningCheck = 0;//if it has been to long close socket
            long startTime;
            long endTime;
            //increase the thread priority to for faster response
            int threadId = android.os.Process.myTid();
            Log.d("SERVER", "previous Thread Priority=" + android.os.Process.getThreadPriority(threadId));
            android.os.Process.setThreadPriority(COMM_THREAD_PRIORITY);
            Log.d("SERVER", "post Thread Priority=" + android.os.Process.getThreadPriority(threadId));

            while (!Thread.currentThread().isInterrupted()) {

                startTime = System.currentTimeMillis();
                try {

                    byte[] data = new byte[64];
                    int readCount = this.clientSocket.getInputStream().read(data, 0, 64);
                    if(readCount == -1)
                    {
                        runningCheck++;
                    }
                    else
                    {
                        runningCheck = 0;
                    }
                    if(runningCheck > 50)
                    {
                        //system is disconnected
                        this.clientSocket.close();
                        this.mToClient.close();
                        return;
                    }

                    if(mCommLogging) {
                        String result = "raw client " + this.clientSocket.getInetAddress().getHostAddress() + ":" + this.clientSocket.getPort() + "data=\n";
                        int counter = 0;
                        int length = data[1];

                        for (byte b : data) {
                            if (counter < length) {
                                result += "[" + counter++ + "]=" + b + "\n";
                            }
                        }
                        Log.d("IN_DATA", result);
                    }
                    this.handleRequest(data);

                    if(mCommLogging) {
                        //log data that is received
                        endTime = System.currentTimeMillis();
                        Log.d("SERVER_SEND_TIME", "Server responseTime:" + (endTime - startTime) + "mSec");
                    }
            } catch (Exception e) {
                    endTime = System.currentTimeMillis();
                    Log.d("NO_COMM", "Nothing to receive, Time was:" + (endTime - startTime) + "mSec" );
//                e.printStackTrace();
            }
        }
    }

        private void handleRequest(byte[] buff)
        {

            byte[] data = buff;
            try {
                //created the command
                //check what the command is, and my current master configuration

                if(data[0] == (byte)0x02 && data[2] == (byte)0x82)//addressing the Main device for sys info
                {
                    //read the rest of the data
                    //int readCount = this.inFromClient.read(data, 3, 61);//read the rest of the data in the command
                    //return System Info command with appropriate system configuration
                    ByteBuffer reply = mSysDev.getSysInfoSts().getReplyBuffer();

                    reply.position(0);
//                    reply.put(0, (byte) 0x03);//portal device
                    if(mSysDev.getConfig() == SystemConfiguration.SLAVE || mSysDev.getConfig() == SystemConfiguration.PORTAL_TO_SLAVE) {
                        reply.put(4, (byte)SystemConfiguration.PORTAL_TO_SLAVE.ordinal());//portal device
                    }
                    else if(mSysDev.getConfig() == SystemConfiguration.MASTER || mSysDev.getConfig() == SystemConfiguration.MULTI_MASTER) {
                        reply.put(4, (byte)SystemConfiguration.PORTAL_TO_MASTER.ordinal());//portal device
                    }

                    byte length = reply.get(1);
                    reply.position(length-1);
                    reply.put(Command.getCheckSum(reply));
                    reply.position(0);
                    this.mToClient.write(reply.array());
                    this.mToClient.flush();
                    //they then use Listen command and single not repeat commands

                }
                else if (data[0] == (byte)0x03 && data[2] == (byte)0x01)//get System Device Command
                {
                    //reply with specific command
//                    int readCount = this.inFromClient.read(data, 3, 61);//read the rest of the data in the command
                   // int readCount = this.clientSocket.getInputStream().read(data, 3, 61);

                    this.mToClient.write(0x03);
                    mSysDev.writeObject(this.mToClient);
                    this.mToClient.flush();
                    //this.mToClient.write(dataObjectArray);//write object to client
                }
                else {

                    int readCount = 0;//read the rest of the data in the command
//                    try {
//                        //readCount = this.inFromClient.read(data, 3, 61);
//                        //readCount = this.clientSocket.getInputStream().read(data, 3, 61);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        ByteBuffer errBuff = ByteBuffer.allocate(64);
//                        errBuff.order(ByteOrder.LITTLE_ENDIAN);
//                        String errMessage = "Error with the message 0 data was send";
//                        errBuff.put(errMessage.getBytes());
//                        errBuff.position(0);
//                        this.mToClient.write(errBuff.array(), 0, 64);//error with message reply
//                        this.mToClient.flush();
//                    }
                    if(readCount == -1 || (data[0] == 0 && data[1] == 0))
                    {
                        //read to the end of the input stream
//                        while(this.inFromClient.available()>0)
//                        {
//                            this.inFromClient.read();
//
//
//                        }
                        ByteBuffer errBuff = ByteBuffer.allocate(64);
                        errBuff.order(ByteOrder.LITTLE_ENDIAN);
                        String errMessage = "Error with the message 0 data was send";
                        errBuff.put(errMessage.getBytes());
                        errBuff.position(0);
//                        this.mToClient.write(errBuff.array(), 0, 64);//error with message reply
//                        this.mToClient.flush();
                        return;
                    }
                    this.mRawFecpCmd = new FecpCommand(new RawDataCmd(ByteBuffer.wrap(data)), this);

                    //set to be a higher priority
                    //check if it is a master command
                    //send to FecpCmdHandler
                    mCmdHandler.addFecpCommand(this.mRawFecpCmd);
                }

                //clear everything from in buffer
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Handles the reply from the device
         *
         * @param cmd the command that was sent.
         */
        @Override
        public void onCommandReceived(Command cmd) {

            ByteBuffer buffer = ((RawDataSts)cmd.getStatus()).getRawBuffer();
            if(buffer == null)
            {
                return;//nothing to send invalid data
            }
            buffer.position(0);
            try {
                this.mToClient.write(buffer.array());//write the reply back to the server
                this.mToClient.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
