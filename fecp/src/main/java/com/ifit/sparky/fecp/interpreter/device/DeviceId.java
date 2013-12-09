/**
 * Handles the Device Id enum, and all the items dealing with it.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Contains a id and a description of the Device.
 */
package com.ifit.sparky.fecp.interpreter.device;

public enum DeviceId {
    NONE(0, "No Device"),
    MULTIPLE_DEVICES(0x01, "This is a special command for sending multiple device commands" +
            " in single message."),
    MAIN(0x02, "Main, or Parent, device that contains all the sub devices"),
    TREADMILL(0x04, "Treadmill system"),
    INCLINE_TRAINER(0x05, "Incline Trainer");

    private int id;
    private String description;

    /** constructor for the DeviceId enum.
     *
     * @param id value of the Device
     * @param description of what the Device is
     */
    DeviceId(int id, String description)
    {

        this.id = id;
        this.description = description;
    }

    /**
     * gets the description of the device.
     * @return a description of the device id
     */
    public String getDescription()
    {
        return  this.description;
    }
}
