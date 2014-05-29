/**
 * This is a dumbed version of the Device.
 * @author Levi.Balling
 * @date 5/29/2014
 * @version 1
 * This will have specific information about which device you would like to connect to.
 */
package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.SystemConfiguration;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;

public class ConnectionDevice {

    //this will be a very generic System Device,
    protected DeviceId mDevId;
    protected  SystemConfiguration mConfig;
    protected  String mModel;
    protected  String mPartNumber;
    protected  String mConsoleName;//reconfigurable to allow user writing it.
    protected CommType mCommType;//this is what type of communication the Device uses

    /**
     * Default constructor
     */
    public ConnectionDevice()
    {
        this.initializeConnectionDevice(DeviceId.NONE, SystemConfiguration.SLAVE, "", "", "", CommType.NONE);
    }

    /**
     * Creates a Connection Device with the Info that we need to now about.
     * @param id Device Id of the Specific System we are looking at
     * @param config the Type of configuration of the System we are looking at
     * @param model the model of the system
     * @param partNum the partnumber
     * @param consoleName the Console name of the System.
     * @param commType The type of communication the Device uses
     */
    public ConnectionDevice(DeviceId id, SystemConfiguration config, String model,
                            String partNum, String consoleName, CommType commType)
    {
        this.initializeConnectionDevice(id, config, model, partNum, consoleName, commType);
    }

    /**
     * This is used to initialize the Connection Device
     * @param id Device Id of the Specific System we are looking at
     * @param config the Type of configuration of the System we are looking at
     * @param model the model of the system
     * @param partNum the partnumber
     * @param consoleName the Console name of the System.
     * @param commType The type of communication the Device uses
     */
    protected void initializeConnectionDevice(DeviceId id, SystemConfiguration config,
                                              String model, String partNum, String consoleName,
                                              CommType commType)
    {
        this.mDevId = id;
        this.mConfig = config;
        this.mModel = model;
        this.mPartNumber = partNum;
        this.mConsoleName = consoleName;
        this.mCommType = commType;
    }

    // ************GETTERS****************

    /**
     * The Device id of the system
     * @return the device id
     */
    public DeviceId getDevId() {
        return mDevId;
    }

    /**
     * The Type of configuration of the system
     * @return System Configuration
     */
    public SystemConfiguration getConfig() {
        return mConfig;
    }

    /**
     * The Model String
     * @return the model of the system
     */
    public String getModel() {
        return mModel;
    }

    /**
     * The Part number of the system
     * @return the Part number
     */
    public String getPartNumber() {
        return mPartNumber;
    }

    /**
     * The Console name of the system
     * @return the Console Name
     */
    public String getConsoleName() {
        return mConsoleName;
    }

    /**
     * The type of communication of the system.
     * @return the communication type
     */
    public CommType getCommType() {
        return mCommType;
    }

    // ************SETTERS****************


    public void setSysInfoVal(GetSysInfoSts sts)
    {

        this.mDevId = sts.getDevId();
        this.mConfig = sts.getConfig();
        this.mModel = sts.getModel() + "";
        this.mPartNumber = sts.getPartNumber() + "";
        this.mConsoleName = sts.getConsoleName();
    }

    /**
     * Sets the Device ID
     * @param devId the Device ID
     */
    public void setDevId(DeviceId devId) {
        this.mDevId = devId;
    }

    /**
     * Sets the System Configuration of the System(not the actual system
     * @param config the System Configuration
     */
    public void setConfig(SystemConfiguration config) {
        this.mConfig = config;
    }

    /**
     * Sets the Model number of the system
     * @param model
     */
    public void setModel(String model) {
        this.mModel = model;
    }

    /**
     * Sets the Part number of the system
     * @param partNumber the system's Part number
     */
    public void setPartNumber(String partNumber) {
        this.mPartNumber = partNumber;
    }

    /**
     * Sets the Consoles Name
     * @param consoleName the Name of the Console
     */
    public void setConsoleName(String consoleName) {
        this.mConsoleName = consoleName;
    }

    /**
     * Sets the Communication type of the system
     * @param commType the Communication type
     */
    public void setCommType(CommType commType) {
        this.mCommType = commType;
    }
}
