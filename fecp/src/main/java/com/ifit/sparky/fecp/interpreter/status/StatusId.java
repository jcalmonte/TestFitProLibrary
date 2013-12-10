/**
 * This is the enum for the Status.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * This will hold all the things dealing with the Status ID.
 */
package com.ifit.sparky.fecp.interpreter.status;

public enum StatusId {
    DEV_NOT_SUPPORTED(0, "Device is not supported"),
    CMD_NOT_SUPPORTED(0x01, "Command is not supported"),
    DONE(0x02, "Command was successful send and recieved");

    private int id;
    private String description;

    /**
     * Constructor for the StatusId
     * @param id the StatusID
     * @param description what the status is.
     */
    StatusId(int id, String description)
    {
        this.id = id;
        this.description = description;
    }

    /**
     * gets the id value
     * @return the Status Id Value
     */
    public int getVal()
    {
        return this.id;
    }

    /**
     * Gets the description of the status.
     * @return a description of the Status
     */
    public String getDescription()
    {
        return  this.description;
    }

    /**
     * Gets the StatusId based on the id value
     * @param id the statusId value
     * @return the Status ID
     * @throws Exception if it doesn't exist.
     */
    public static StatusId getStatusId(int id) throws Exception
    {
        //go through all device ids and if it equals then return it.
        for (StatusId stsId : StatusId.values())
        {
            if(id == stsId.getVal())
            {
                return stsId;
            }
        }

        //error throw exception
        throw new Exception("Invalid Status id ("+id+").");
    }
}
