/**
 * This status handles the Get System info, Things specific to the whole System.
 * @author Levi.Balling
 * @date 2/11/14
 * @version 1
 * The things that you can get with this command are the System unique part numbers, Console id.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.SystemConfiguration;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.nio.ByteBuffer;

public class GetSysInfoSts extends Status implements StatusInterface {

    private static final int STS_LENGTH = 24;

    private SystemConfiguration mConfig;//slave,master, or multi master
    private int mModel;
    private int mPartNumber;
    private double mCpuUse;
    private int mNumberOfTasks;
    private int mIntervalTime;
    private int mCpuFrequency;
    private String mMcuName;
    private String mConsoleName;

    /**
     * Main constructor for the Info Status response
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public GetSysInfoSts(DeviceId devId) throws Exception
    {
        super(StatusId.DEV_NOT_SUPPORTED, STS_LENGTH, CommandId.GET_SYSTEM_INFO, devId);

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
     * @return the part number
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
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception
    {
        super.handleStsMsg(buff);
        int nameLength;

        //now parse the data
        if(this.getStsId() == StatusId.DONE)
        {
            //System config
            this.mConfig = SystemConfiguration.convert(buff.get());
            //model Number
            this.mModel = buff.getInt();
            //part number
            this.mPartNumber = buff.getInt();
            //Cpu usage
            this.mCpuUse = buff.getShort();//240 == 0.240
            this.mCpuUse /= 1000;
            //number Of tasks
            this.mNumberOfTasks = buff.get();
            //cpu Min interval
            this.mIntervalTime = buff.getShort();
            //cpu clk freq
            this.mCpuFrequency = buff.getInt();
            //mcu name length
            nameLength = buff.get();
            //mcu name
            this.mMcuName = "";
            for(int i = 0; i < nameLength; i++)
            {
                this.mMcuName += buff.get();
            }
            //console name length
            nameLength = buff.get();
            //console name
            this.mConsoleName = "";
            for(int i = 0; i < nameLength; i++)
            {
                this.mConsoleName += buff.get();
            }
        }
    }

}
