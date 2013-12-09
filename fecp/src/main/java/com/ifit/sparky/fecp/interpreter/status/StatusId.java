/**
 * This is the enum for the Status.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * This will hold all the things dealing with the Status ID.
 */
package com.ifit.sparky.fecp.interpreter.status;

public enum StatusId {
    DEV_NOT_SUPPORTED(0, "Device is not supported"),
    CMD_NOT_SUPPORTED(1, "Command is not supported");

    private int id;
    private String description;

    /**
     * Constructor for the StatusId
     * @param id
     * @param description
     */
    StatusId(int id, String description)
    {
        this.id = id;
        this.description = description;
    }

    /**
     * Gets the description of the status.
     * @return a description of the Status
     */
    public String getDescription()
    {
        return  this.description;
    }
}
