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
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.GetCmdsCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSubDevicesCmd;
import com.ifit.sparky.fecp.interpreter.command.GetSysInfoCmd;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.command.PortalDeviceCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.DeviceInfo;
import com.ifit.sparky.fecp.interpreter.status.GetCmdsSts;
import com.ifit.sparky.fecp.interpreter.status.GetSubDevicesSts;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;
import com.ifit.sparky.fecp.interpreter.status.GetTaskInfoSts;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;
import com.ifit.sparky.fecp.interpreter.status.PortalDeviceSts;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SystemDevice extends Device implements Serializable{

    private SystemConfiguration mConfig;//slave,master, or multi master
    private int mModel;
    private int mPartNumber;
    private double mCpuUse;
    private int mNumberOfTasks;
    private int mIntervalTime;
    private int mCpuFrequency;
    private String mMcuName;
    private String mConsoleName;
    private GetSysInfoSts mSysInfoReply;//this is to help with other tablet querying for more info about this machine.

    private TreeMap<BitFieldId, BitfieldDataConverter> mCurrentSystemData;//anyTime data is received
    //it will send this to all the listeners. this is to make the delay of communication seemless

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
        this.mCurrentSystemData = new TreeMap<BitFieldId, BitfieldDataConverter>();
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
        this.mCurrentSystemData = new TreeMap<BitFieldId, BitfieldDataConverter>();
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
        this.mCurrentSystemData = new TreeMap<BitFieldId, BitfieldDataConverter>();
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
        this.mCurrentSystemData = new TreeMap<BitFieldId, BitfieldDataConverter>();
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
        this.mCurrentSystemData = new TreeMap<BitFieldId, BitfieldDataConverter>();


        //first start out by finding out the configuration
        sysInfoCmd = new GetSysInfoCmd(DeviceId.MAIN);

        sysInfoCmd.getStatus().handleStsMsg(this.mComCntrl.sendAndReceiveCmd(sysInfoCmd.getCmdMsg()));
        this.mSysInfoReply = (GetSysInfoSts)sysInfoCmd.getStatus();

        this.setSystemInfo(sysInfoCmd);


        if(this.getConfig() == SystemConfiguration.MASTER || this.getConfig() == SystemConfiguration.SLAVE )//direct master connection
        {
            resultDevice = getInitialDevice(DeviceId.MAIN);
            this.addCommands(resultDevice.getCommandSet().values());
            this.addAllSubDevice(resultDevice.getSubDeviceList());
            this.setDeviceInfo(resultDevice.getInfo());
        }
        else if(this.getConfig() == SystemConfiguration.PORTAL_TO_MASTER || this.getConfig() == SystemConfiguration.PORTAL_TO_SLAVE)
        {
            //communication is different update through the latest Command data
            //create a Portal Listen command
            //fetch the System Device object
            Command portalDeviceCmd = new PortalDeviceCmd(DeviceId.PORTAL);
            portalDeviceCmd.getStatus().handleStsMsg(this.mComCntrl.sendAndReceiveCmd(portalDeviceCmd.getCmdMsg()));
            SystemDevice newSysData = ((PortalDeviceSts)portalDeviceCmd.getStatus()).getmSysDev();

            //copy data
            //exclude Configuration
            this.mModel = newSysData.mModel;
            this.mPartNumber = newSysData.mPartNumber;
            this.mCpuUse = newSysData.mCpuUse;
            this.mNumberOfTasks = newSysData.mNumberOfTasks;
            this.mIntervalTime = newSysData.mIntervalTime;
            this.mCpuFrequency = newSysData.mCpuFrequency;
            this.mMcuName = newSysData.mMcuName;
            this.mConsoleName = newSysData.mConsoleName;
            this.setDeviceInfo(newSysData.getInfo());
            this.mCurrentSystemData = newSysData.mCurrentSystemData;

        }


        if (this.getInfo().getDevId() == DeviceId.NONE) {
            return;//not connected to any device.
        }

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
     * Gets the latest data about the System
     * @return TreeMap of all the latest data.
     */
    public TreeMap<BitFieldId, BitfieldDataConverter> getCurrentSystemData() {
        return mCurrentSystemData;
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

    public void updateCurrentData(WriteReadDataSts sts)
    {
        TreeMap<BitFieldId, BitfieldDataConverter> cmdResults;

        cmdResults = sts.getResultData();

        for (Map.Entry<BitFieldId, BitfieldDataConverter> entry : cmdResults.entrySet()) {
                entry.getValue().setTimeRecieved(System.currentTimeMillis());
                this.mCurrentSystemData.put(entry.getKey(), entry.getValue());
        }
    }

    public GetSysInfoSts getSysInfoSts()
    {
        //returns the system info based on what we currently have
        return this.mSysInfoReply;
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


    public void writeObject(ObjectOutputStream stream) throws IOException
    {
        //write the data we are concerned about

        stream.writeObject(this.mConfig);
        stream.writeInt(this.mModel);
        stream.writeInt(this.mPartNumber);
        stream.writeDouble(this.mCpuUse);
        stream.writeInt(this.mNumberOfTasks);
        stream.writeInt(this.mIntervalTime);
        stream.writeInt(this.mCpuFrequency);
        stream.writeObject(this.mMcuName);
        stream.writeObject(this.mConsoleName);
        stream.writeObject(this.getInfo());
        int dataSize = this.mCurrentSystemData.size();

        stream.writeInt(dataSize);

        for (Map.Entry<BitFieldId, BitfieldDataConverter> entry : this.mCurrentSystemData.entrySet()) {
            stream.writeObject(entry.getKey());
            entry.getValue().writeObject(stream);

        }
    }

    public void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
    {
        this.mConfig = (SystemConfiguration)stream.readObject();
        this.mModel = stream.readInt();
        this.mPartNumber = stream.readInt();
        this.mCpuUse= stream.readDouble();
        this.mNumberOfTasks = stream.readInt();
        this.mIntervalTime = stream.readInt();
        this.mCpuFrequency = stream.readInt();
        this.mMcuName = (String)stream.readObject();
        this.mConsoleName = (String)stream.readObject();
        this.setDeviceInfo((DeviceInfo)stream.readObject());

        int currDataSize = stream.readInt();

        if(this.mCurrentSystemData == null)
        {
            this.mCurrentSystemData =  new TreeMap<BitFieldId, BitfieldDataConverter>();
        }

        for(int i = 0; i < currDataSize; i++)
        {
            BitFieldId key = (BitFieldId)stream.readObject();
            BitfieldDataConverter value = key.getConverter();
            value.readObject(stream);
            this.mCurrentSystemData.put(key, value);
        }
    }
}
