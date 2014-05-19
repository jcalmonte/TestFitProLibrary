/**
 * The main device for the system.
 * @author Levi.Balling
 * @date 12/18/13
 * @version 1
 * The main device for the system, but also holds more information. It contains information about
 * what type of system it is(slave,master, etc...).
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.GetCmdsCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSubDevicesCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSysInfoCmd;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.DeviceInfo;
import com.ifit.sparky.fecp.interpreter.status.GetCmdsSts;
import com.ifit.sparky.fecp.interpreter.status.GetSubDevicesSts;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;
import com.ifit.sparky.fecp.interpreter.status.GetTaskInfoSts;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;

import java.util.Set;

public class SystemDevice extends Device{

    private SystemConfiguration mConfig;//slave,master, or multi master
    private int mModel;
    private int mPartNumber;
    private double mCpuUse;
    private int mNumberOfTasks;
    private int mIntervalTime;
    private int mCpuFrequency;
    private String mMcuName;
    private String mConsoleName;

    // this is to clean up the controlling to allow better interfaces to the fitPro
    private CommInterface mComCntrl;

    /**
     * the default constructor for the System Device
     */
    public SystemDevice() throws Exception
    {
        super();
        this.mConfig = SystemConfiguration.SLAVE;
        this.mModel = 0;
        this.mPartNumber = 0;
        this.mCpuUse = 0.0;
        this.mNumberOfTasks = 0;
        this.mIntervalTime = 0;
        this.mCpuFrequency = 0;
        this.mMcuName = "";
        this.mConsoleName = "";
    }

    /**
     * the initializes the System with the communication
     */
    public SystemDevice(CommInterface comCntrl) throws Exception
    {
        super();
        initializeSystemDevice(comCntrl);
    }

    /**
     * the default constructor for the System Device
     */
    public SystemDevice(DeviceId id) throws Exception
    {
        super(id);
        this.mConfig = SystemConfiguration.SLAVE;
        this.mModel = 0;
        this.mPartNumber = 0;
        this.mCpuUse = 0.0;
        this.mNumberOfTasks = 0;
        this.mIntervalTime = 0;
        this.mCpuFrequency = 0;
        this.mMcuName = "";
        this.mConsoleName = "";
    }

    /**
     * generates a System Device from a generic device.
     * easier for initialization
     * @param dev the device that will be the System Device
     * @throws Exception
     */
    public SystemDevice(Device dev) throws Exception
    {
        super(dev.getCommandSet().values(), dev.getSubDeviceList(),dev.getInfo());

        this.mConfig = SystemConfiguration.SLAVE;
        this.mModel = 0;
        this.mPartNumber = 0;
        this.mCpuUse = 0.0;
        this.mNumberOfTasks = 0;
        this.mIntervalTime = 0;
        this.mCpuFrequency = 0;
        this.mMcuName = "";
        this.mConsoleName = "";
    }

    /**
     * the default constructor for the System Device
     */
    public SystemDevice(DeviceId id, SystemConfiguration config) throws Exception
    {
        super(id);
        this.mConfig = config;
        this.mModel = 0;
        this.mPartNumber = 0;
        this.mCpuUse = 0.0;
        this.mNumberOfTasks = 0;
        this.mIntervalTime = 0;
        this.mCpuFrequency = 0;
        this.mMcuName = "";
        this.mConsoleName = "";
    }

    /**
     * initializes the System device in a variety of ways to clean up the system.
     * @param comCntrl the communication interface for initializing the system.
     */
    protected void initializeSystemDevice(CommInterface comCntrl) throws Exception
    {
        this.mComCntrl = comCntrl;
        GetSysInfoCmd sysInfoCmd;
        Device resultDevice;

        resultDevice = getInitialDevice(DeviceId.MAIN);
        this.addCommands(resultDevice.getCommandSet().values());
        this.addAllSubDevice(resultDevice.getSubDeviceList());
        this.setDeviceInfo(resultDevice.getInfo());


        if (this.getInfo().getDevId() == DeviceId.NONE) {
            return;//not connected to any device.
        }

        sysInfoCmd = new GetSysInfoCmd(this.getInfo().getDevId());

        sysInfoCmd.getStatus().handleStsMsg(this.mComCntrl.sendAndReceiveCmd(sysInfoCmd.getCmdMsg()));

        this.setSystemInfo(sysInfoCmd);
    }

    /**
     * Gets the system configuration
     * @return the system configuration
     */
    public SystemConfiguration getConfig()
    {
        return this.mConfig;
    }

    /**
     * Gets the model 
     * @return the Model Number
     */
    public int getModel() {
        return mModel;
    }

    /**
     * Gets the part number for the main system
     * @return
     */
    public int getPartNumber() {
        return mPartNumber;
    }

    /**
     * Gets the Current CPU of the System
     * @return the CPU usage
     */
    public double getCpuUse() {
        return mCpuUse;
    }

    /**
     * Gets the number of Tasks used
     * @return the number of tasks used
     */
    public int getNumberOfTasks() {
        return mNumberOfTasks;
    }

    /**
     * Gets the interval time in uSeconds
     * @return the interval time in uSeconds
     */
    public int getIntervalTime() {
        return mIntervalTime;
    }

    /**
     * Gets the CPU frequency
     * @return the CPU frequency in Hz
     */
    public int getCpuFrequency() {
        return mCpuFrequency;
    }

    /**
     * Gets the name of the Mcu
     * @return the Mcu name
     */
    public String getMcuName() {
        return mMcuName;
    }

    /**
     * gets the Name of the Console according to the Main Device
     * @return Main Device
     */
    public String getConsoleName() {
        return mConsoleName;
    }

    /**
     * Sets the System's configuration
     * @param config the system's configuration
     */
    public void setConfig(SystemConfiguration config)
    {
        this.mConfig = config;
    }

    /**
     * Sets the model number for the Main Device
     * @param model the model number
     */
    public void setModel(int model) {
        this.mModel = model;
    }

    /**
     * Sets the Part number for the Main Device
     * @param partNumber the Part Number
     */
    public void setPartNumber(int partNumber) {
        this.mPartNumber = partNumber;
    }

    /**
     * Sets the current CPU usage 0.000
     * @param cpuUse the CPU
     */
    public void setCpuUse(double cpuUse) {
        this.mCpuUse = cpuUse;
    }

    /**
     * Sets the Number of Tasks
     * @param numberOfTasks the number of Tasks
     */
    public void setNumberOfTasks(int numberOfTasks) {
        this.mNumberOfTasks = numberOfTasks;
    }

    /**
     * Sets the length of time for the interval in uSeconds
     * @param intervalTime time in uSeconds
     */
    public void setIntervalTime(int intervalTime) {
        this.mIntervalTime = intervalTime;
    }

    /**
     * Sets the Main Device CPU Frequency
     * @param cpuFrequency the frequency in Hz
     */
    public void setCpuFrequency(int cpuFrequency) {
        this.mCpuFrequency = cpuFrequency;
    }

    /**
     * The name of the MCU of the Main System
     * @param mcuName the Name of the Mcu
     */
    public void setMcuName(String mcuName) {
        this.mMcuName = mcuName;
    }

    /**
     * Sets the name of the Console
     * @param consoleName the name of the console
     */
    public void setConsoleName(String consoleName) {
        this.mConsoleName = consoleName;
    }

    public void setSystemInfo(GetSysInfoCmd systemInfo)
    {
        this.setConfig(((GetSysInfoSts) systemInfo.getStatus()).getConfig());
        this.setModel(((GetSysInfoSts) systemInfo.getStatus()).getModel());
        this.setPartNumber(((GetSysInfoSts) systemInfo.getStatus()).getPartNumber());
        this.setCpuUse(((GetSysInfoSts) systemInfo.getStatus()).getCpuUse());
        this.setNumberOfTasks(((GetSysInfoSts) systemInfo.getStatus()).getNumberOfTasks());
        this.setIntervalTime(((GetSysInfoSts) systemInfo.getStatus()).getIntervalTime());
        this.setCpuFrequency(((GetSysInfoSts) systemInfo.getStatus()).getCpuFrequency());
        this.setMcuName(((GetSysInfoSts) systemInfo.getStatus()).getMcuName());
        this.setConsoleName(((GetSysInfoSts) systemInfo.getStatus()).getConsoleName());

        if (this.getCommandSet().containsKey(CommandId.GET_TASK_INFO)) {
            //add the Cpu Frequency to the command
            ((GetTaskInfoSts) this.getCommand(CommandId.GET_TASK_INFO).getStatus()).getTask().setMainClkFrequency(this.getCpuFrequency());
        }

    }


private Device getInitialDevice(DeviceId devId) throws Exception {
        Device dev = new Device(devId);//
        Set<DeviceId> subDeviceList;
        Set<CommandId> tempCmds;
        //get subDevices
        subDeviceList = getSupportedSubDevices(devId);

        for (DeviceId subDevId : subDeviceList) {
        dev.addSubDevice(getInitialDevice(subDevId));
        }
        //add commands
        tempCmds = getSupportedCommands(dev.getInfo().getDevId());

    for (CommandId id : tempCmds) {
        dev.addCommand(id.getCommand(dev.getInfo().getDevId()));
    }

        //add info
        dev.setDeviceInfo(getDevicesInfo(dev.getInfo().getDevId()));
        return dev;
        }

private DeviceInfo getDevicesInfo(DeviceId devId) throws Exception {
        Command cmd = new InfoCmd(devId);
        cmd.getStatus().handleStsMsg(this.mComCntrl.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((InfoSts) cmd.getStatus()).getInfo();
        }

private Set<CommandId> getSupportedCommands(DeviceId devId) throws Exception {
        Command cmd = new GetCmdsCmd(devId);
        cmd.getStatus().handleStsMsg(this.mComCntrl.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((GetCmdsSts) cmd.getStatus()).getSupportedCommands();
        }

private Set<DeviceId> getSupportedSubDevices(DeviceId devId) throws Exception {
        Command cmd = new GetSubDevicesCmd(devId);
        cmd.getStatus().handleStsMsg(this.mComCntrl.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((GetSubDevicesSts) cmd.getStatus()).getSubDevices();
        }

    @Override
    public String toString() {
        String resultStr;

        resultStr = super.toString();
        resultStr += " config=" + mConfig.toString() +
                ", mModel=" + mModel +
                ", mPartNumber=" + mPartNumber +
                ", mCpuUse=%" + (mCpuUse*100) +
                ", mNumberOfTasks=" + mNumberOfTasks +
                ", mIntervalTime=" + mIntervalTime + "uSec"+
                ", mCpuFrequency=" + mCpuFrequency + "hz" +
                ", mMcuName='" + mMcuName +
                ", mConsoleName='" + mConsoleName;
        return resultStr;
    }
}
