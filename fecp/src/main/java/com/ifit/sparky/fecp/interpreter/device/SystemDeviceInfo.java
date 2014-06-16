/**
 * This will handle all of the data of the Main System Info.
 * @author Levi.Balling
 * @date 6/13/2014
 * @version 1
 * This will be used to determine what system we are connecting to, when you scan for
 * devices it will be a bunch of SystemDeviceInfo.
 */
package com.ifit.sparky.fecp.interpreter.device;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class SystemDeviceInfo {

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
     * The System may only be created from a getSystemInfoCommand
     * @param buff buffer that has the raw data
     */
    public SystemDeviceInfo(ByteBuffer buff)
    {
        int nameLength;
        //populates all of the items in the object
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
     * @return gets the system's Part Number
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
     * Writes the Stream of the systemDev Info
     * @param stream of the System Dev Info
     * @throws IOException if there is a issue adding the info to the stream.
     */
    public void writeObject(BufferedOutputStream stream)throws IOException
    {
        ByteBuffer tempBuff = ByteBuffer.allocate(2000);//we don't need all of this, but it will help
        tempBuff.order(ByteOrder.LITTLE_ENDIAN);
        //write the data we are concerned about

        if(this.mConfig== SystemConfiguration.MASTER)
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

        //write all the data to the stream
        int currPosition = tempBuff.position();
        tempBuff.position(0);
        stream.write(tempBuff.array(), 0, currPosition);
    }

    public void readObject(ByteBuffer stream) throws IOException
    {
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

    }
}
