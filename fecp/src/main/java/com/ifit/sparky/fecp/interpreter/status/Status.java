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

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InvalidCommandException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.InvalidDeviceException;

import java.nio.ByteBuffer;


public class Status implements StatusInterface{

    public final int MAX_MSG_LENGTH = 64;// this may change in the future, but for now this is it.

    protected StatusId mStsId;
    protected int mLength;
    protected CommandId mCmdId;
    protected DeviceId mDevId;

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
     * @param cmdId the command Id
     * @param devId the Device Id
     */
    public Status(StatusId stsId, int length, CommandId cmdId, DeviceId devId) throws Exception
    {
        this.mStsId = stsId;

        if(length <= MAX_MSG_LENGTH && length >= 0)
        {
            this.mLength = length;
        }
        else
        {
            throw new Exception("Invalid Length, Max =" + MAX_MSG_LENGTH + " Input Length="+length);
        }

        this.mCmdId = cmdId;
        this.mDevId = devId;
    }

    /*************************
     * GETTERS
     ************************/

    /**
     * gets the Status Id
     * @return the StatusId
     */
    public StatusId getStsId()
    {
        return  this.mStsId;
    }

    /**
     *  gets the mLength of the message
     * @return the Length of the message
     */
    public int getLength()
    {
        return this.mLength;
    }

    /**
     * gets the Command id
     * @return the Command Id
     */
    public CommandId getCmdId()
    {
        return this.mCmdId;
    }

    /**
     * Gets the device id that is in the message
     * @return The Device Id
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
     * @param id the StatusId
     */
    public void setStsId(StatusId id)
    {
        this.mStsId = id;
    }

    /**
     * sets the length of the message
     * @param length the Length of the message
     * @throws Exception if the length is outside of the bounds
     */
    public void setLength(int length) throws Exception
    {
        if(length <= MAX_MSG_LENGTH && length >= 0)
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
     * @param id The CommandId
     */
    public void setCmdId(CommandId id)
    {
        this.mCmdId = id;
    }

    /**
     * Sets the Device Id
     * @param id The DeviceId
     */
    public  void setDevId(DeviceId id)
    {
        this.mDevId = id;
    }


    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception {
        //goes through all the major items, but doesn't handle the specifics
        byte checkSum;
        byte actualByte;
        buff.position(0);
        //first check if the checksum is good
        checkSum = Command.getCheckSum(buff);
        actualByte = buff.get();
        if(actualByte != checkSum)
        {
            throw new InvalidStatusException(checkSum, actualByte);
        }
        //check if the device id matches
        buff.position(0);
        actualByte = buff.get();
        //todo check if message was sent to main.
        if(this.getDevId() == DeviceId.MAIN)
        {
            //set actual byte to be the device id
            this.setDevId(DeviceId.getDeviceId(actualByte));
        }
        if(actualByte != (byte)this.mDevId.getVal())
        {
            throw new InvalidDeviceException(actualByte, this.mDevId);
        }
        //get the length
        this.setLength(buff.get());

        //check if the command id matches
        actualByte = buff.get();
        if(actualByte != (byte)this.mCmdId.getVal())
        {
            throw new InvalidCommandException(this.mCmdId, actualByte);
        }
        //get the status ID
        this.mStsId = StatusId.getStatusId(buff.get());
    }
}
