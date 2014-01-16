/**
 * Handles the Device Id enum, and all the items dealing with it.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * Contains a id and a description of the Device. don't use the ordinal
 */
package com.ifit.sparky.fecp.interpreter.device;

public enum DeviceId {
    NONE(0, "No Device"),
    MULTIPLE_DEVICES(0x01, "This is a special command for sending multiple device commands" +
            " in single message."),
    MAIN(0x02, "Main, or Parent, device that contains all the sub devices"),
    TREADMILL(0x04, "Treadmill system"),
    INCLINE_TRAINER(0x05, "Incline Trainer"),
    AUDIO(0x40, "Audio Controller");

    private int mId;
    private String mDescription;

    /** constructor for the DeviceId enum.
     *
     * @param id value of the Device
     * @param description of what the Device is
     */
    DeviceId(int id, String description)
    {
        this.mId = id;
        this.mDescription = description;
    }

    /**
     * gets the device id
     * @return the device id
     */
    public int getVal()
    {
        return this.mId;
    }
    /**
     * gets the description of the device.
     * @return a description of the device id
     */
    public String getDescription()
    {
        return  this.mDescription;
    }

    /**
     * Gets the DeviceId based on the id value
     * @param id the Device Id value
     * @return the Device Id
     * @throws InvalidDeviceException if deviceId doesn't exist throw
     */
    public static DeviceId getDeviceId(int id) throws InvalidDeviceException
    {
        //go through all device ids and if it equals then return it.
        for (DeviceId devId : DeviceId.values())
        {
            if(id == devId.getVal())
            {
                return devId; // the Device ID
            }
        }

        //error throw exception
        throw new InvalidDeviceException(id);
    }
}
