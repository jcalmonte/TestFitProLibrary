/**
 * Handles the Command Id enum.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * Contains the id for the message, and the Description of the device.
 */
package com.ifit.sparky.fecp.interpreter.command;

public enum CommandId {
    NONE(0, "No Command"),
    WRITE_READ_DATA(0x02, "Writes data and reads data in single command."),
    TEST(0x03, "Test the device."),
    CONNECT(0x04, "Connects to the device."),
    DISCONNECT(0x05, "Disconnects from the device."),
    CALIBRATE(0x06, "Calibrates the device."),
    WRITE_DATA(0x0B, "Write Data."),
    GET_SUPPORTED_DEVICES(0x80, "Get Supported Devices."),
    GET_INFO(0x81, "Get Device Info."),
    GET_SYSTEM_INFO(0x82, "Get System Info."),
    GET_TASK_INFO(0x83, "Get Task Info."),
    GET_SUPPORTED_COMMANDS(0x88, "Get Supported Commands."),
    READ_DATA(0x90, "Read Data.");

    private int mId;
    private String mDescription;

    /**
     * constructor for the CommandId enum.
     * @param id value of the Command
     * @param description of what the command is for
     */
    CommandId(int id, String description)
    {
        this.mId = id;
        this.mDescription = description;
    }

    /**
     * gets the id value
     * @return gets the Command Id Value
     */
    public int getVal()
    {
        return this.mId;
    }

    /**
     * gets the description of the Command.
     * @return a description of the Command.
     */
    public String getDescription()
    {
        return  this.mDescription;
    }

    /**
     * Gets the CommandId based on the idNumber.
     * @param id The Command id Value
     * @return the Command Id
     * @throws InvalidCommandException if it doesn't exist
     */
    public static CommandId getCommandId(int id) throws InvalidCommandException
    {
        //go through all command ids and if it equals then return it.
        for (CommandId cmdId : CommandId.values())
        {
            if(id == cmdId.getVal())
            {
                return cmdId;
            }
        }

        //error throw exception
        throw new InvalidCommandException(id);
    }

}
