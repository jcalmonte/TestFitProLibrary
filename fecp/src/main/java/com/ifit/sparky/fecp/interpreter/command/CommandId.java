/**
 * Handles the Command Id enum.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Contains the id for the message, and the Description of the device.
 */
package com.ifit.sparky.fecp.interpreter.command;

public enum CommandId {
    NONE(0, "No Command"),
    WRITE_READ_DATA(1, "Writes data and reads data in single command.");

    private int id;
    private String description;

    /**
     * constructor for the CommandId enum.
     * @param id value of the Command
     * @param description of what the command is for
     */
    CommandId(int id, String description)
    {
        this.id = id;
        this.description = description;
    }

    /**
     * gets the description of the Command.
     * @return a description of the Command.
     */
    public String getDescription()
    {
        return  this.description;
    }
}
