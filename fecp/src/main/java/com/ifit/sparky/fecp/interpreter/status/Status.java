/**
 * Handles all the status SuperClass items.
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This class will handle the status items, mainly anything dealing with the reply from the system.
 */

package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

public class Status {

    private StatusId mStsId;
    private int mLength;
    private CommandId mCmdId;
    private DeviceId mDevId;
    public final int MAX_MSG_LENGTH = 64;// this may change in the future, but for now this is it.

    /**
     * Default Constructor for the Status object.
     */
    public Status()
    {
        this.mStsId = StatusId.DEV_NOT_SUPPORTED;
        this.mLength = 0;
        this.mCmdId = CommandId.NONE;
        this.mDevId = DeviceId.NONE;
    }

    /**
     * sets the values of the status
     * @param stsId the status Id
     * @param length the Length of the message
     * @param cmdId the ommand Id
     * @param devId the Device Id
     */
    public Status(StatusId stsId, int length, CommandId cmdId, DeviceId devId)
    {
        this.mStsId = stsId;
        this.mLength = length;
        this.mCmdId = cmdId;
        this.mDevId = devId;
    }

    /*************************
     * GETTERS
     ************************/

    /**
     * gets the Status Id
     * @return
     */
    public StatusId getStsId()
    {
        return  this.mStsId;
    }

    /**
     *  gets the mLength of the message
     * @return
     */
    public int getLength()
    {
        return this.mLength;
    }

    /**
     * gets the Command id
     * @return
     */
    public CommandId getCmdId()
    {
        return this.mCmdId;
    }

    /**
     * Gets the device id that is in the message
     * @return
     */
    public DeviceId getDevId()
    {
        return this.mDevId;
    }

    /*************************
     * SETTERS
     ************************/

    /**
     * Sets the Status id from the message
     * @param id
     */
    public void setStsId(StatusId id)
    {
        this.mStsId = id;
    }

    /**
     * sets the length of the message
     * @param length
     * @throws Exception if the length is outside of the bounds
     */
    public void setLength(int length) throws Exception
    {
        if(length <= MAX_MSG_LENGTH || length >= 0)
        {
            this.mLength = length;
        }
        else
        {
            throw new Exception("Invalid Length, Max =" + MAX_MSG_LENGTH + " Input Length="+length);
        }
    }

    /**
     * Sets the Command Id
     * @param id
     */
    public void setCmdId(CommandId id)
    {
        this.mCmdId = id;
    }

    /**
     * Sets the Device Id
     * @param id
     */
    public  void setDevId(DeviceId id)
    {
        this.mDevId = id;
    }



}
