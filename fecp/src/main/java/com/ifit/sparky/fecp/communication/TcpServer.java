/**
 * This will allow quick access to data directly from the device.
 * @author Levi.Balling
 * @date 5/23/2014
 * @version 1
 * This will handle data with as little footprint on both apps, and the communication interface.
 */
package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.FecpCmdHandleInterface;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.RawDataCmd;
import com.ifit.sparky.fecp.interpreter.status.RawDataSts;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TcpServer implements CommInterface.DeviceConnectionListener {

    private ServerSocket mServerSock;
    private Thread mServerThread;
    private int mServerPort = 8080;//default
    private FecpCmdHandleInterface mCmdHandler;

    public TcpServer(FecpCmdHandleInterface cmdHandler)
    {
        this.mCmdHandler = cmdHandler;
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
        private BufferedInputStream inFromClient;
        private DataOutputStream mToClient;
        private FecpCommand mRawFecpCmd;
        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.inFromClient = new BufferedInputStream(this.clientSocket.getInputStream());
                this.mToClient = new DataOutputStream(this.clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] data = new byte[64];

                    this.inFromClient.read(data, 0, 64);
                    try {
                        //created the command
                        this.mRawFecpCmd = new FecpCommand(new RawDataCmd(ByteBuffer.wrap(data)),this);
                        //send to FecpCmdHandler
                        mCmdHandler.addFecpCommand(this.mRawFecpCmd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            buffer.position(0);
            try {
                this.mToClient.write(buffer.array());//write the reply back to the server
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
