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
import com.ifit.sparky.fecp.interpreter.command.GetCmdsCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSubDevicesCmd;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.DeviceInfo;
import com.ifit.sparky.fecp.interpreter.status.GetCmdsSts;
import com.ifit.sparky.fecp.interpreter.status.GetSubDevicesSts;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;

import java.nio.ByteBuffer;
import java.util.Set;

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
            this.mSysDev = new SystemDevice(getSubDevice(DeviceId.MAIN));

            //two references to the same object with different responsibilities
            this.mCmdHandleInterface = new FecpCmdHandler(this.mCommController);
        }
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
    public void addCmd(FecpCommand cmd) throws Exception
    {
        this.mCmdHandleInterface.addFecpCommand(cmd);
    }

    //this is temp function to be removed
    //todo remove
    public void sendCommand(FecpCommand cmd) throws Exception
    {
        this.mCmdHandleInterface.sendCommand(cmd);
    }

    private Device getSubDevice(DeviceId devId) throws Exception
    {
        Device dev = new Device(devId);//
        Set<DeviceId> subDeviceList;
        Set<CommandId> tempCmds;
        //get subDevices
        subDeviceList = getSupportedSubDevices(devId);

        for(DeviceId subDevId : subDeviceList)
        {
            dev.addSubDevice(getSubDevice(subDevId));
        }
        //add commands
        tempCmds = getSupportedCommands(dev.getInfo().getDevId());

        for(CommandId id : tempCmds)
        {
            dev.addCommand(initializeCommand(dev.getInfo().getDevId(), id));
        }

        //add info
        dev.setDeviceInfo(getDevicesInfo(dev.getInfo().getDevId()));
        return dev;
    }


    private DeviceInfo getDevicesInfo(DeviceId devId) throws Exception
    {
        Command cmd = new InfoCmd(devId);
        cmd.getStatus().handleStsMsg(this.mCommController.sendAndRecieveCmd(cmd.getCmdMsg()));
        return ((InfoSts)cmd.getStatus()).getInfo();
    }

    private Set<CommandId> getSupportedCommands(DeviceId devId) throws Exception
    {
        Command cmd = new GetCmdsCmd(devId);
        cmd.getStatus().handleStsMsg(this.mCommController.sendAndRecieveCmd(cmd.getCmdMsg()));
        return ((GetCmdsSts)cmd.getStatus()).getSupportedCommands();
    }
    private Set<DeviceId> getSupportedSubDevices(DeviceId devId) throws Exception
    {
        Command cmd = new GetSubDevicesCmd(devId);
        cmd.getStatus().handleStsMsg(this.mCommController.sendAndRecieveCmd(cmd.getCmdMsg()));
        return ((GetSubDevicesSts)cmd.getStatus()).getSubDevices();
    }

    private static Command initializeCommand(DeviceId devId, CommandId cmdId) throws Exception
    {
        Command cmd;

        if(CommandId.CONNECT == cmdId)
        {
            //not implemented yet
            throw new Exception("Command not supported yet");
        }
        else if(CommandId.DISCONNECT == cmdId)
        {
            //not implemented yet
            throw new Exception("Command not supported yet");
        }
        else if(CommandId.CALIBRATE == cmdId)
        {
            //not implemented yet
            throw new Exception("Command not supported yet");
        }
        return null;//nothing supported yet
    }

}
