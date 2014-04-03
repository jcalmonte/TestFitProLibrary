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

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.UsbComm;
import com.ifit.sparky.fecp.error.ErrorCntrl;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.GetCmdsCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSubDevicesCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSysInfoCmd;
import com.ifit.sparky.fecp.interpreter.command.GetTaskInfoCmd;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.command.SetTestingKeyCmd;
import com.ifit.sparky.fecp.interpreter.command.SetTestingTachCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.DeviceInfo;
import com.ifit.sparky.fecp.interpreter.status.GetCmdsSts;
import com.ifit.sparky.fecp.interpreter.status.GetSubDevicesSts;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;
import com.ifit.sparky.fecp.interpreter.status.GetTaskInfoSts;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;

import java.util.Set;

public class FecpController {
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
    private ErrorCntrl mSysErrorControl;

    /**
     * Sets up the controller, and all the facets dealing with the controller
     *
     * @param context  the application context
     * @param type     the type of communication
     * @param callback the callback for connection and disconnections
     * @throws Exception if the device is invalid
     */
    public FecpController(Context context, Intent intent, CommType type, SystemStatusCallback callback) throws Exception {
        this.mCommType = type;
        this.statusCallback = callback;
        this.mSysDev = new SystemDevice(DeviceId.MAIN);//starts out as main
        this.mIsConnected = false;
        this.mContext = context;
        this.mIntent = intent;
    }

    /**
     * Initializes the connection and sets up the communication
     *
     * @param type the type of handling the system should have
     * @return the system device
     */
    public SystemDevice initializeConnection(CmdHandlerType type) throws Exception {
        GetSysInfoCmd sysInfoCmd;
        //add as we add support for these
        if (this.mCommType == CommType.USB_COMMUNICATION) {
            this.mCommController = new UsbComm(this.mContext, this.mIntent, 100);
            this.mSysDev = new SystemDevice(getSubDevice(DeviceId.MAIN));

            if (this.mSysDev.getInfo().getDevId() == DeviceId.NONE) {
                return this.mSysDev;//return null if no device is present.
            }
            sysInfoCmd = new GetSysInfoCmd(this.mSysDev.getInfo().getDevId());

            sysInfoCmd.getStatus().handleStsMsg(this.mCommController.sendAndReceiveCmd(sysInfoCmd.getCmdMsg()));

            this.mSysDev.setConfig(((GetSysInfoSts) sysInfoCmd.getStatus()).getConfig());
            this.mSysDev.setModel(((GetSysInfoSts) sysInfoCmd.getStatus()).getModel());
            this.mSysDev.setPartNumber(((GetSysInfoSts) sysInfoCmd.getStatus()).getPartNumber());
            this.mSysDev.setCpuUse(((GetSysInfoSts) sysInfoCmd.getStatus()).getCpuUse());
            this.mSysDev.setNumberOfTasks(((GetSysInfoSts) sysInfoCmd.getStatus()).getNumberOfTasks());
            this.mSysDev.setIntervalTime(((GetSysInfoSts) sysInfoCmd.getStatus()).getIntervalTime());
            this.mSysDev.setCpuFrequency(((GetSysInfoSts) sysInfoCmd.getStatus()).getCpuFrequency());
            this.mSysDev.setMcuName(((GetSysInfoSts) sysInfoCmd.getStatus()).getMcuName());
            this.mSysDev.setConsoleName(((GetSysInfoSts) sysInfoCmd.getStatus()).getConsoleName());
            if (this.mSysDev.getCommandSet().containsKey(CommandId.GET_TASK_INFO)) {
                //add the Cpu Frequency to the command
                ((GetTaskInfoSts) this.mSysDev.getCommand(CommandId.GET_TASK_INFO).getStatus()).getTask().setMainClkFrequency(this.mSysDev.getCpuFrequency());
            }

            //two references to the same object with different responsibilities
            this.mCmdHandleInterface = new FecpCmdHandler(this.mCommController);
        }
        this.mSysErrorControl = new ErrorCntrl(this);
        //connected to the system
        this.statusCallback.systemConnected();
        return this.mSysDev;
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

    private Device getSubDevice(DeviceId devId) throws Exception {
        Device dev = new Device(devId);//
        Set<DeviceId> subDeviceList;
        Set<CommandId> tempCmds;
        //get subDevices
        subDeviceList = getSupportedSubDevices(devId);

        for (DeviceId subDevId : subDeviceList) {
            dev.addSubDevice(getSubDevice(subDevId));
        }
        //add commands
        tempCmds = getSupportedCommands(dev.getInfo().getDevId());

        for (CommandId id : tempCmds) {
            dev.addCommand(initializeCommand(dev.getInfo().getDevId(), id));
        }

        //add info
        dev.setDeviceInfo(getDevicesInfo(dev.getInfo().getDevId()));
        return dev;
    }

    private DeviceInfo getDevicesInfo(DeviceId devId) throws Exception {
        Command cmd = new InfoCmd(devId);
        cmd.getStatus().handleStsMsg(this.mCommController.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((InfoSts) cmd.getStatus()).getInfo();
    }

    private Set<CommandId> getSupportedCommands(DeviceId devId) throws Exception {
        Command cmd = new GetCmdsCmd(devId);
        cmd.getStatus().handleStsMsg(this.mCommController.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((GetCmdsSts) cmd.getStatus()).getSupportedCommands();
    }

    private Set<DeviceId> getSupportedSubDevices(DeviceId devId) throws Exception {
        Command cmd = new GetSubDevicesCmd(devId);
        cmd.getStatus().handleStsMsg(this.mCommController.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((GetSubDevicesSts) cmd.getStatus()).getSubDevices();
    }

    private static Command initializeCommand(DeviceId devId, CommandId cmdId) throws Exception {
        if (CommandId.CONNECT == cmdId) {
            //not implemented yet
            throw new Exception("Command not supported yet");
        } else if (CommandId.DISCONNECT == cmdId) {
            //not implemented yet
            throw new Exception("Command not supported yet");
        } else if (CommandId.CALIBRATE == cmdId) {
            //not implemented yet
            throw new Exception("Command not supported yet");
        } else if (CommandId.GET_SYSTEM_INFO == cmdId) {
            return new GetSysInfoCmd(devId);
        } else if (CommandId.GET_TASK_INFO == cmdId) {
            return new GetTaskInfoCmd(devId);
        } else if (CommandId.SET_TESTING_KEY == cmdId) {
            return new SetTestingKeyCmd(devId);
        } else if (CommandId.SET_TESTING_TACH == cmdId) {
            return new SetTestingTachCmd(devId);
        }

        return null;//nothing supported yet
    }

}
