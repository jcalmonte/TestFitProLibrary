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
import android.util.Log;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.TestComm;
import com.ifit.sparky.fecp.communication.UsbComm;
import com.ifit.sparky.fecp.error.ErrorCntrl;
import com.ifit.sparky.fecp.error.ErrorEventListener;
import com.ifit.sparky.fecp.error.ErrorReporting;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;
import com.ifit.sparky.fecp.interpreter.command.CalibrateCmd;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.GetCmdsCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSubDevicesCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSysInfoCmd;
import com.ifit.sparky.fecp.interpreter.command.GetTaskInfoCmd;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.command.SetTestingKeyCmd;
import com.ifit.sparky.fecp.interpreter.command.SetTestingTachCmd;
import com.ifit.sparky.fecp.interpreter.command.UpdateCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.DeviceInfo;
import com.ifit.sparky.fecp.interpreter.status.GetCmdsSts;
import com.ifit.sparky.fecp.interpreter.status.GetSubDevicesSts;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;
import com.ifit.sparky.fecp.testingUtil.CmdInterceptor;

import java.nio.ByteBuffer;
import java.util.Set;

public class FecpController implements ErrorReporting, CommInterface.DeviceConnectionListener {
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

        if(callback == null)
        {
            throw new Exception("SystemStatusCallback callback is null, Can't be null");
        }
        this.mCommType = type;
        this.statusCallback = callback;
        this.mSysDev = new SystemDevice(DeviceId.MAIN);//starts out as main
        this.mIsConnected = false;
        this.mContext = context;
        this.mIntent = intent;

        this.mSysErrorControl = new ErrorCntrl(this);
    }


    public void initializeConnection(CmdHandlerType type) throws Exception{
        this.initializeConnection(type, null);
    }

    /**
     * Initializes the connection and sets up the communication
     *
     * @param type the type of handling the system should have
     * @param listener this listens for changes in the connection
     */
    public void initializeConnection(CmdHandlerType type, CommInterface.DeviceConnectionListener listener) throws Exception {

        //add as we add support for these
        if (this.mCommType == CommType.USB_COMMUNICATION) {
            this.mCommController = new UsbComm(this.mContext, this.mIntent, 100);
        }
        else if(this.mCommType == CommType.TESTING_COMM) {
            this.mCommController = new TestComm();
            //right after this. and sets the interceptor
        }
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
        if (this.mCommType == CommType.USB_COMMUNICATION) {
            GetSysInfoCmd sysInfoCmd;
            this.mSysDev = new SystemDevice(getSubDevice(DeviceId.MAIN));

            if (this.mSysDev.getInfo().getDevId() == DeviceId.NONE) {
                return;//not connected to any device.
            }
            sysInfoCmd = new GetSysInfoCmd(this.mSysDev.getInfo().getDevId());

            sysInfoCmd.getStatus().handleStsMsg(this.mCommController.sendAndReceiveCmd(sysInfoCmd.getCmdMsg()));

            this.mSysDev.setSystemInfo(sysInfoCmd);

        }
        else if(this.mCommType == CommType.TESTING_COMM) {

        }

        this.mCmdHandleInterface = new FecpCmdHandler(this.mCommController);
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
            return new CalibrateCmd(devId);
        } else if (CommandId.GET_SYSTEM_INFO == cmdId) {
            return new GetSysInfoCmd(devId);
        } else if (CommandId.GET_TASK_INFO == cmdId) {
            return new GetTaskInfoCmd(devId);
        } else if (CommandId.SET_TESTING_KEY == cmdId) {
            return new SetTestingKeyCmd(devId);
        } else if (CommandId.SET_TESTING_TACH == cmdId) {
            return new SetTestingTachCmd(devId);
        } else if (CommandId.UPDATE == cmdId) {
            return new UpdateCmd(devId);
        }

        return null;//nothing supported yet
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
            this.getSystem();

            if(this.mSysDev.getInfo().getDevId() != DeviceId.NONE)
            {
                this.statusCallback.systemDeviceConnected(this.mSysDev);
            }
        }
        catch (Exception ex)
        {
            Log.e("Get System Failed", ex.getMessage());
        }
    }

    @Override
    public void onDeviceDisconnected() {
        //nothing to do
    }
}
