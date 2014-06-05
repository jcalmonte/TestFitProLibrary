/**
 * The main device for the system.
 * @author Levi.Balling
 * @date 12/18/13
 * @version 1
 * The main device for the system, but also holds more information. It contains information about
 * what type of system it is(slave,master, etc...).
 */
package com.ifit.sparky.fecp;

import android.util.Log;

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
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
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

    public SystemDevice(GetSysInfoSts sts) throws Exception
    {
        super(sts.getDevId());

        this.mConfig = sts.getConfig();
        this.mModel = sts.getModel();
        this.mPartNumber = sts.getPartNumber();
        this.mCpuUse = sts.getCpuUse();
        this.mNumberOfTasks = sts.getNumberOfTasks();
        this.mIntervalTime = sts.getIntervalTime();
        this.mCpuFrequency = sts.getCpuFrequency();
        this.mMcuName = sts.getMcuName();
        this.mConsoleName = sts.getConsoleName();
        this.mSysInfoReply = sts;
        this.mCurrentSystemData = new TreeMap<BitFieldId, BitfieldDataConverter>();


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


    /**
     * Updates the data that you need to know for displaying data.
     * @param sts Results of the command for any listeners
     */
    public void updateCurrentData(WriteReadDataSts sts)
    {
        TreeMap<BitFieldId, BitfieldDataConverter> cmdResults;

        cmdResults = sts.getResultData();

        for (Map.Entry<BitFieldId, BitfieldDataConverter> entry : cmdResults.entrySet()) {
                entry.getValue().setTimeRecieved(System.currentTimeMillis());
            //todo add a set Value is dirty here, so we can get all of the values that have changed easier.
                this.mCurrentSystemData.put(entry.getKey(), entry.getValue());
        }
    }

    public GetSysInfoSts getSysInfoSts()
    {
        //returns the system info based on what we currently have
        return this.mSysInfoReply;
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

    /**
     * Writes the object to be sent over a Serial Stream.
     * @param stream Stream to be loaded. data size between 1K and 2K
     * @throws IOException
     */
    public void writeObject(BufferedOutputStream stream) throws IOException
    {
        ByteBuffer tempBuff = ByteBuffer.allocate(2000);//we don't need all of this, but it will help
        tempBuff.order(ByteOrder.LITTLE_ENDIAN);
        //write the data we are concerned about
        if(this.mConfig == SystemConfiguration.MASTER)
        {
            tempBuff.put((byte) SystemConfiguration.PORTAL_TO_MASTER.getVal());
        }
        else if(this.mConfig == SystemConfiguration.MULTI_MASTER)
        {
            tempBuff.put((byte) SystemConfiguration.PORTAL_TO_MASTER.getVal());
        }
        else if(this.mConfig == SystemConfiguration.SLAVE)
        {
            //portal to slave
            tempBuff.put((byte) SystemConfiguration.PORTAL_TO_SLAVE.getVal());
        }
        else
        {
            tempBuff.put((byte) this.mConfig.getVal());
        }
        tempBuff.putInt(this.mModel);
        tempBuff.putInt(this.mPartNumber);
        tempBuff.putDouble(this.mCpuUse);
        tempBuff.putInt(this.mNumberOfTasks);
        tempBuff.putInt(this.mIntervalTime);
        tempBuff.putInt(this.mCpuFrequency);

        tempBuff.put((byte)this.mMcuName.length());//length of string
        tempBuff.put(this.mMcuName.getBytes());

        tempBuff.put((byte)this.mConsoleName.length());//length of string
        tempBuff.put(this.mConsoleName.getBytes());

        DeviceInfo info = this.getInfo();

        tempBuff.put((byte)info.getDevId().getVal());
        tempBuff.put((byte)info.getSWVersion());
        tempBuff.put((byte)info.getHWVersion());
        tempBuff.putInt(info.getSerialNumber());
        tempBuff.putInt(info.getManufactureNumber());
        tempBuff.put((byte) info.getSections());
        //put the number of bitfields
        tempBuff.put((byte)info.getSupportedBitfields().size());

        for (BitFieldId fieldId : info.getSupportedBitfields()) {
            tempBuff.put((byte)fieldId.getVal());
        }

        tempBuff.put((byte) this.mCurrentSystemData.size());

        int dataSize = this.mCurrentSystemData.size();

        for (Map.Entry<BitFieldId, BitfieldDataConverter> entry : this.mCurrentSystemData.entrySet()) {
            tempBuff.put((byte)entry.getKey().getVal());//store the key id
            //store the value associated with that key
            //just the raw data
            entry.getValue().writeObject(tempBuff);
        }
        //write all the data to the stream
        int currPosition = tempBuff.position();
        tempBuff.position(0);
        stream.write(tempBuff.array(), 0, currPosition);
    }

    /**
     * To allow easy passing of data objects from one System to another we have this system that will
     * serialize the object for the other side estimate on object size is between 1K Bytes and 2K Bytes
     * @param stream the stream we want to load with the data
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException
    {

        BitFieldId key  = BitFieldId.KPH;//first bit anyway
        BitfieldDataConverter value;
        int i = 0;
        try {
            this.mConfig = SystemConfiguration.convert(stream.get());
            this.mModel = stream.getInt();
            this.mPartNumber = stream.getInt();
            this.mCpuUse= stream.getDouble();
            this.mNumberOfTasks = stream.getInt();
            this.mIntervalTime = stream.getInt();
            this.mCpuFrequency = stream.getInt();

            int strLength = stream.get();
            byte[] strArr = new byte[strLength];
            stream.get(strArr, 0, strLength);
            String str = new String( strArr, Charset.forName("UTF-8") );
            this.mMcuName = str;

            strLength = stream.get();
            strArr = new byte[strLength];
            stream.get(strArr, 0, strLength);
            str = new String( strArr, Charset.forName("UTF-8") );

            this.mConsoleName = str;
            //get the device info
            DeviceInfo info = this.getInfo();

            info.setDevId(DeviceId.getDeviceId(stream.get()));
            info.setSWVersion(stream.get());
            info.setHWVersion(stream.get());
            info.setSerialNumber(stream.getInt());
            info.setManufactureNumber(stream.getInt());
            int sections = stream.get();//not necessary
            int numOfBitFields = stream.get();

            for(i = 0; i < numOfBitFields; i++)
            {
                info.addBitfield(BitFieldId.getBitFieldId(stream.get()));
            }

            int currDataSize = stream.get();
            if(this.mCurrentSystemData == null)
            {
                this.mCurrentSystemData =  new TreeMap<BitFieldId, BitfieldDataConverter>();
            }

            for(i = 0; i < currDataSize; i++)
            {
                key = BitFieldId.getBitFieldId(stream.get());
                value = key.getConverter();
                value.readObject(stream);
                this.mCurrentSystemData.put(key, value);
            }


        } catch (ClassNotFoundException e) {
            if(key != null) {
                Log.d("Data_Interpret", "key=" + key.getDescription() + " i=" + i);
            }
            e.printStackTrace();
        } catch (Exception e) {
            if(key != null) {
                Log.d("Data_Interpret", "key=" + key.getDescription() + " i=" + i);
            }
            e.printStackTrace();
        }
    }



    /**
     * initializes the System device in a variety of ways to clean up the system.
     * @param comm the communication interface for initializing the system.
     */
    public static SystemDevice initializeSystemDevice(CommInterface comm) throws Exception
    {
        GetSysInfoCmd sysInfoCmd;
        SystemDevice resultDevice;
        Device tempDevice;


        //first start out by finding out the configuration
        sysInfoCmd = new GetSysInfoCmd(DeviceId.MAIN);

        sysInfoCmd.getStatus().handleStsMsg(comm.sendAndReceiveCmd(sysInfoCmd.getCmdMsg()));

        if(sysInfoCmd.getStatus().getStsId() != StatusId.DONE || sysInfoCmd.getDevId() == DeviceId.NONE)
        {
            //disconnect the comm port
            return null;//no device available
        }

        resultDevice = new SystemDevice((GetSysInfoSts)sysInfoCmd.getStatus());

        if(resultDevice.getConfig() == SystemConfiguration.MASTER || resultDevice.getConfig() == SystemConfiguration.SLAVE )//direct master connection
        {
            tempDevice = getInitialDevice(comm, DeviceId.MAIN);
            resultDevice.addCommands(tempDevice.getCommandSet().values());
            resultDevice.addAllSubDevice(tempDevice.getSubDeviceList());
            resultDevice.setDeviceInfo(tempDevice.getInfo());

            if (resultDevice.getCommandSet().containsKey(CommandId.GET_TASK_INFO)) {
                //add the Cpu Frequency to the command so it will reflect actual time for the tasks
                ((GetTaskInfoSts) resultDevice.getCommand(CommandId.GET_TASK_INFO).getStatus()).getTask().setMainClkFrequency(resultDevice.mCpuFrequency);
            }
        }
        else if(resultDevice.getConfig() == SystemConfiguration.PORTAL_TO_MASTER || resultDevice.getConfig() == SystemConfiguration.PORTAL_TO_SLAVE)
        {
            //communication is different update through the latest Command data
            //create a Portal Listen command
            //fetch the System Device object
            Command portalDeviceCmd = new PortalDeviceCmd(DeviceId.PORTAL);
            portalDeviceCmd.getStatus().handleStsMsg(comm.sendAndReceiveCmd(portalDeviceCmd.getCmdMsg()));
            SystemDevice newSysData = ((PortalDeviceSts)portalDeviceCmd.getStatus()).getmSysDev();

            resultDevice.setDeviceInfo(newSysData.getInfo());
            resultDevice.mCurrentSystemData = newSysData.mCurrentSystemData;

        }

        return resultDevice;
    }


    private static Device getInitialDevice(CommInterface comm, DeviceId devId) throws Exception
    {
        Device dev = new Device(devId);//
        Set<DeviceId> subDeviceList;
        Set<CommandId> tempCmds;
        //get subDevices
        subDeviceList = getSupportedSubDevices(comm, devId);

        for (DeviceId subDevId : subDeviceList) {
            dev.addSubDevice(getInitialDevice(comm, subDevId));
        }

        //add commands
        tempCmds = getSupportedCommands(comm, dev.getInfo().getDevId());

        if (tempCmds.size() != 1 && !tempCmds.contains(CommandId.NONE)) {
            for (CommandId id : tempCmds) {
                dev.addCommand(id.getCommand(dev.getInfo().getDevId()));
            }
        }

        //add info
        dev.setDeviceInfo(getDevicesInfo(comm, dev.getInfo().getDevId()));
        return dev;

    }

    private static DeviceInfo getDevicesInfo(CommInterface comm, DeviceId devId) throws Exception {
        Command cmd = new InfoCmd(devId);
        cmd.getStatus().handleStsMsg(comm.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((InfoSts) cmd.getStatus()).getInfo();
        }

    private static Set<CommandId> getSupportedCommands(CommInterface comm, DeviceId devId) throws Exception {
        Command cmd = new GetCmdsCmd(devId);
        cmd.getStatus().handleStsMsg(comm.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((GetCmdsSts) cmd.getStatus()).getSupportedCommands();
        }

    private static Set<DeviceId> getSupportedSubDevices(CommInterface comm, DeviceId devId) throws Exception {
        Command cmd = new GetSubDevicesCmd(devId);
        cmd.getStatus().handleStsMsg(comm.sendAndReceiveCmd(cmd.getCmdMsg()));
        return ((GetSubDevicesSts) cmd.getStatus()).getSubDevices();
        }



}
