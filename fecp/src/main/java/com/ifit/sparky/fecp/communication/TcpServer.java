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
        //private InputStream inFromClient;
//        private BufferedInputStream inFromClient;
        //private DataOutputStream mToClient;
        private BufferedOutputStream mToClient;

        private FecpCommand mRawFecpCmd;
        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;

            try {
                this.clientSocket.setSendBufferSize(1024);
                this.clientSocket.setReceiveBufferSize(1024);
                //this.inFromClient = new BufferedInputStream(this.clientSocket.getInputStream());
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
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    startTime = System.currentTimeMillis();
                    if(true|| this.clientSocket.getInputStream().available()!=0)
                    {

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
                        //this.inFromClient.read(data);

                        String result = "raw client " + this.clientSocket.getInetAddress().getHostAddress() +":" + this.clientSocket.getPort() +"data=\n";
                        int counter = 0;
                        int length = data[1];

                        for (byte b : data) {
                            if(counter < length )
                            {
                                result += "[" + counter++ + "]=" + b + "\n";
                            }
                        }
                        Log.d("IN_DATA", result);
                        this.handleRequest(data);

//                        this.clientSocket.getOutputStream().write(helloWorld.getBytes());
//
//                        this.clientSocket.getOutputStream().write(data);
                    }
                    //log data that is received
                    long endTime = System.currentTimeMillis();

                    Log.d("SERVER_SEND_TIME","Server Full:" + (endTime -startTime ));
            } catch (Exception e) {
                    Log.d("NO_COMM", "Nothing to receive");
//                e.printStackTrace();
            }
        }
    }


//        public void run2() {
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    long startTime = System.currentTimeMillis();
//                    long medTime = 0;
//                    byte[] data = new byte[64];
//
//
////                    this.inFromClient.read(data, 0, 3);//read the first 3 bytes
//                    this.clientSocket.getInputStream().read(data, 0, 3);
//                    try {
//                        //created the command
//                        //check what the command is, and my current master configuration
//
//                        if(data[0] == (byte)0x02 && data[2] == (byte)0x82)//addressing the Main device for sys info
//                        {
//                            //read the rest of the data
////                            int readCount = this.inFromClient.read(data, 3, 61);//read the rest of the data in the command
//                            int readCount = this.clientSocket.getInputStream().read(data, 3, 61);
//                            //return System Info command with appropriate system configuration
//                            ByteBuffer reply = mSysDev.getSysInfoSts().getReplyBuffer();
//
//                            reply.position(0);
//                            reply.put(0, (byte) 0x03);//portal device
//                            if(mSysDev.getConfig() == SystemConfiguration.SLAVE || mSysDev.getConfig() == SystemConfiguration.PORTAL_TO_SLAVE) {
//                                reply.put(4, (byte)SystemConfiguration.PORTAL_TO_SLAVE.ordinal());//portal device
//                            }
//                            else if(mSysDev.getConfig() == SystemConfiguration.MASTER || mSysDev.getConfig() == SystemConfiguration.MULTI_MASTER) {
//                                reply.put(4, (byte)SystemConfiguration.PORTAL_TO_MASTER.ordinal());//portal device
//                            }
//
//                            byte length = reply.get(1);
//                            reply.position(length-1);
//                            reply.put(Command.getCheckSum(reply));
//                            reply.position(0);
//                            this.mToClient.write(reply.array());
//                            //they then use Listen command and single not repeat commands
//
//                        }
//                        else if (data[0] == (byte)0x03 && data[2] == (byte)0x01)//get System Device Command
//                        {
//                            medTime = System.currentTimeMillis();
//                            //reply with specific command
//                            int readCount = this.inFromClient.read(data, 3, 61);//read the rest of the data in the command
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            ObjectOutput objectOutput = null;
//                            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
//
//                            mSysDev.writeObject((ObjectOutputStream) objectOutput);
//                            byte[] dataObjectArray = byteArrayOutputStream.toByteArray();
//
//                            this.mToClient.write(0x03);//size of the object may vary greatly
//                            ByteBuffer b = ByteBuffer.allocate(5);
//                            b.order(ByteOrder.LITTLE_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
//                            b.put((byte)0x03);
//                            b.putInt(dataObjectArray.length);
//
//                            //this.mToClient.writeInt(dataObjectArray.length);//number of bytes coming up
//                            this.mToClient.write(b.array());
//                            this.mToClient.write(dataObjectArray);//write object to client
//                        }
//                        else {
//
//                            int readCount = 0;//read the rest of the data in the command
//                            try {
//                                readCount = this.inFromClient.read(data, 3, 61);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                ByteBuffer buff = ByteBuffer.allocate(64);
//                                buff.order(ByteOrder.LITTLE_ENDIAN);
//                                String errMessage = "Error with the message 0 data was send";
//                                buff.put(errMessage.getBytes());
//                                buff.position(0);
//                                this.mToClient.write(buff.array(), 0, 64);//error with message reply
//                            }
//                            if(readCount == -1 || (data[0] == 0 && data[1] == 0))
//                            {
//                                //read to the end of the input stream
//                                while(this.inFromClient.available()>0)
//                                {
//                                    this.inFromClient.read();
//
//
//                                }
//                                ByteBuffer buff = ByteBuffer.allocate(64);
//                                buff.order(ByteOrder.LITTLE_ENDIAN);
//                                String errMessage = "Error with the message 0 data was send";
//                                buff.put(errMessage.getBytes());
//                                buff.position(0);
//                                this.mToClient.write(buff.array(), 0, 64);//error with message reply
//                                return;
//                            }
//                            this.mRawFecpCmd = new FecpCommand(new RawDataCmd(ByteBuffer.wrap(data)), this);
//
//                            //set to be a higher priority
//                            //check if it is a master command
//                            //send to FecpCmdHandler
//                            mCmdHandler.addFecpCommand(this.mRawFecpCmd);
//                        }
//
//                        long endTime = System.currentTimeMillis();
//                        if(medTime == 0)
//                        {
//                            medTime = endTime;
//                        }
//                        Log.d("SERVER_SEND_TIME","Server Full:" + (endTime -startTime ) + "mSec Part:" + (endTime - medTime));
//                        //clear everything from in buffer
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

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
