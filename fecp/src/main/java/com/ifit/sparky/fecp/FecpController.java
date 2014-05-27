/**
 * This is Master and Commander for communication to fitness equipment.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * This controller will handle all the different aspects of the communication to the system.
 */
package com.ifit.sparky.fecp;

import android.util.Log;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.TcpServer;
import com.ifit.sparky.fecp.error.ErrorCntrl;
import com.ifit.sparky.fecp.error.ErrorEventListener;
import com.ifit.sparky.fecp.error.ErrorReporting;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.PortalDeviceCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.PortalDeviceSts;
import com.ifit.sparky.fecp.testingUtil.CmdInterceptor;

import java.nio.ByteBuffer;

public class FecpController implements ErrorReporting, CommInterface.DeviceConnectionListener {
    //Fecp System Version number
    private final int VERSION = 1;
    private CommType mCommType;
    protected SystemStatusCallback statusCallback;
    protected SystemDevice mSysDev;
    protected boolean mIsConnected;
    protected CommInterface mCommController;
    protected FecpCmdHandleInterface mCmdHandleInterface;
    protected ErrorCntrl mSysErrorControl;
    private TcpServer mTcpServer;

    /**
     * This is for Fecp connections that don't req
     * @param type Communication type
     * @param callback callback for the system
     * @throws Exception
     */
    public FecpController(CommType type, SystemStatusCallback callback) throws Exception {

        if(callback == null)
        {
            throw new Exception("SystemStatusCallback callback is null, Can't be null");
        }
        this.mCommType = type;
        this.statusCallback = callback;
        this.mSysDev = new SystemDevice(DeviceId.MAIN);//starts out as main
        this.mIsConnected = false;
        this.mSysErrorControl = new ErrorCntrl(this);
    }

    /**
     *
     * @throws Exception
     */
    public void initializeConnection() throws Exception{

        this.mCommController.addConnectionListener(this);
        this.mCommController.initializeCommConnection();
    }

    /**
     * Initializes the connection and sets up the communication
     *
     * @param listener this listens for changes in the connection
     */
    public void initializeConnection(CommInterface.DeviceConnectionListener listener) throws Exception {

        this.mCommController.addConnectionListener(this);

        if(listener != null) {

            this.mCommController.addConnectionListener(listener);
        }
        this.mCommController.initializeCommConnection();
    }

    /**
     * Gets the version of the Fecp Controller
     *
     * @return the version
     */
    public int getVersion() {
        return this.VERSION;
    }

    /**
     * Gets the Communication type
     *
     * @return the communication type
     */
    public CommType getCommType() {
        return this.mCommType;
    }

    /**
     * Gets the Main System Device, or the head of the tree
     *
     * @return the System Device
     */
    public SystemDevice getSysDev() {

        return this.mSysDev;
    }

    /**
     * Gets the connection status
     *
     * @return the connection status true for connected
     */
    public boolean getIsConnected() {
        return this.mIsConnected;
    }

    private void getSystem() throws Exception
    {
        if(this.mCommType == CommType.TESTING_COMM) {

        }
        else
        {
            this.mSysDev = new SystemDevice(this.mCommController);
        }

        if(this.mSysDev.getConfig() == SystemConfiguration.PORTAL_TO_MASTER || this.mSysDev.getConfig() == SystemConfiguration.PORTAL_TO_SLAVE)
        {
            //communication is different update through the latest Command data
            //create a Portal Listen command
            //fetch the System Device object
            Command portalDeviceCmd = new PortalDeviceCmd(DeviceId.PORTAL);
            portalDeviceCmd.getStatus().handleStsMsg(this.mCommController.sendAndReceiveCmd(portalDeviceCmd.getCmdMsg()));
            SystemDevice newSysData = ((PortalDeviceSts)portalDeviceCmd.getStatus()).getmSysDev();

            this.mSysDev = newSysData;//latest version of the System
            //copy data
        }

        if(this.mSysDev.getInfo().getDevId() == DeviceId.NONE)
        {
            return;
        }
        this.mCmdHandleInterface = new FecpCmdHandler(this.mCommController, this.mSysDev);

        this.mTcpServer = new TcpServer(this.mCmdHandleInterface, this.mSysDev);//currently accepting connections
        //on port 8080.

        this.mCommController.setupErrorReporting(this.mSysErrorControl);
    }

    /**
     * Adds a command to send to the device
     *
     * @param cmd the command to send to the device
     */
    public void addCmd(FecpCommand cmd) throws Exception {
        this.mCmdHandleInterface.addFecpCommand(cmd);
    }

    /**
     * Removes a command from the list to send
     *
     * @param cmd the command you wish to remove
     */
    public void removeCmd(FecpCommand cmd) {
        this.mCmdHandleInterface.removeFecpCommand(cmd);
    }

    /**
     * Removes all of the commands with the same device Id and Command Id
     *
     * @param devId the device you wish to remove the command from
     * @param cmdId the command from the device that you wish to remove.
     */
    public void removeCmd(DeviceId devId, CommandId cmdId) {
        this.mCmdHandleInterface.removeFecpCommand(devId, cmdId);
    }

    /**
     * Sends the buffer that matches the online profile for Error messages
     * Don't use if you don't now what it does
     * @param buffer buffer that is pointing to the start of the message.
     */
    @Override
    public void sendErrorObject(ByteBuffer buffer) {
        this.mSysErrorControl.sendErrorObject(buffer);
    }

    /**
     * Adds a listener to the system so we can determine if there are any errors
     *
     * @param errListener the listener that will be called when an error occurs
     */
    @Override
    public void addOnErrorEventListener(ErrorEventListener errListener) {
        this.mSysErrorControl.addOnErrorEventListener(errListener);
    }

    /**
     * Removes the listener from the system. so that it won't be called anymore
     *
     * @param errListener the listener that you wish to remove
     */
    @Override
    public void removeOnErrorEventListener(ErrorEventListener errListener) {
        this.mSysErrorControl.removeOnErrorEventListener(errListener);
    }

    /**
     * Clears the Listers from the system
     */
    @Override
    public void clearOnErrorEventListener() {
        this.mSysErrorControl.clearOnErrorEventListener();

    }

    /**
     * Adds an interceptor to the Fecp Controller, redirecting all commands to the CmdInterceptor.
     * This command is meant for testing Ifit code, not the fecp controller or the brain board.
     * @param interceptor interceptor to handle all commands going to the device.
     */
    public void addInterceptor(CmdInterceptor interceptor)
    {
        this.mCmdHandleInterface.addInterceptor(interceptor);
        //this will get the data from fecp controller that the interceptor needs
    }

    /**
     * This is a loophole for Testing Ifits code. It is apart of the interceptor process.
     * @param device The system that ifit will be communicating with.
     */
    public void testingSetSystemDevice(SystemDevice device)
    {
        this.mSysDev = device;
    }

    public void clearConnectionListener() {
        mCommController.clearConnectionListener();
    }

    @Override
    public void onDeviceConnected() {
        //search for the device
        try {

            if(this.mSysDev.getInfo().getDevId() == DeviceId.MAIN) {
                this.getSystem();
                this.mCommController.setCommActive(false);
            }

            if(this.mSysDev.getInfo().getDevId() != DeviceId.NONE)
            {
                this.statusCallback.systemDeviceConnected(this.mSysDev);
                if(this.mSysDev.getConfig() == SystemConfiguration.MASTER || this.mSysDev.getConfig() == SystemConfiguration.MULTI_MASTER )
                {
                    this.mTcpServer.startServer();//start the server
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("Get System Failed", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onDeviceDisconnected() {
        //nothing to do
    }
}
