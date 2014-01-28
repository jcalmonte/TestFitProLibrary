/**
 * This is Master and Commander for communication to fitness equipment.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * This controller will handle all the different aspects of the communication to the system.
 */
package com.ifit.sparky.fecp;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommReply;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.UsbComm;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.nio.ByteBuffer;

public class FecpController{
    //Fecp System Version number
    private final int VERSION = 1;
    private CommType mCommType;
    private SystemStatusCallback statusCallback;
    private SystemDevice mSysDev;
    private boolean mIsConnected;
    private Context mContext;
    private Intent mIntent;
    private CommInterface mCommController;
    private Thread mCommunicationThread;//this thread is to handle all the communications
    private FecpCmdHandleInterface mCmdHandleInterface;

    /**
     * Sets up the controller, and all the facets dealing with the controller
     * @param context the application context
     * @param type the type of communication
     * @param callback the callback for connection and disconnections
     * @throws Exception if the device is invalid
     */
    public FecpController(Context context, Intent intent, CommType type, SystemStatusCallback callback) throws Exception
    {
        this.mCommType = type;
        this.statusCallback = callback;
        this.mSysDev = new SystemDevice(DeviceId.MAIN);//starts out as main
        this.mIsConnected = false;
        this.mContext = context;
        this.mIntent = intent;

    }

    /**
     * Initializes the connection and sets up the communication
     * @param type the type of handling the system should have
     * @return the system device
     */
    public SystemDevice initializeConnection(CmdHandlerType type) throws Exception
    {
        //add as we add support for these
        if(this.mCommType == CommType.USB_COMMUNICATION)
        {
            this.mCommController = new UsbComm(this.mContext, this.mIntent);
            if(type == CmdHandlerType.FIFO_PRIORITY)
            {
                //two references to the same object with different responsibilities
                this.mCmdHandleInterface = new FecpFifoCmdHandler(this.mCommController);
                this.mCommunicationThread = (FecpFifoCmdHandler)this.mCmdHandleInterface;
            }
            else if(type == CmdHandlerType.CMD_TYPE_PRIORITY)
            {
                //todo make the CmdTypeCommand handler
            }
            this.mCommController.setStsHandler((FecpFifoCmdHandler)this.mCmdHandleInterface);
            this.initializeSystemDevice();
            this.mCommunicationThread.start();//start running the system
        }

        //send command to get the system's info
        try
        {
            this.mCommController.sendCmdBuffer(this.mSysDev.getCommand(CommandId.GET_INFO).getCmdMsg());
        }
        catch (Exception ex)
        {

        }
        //only after it is complete handle the data back

        return this.mSysDev;
    }

    /**
     * Gets the version of the Fecp Controller
     * @return the version
     */
    public int getVersion() {
        return this.VERSION;
    }

    /**
     * Gets the Communication type
     * @return the communication type
     */
    public CommType getCommType() {
        return this.mCommType;
    }

    /**
     * Gets the Main System Device, or the head of the tree
     * @return the System Device
     */
    public SystemDevice getSysDev() {
        return this.mSysDev;
    }

    /**
     * Gets the connection status
     * @return the connection status true for connected
     */
    public boolean getIsConnected() {
        return this.mIsConnected;
    }

    /**
     * Addes a command to send to the device
     * @param cmd the command to send to the device
     */
    public void addCmd(FecpCommand cmd)
    {
        this.mCmdHandleInterface.addFecpCommand(cmd);
    }

    private void initializeSystemDevice() throws Exception
    {
        //send command to get info to main device

        Command cmd = new InfoCmd(DeviceId.MAIN);

//        this.mCommController.sendCmdBuffer(cmd.getCmdMsg());
        cmd.getStatus().handleStsMsg(this.mCommController.sendAndRecieveCmd(cmd.getCmdMsg()));
        this.mSysDev = new SystemDevice();
    }

    private void getDevicesInfo(DeviceId devId) throws Exception
    {
        Command cmd = new InfoCmd(devId);

        cmd.getStatus().handleStsMsg(this.mCommController.sendAndRecieveCmd(cmd.getCmdMsg()));
        //cmd should have the data that we need
    }
    private void getSupportedCommands(DeviceId devId) throws Exception
    {
        Command cmd = new InfoCmd(devId);
        this.mCommController.sendCmdBuffer(cmd.getCmdMsg());

    }
    private void getSupportedSubDevices(DeviceId devId) throws Exception
    {
        Command cmd = new InfoCmd(devId);
        this.mCommController.sendCmdBuffer(cmd.getCmdMsg());

    }
}
