/**
 * Formats a data into a message for the devices.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * Formats the message, and also provides the data that is expected in return.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.ReadDataSts;

import java.nio.ByteBuffer;
import java.util.Collection;

public class ReadDataCmd extends Command implements CommandInterface{

    private static final int MIN_CMD_LENGTH = 5;

    /**
     * default constructor
     * @throws Exception values are bad
     */
    public ReadDataCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.READ_DATA);
        this.setStatus(new ReadDataSts(this.getDevId()));
        this.setLength(MIN_CMD_LENGTH);//length varies
    }

    /**
     * constructor for devices
     * @throws Exception
     */
    public ReadDataCmd(DeviceId devId) throws Exception
    {
        super(new ReadDataSts(devId), MIN_CMD_LENGTH, CommandId.READ_DATA, devId);
    }

    /**
     * Constructor for setting the bitfields that are used
     * @param devId the main device Id to be used
     * @param bitFieldList the list of bitfields to be read
     * @throws Exception
     */
    public ReadDataCmd(DeviceId devId, Collection<BitFieldId> bitFieldList) throws Exception
    {
        super(new ReadDataSts(devId), MIN_CMD_LENGTH,CommandId.READ_DATA, devId);
        this.addBitField(bitFieldList);
    }

    /**
     * Adds a Bitfield Id to the command for getting the data
     * @param id of the BitField to get the info
     */
    public void addBitField(BitFieldId id) throws Exception
    {
        DataBaseCmd data = ((ReadDataSts)this.getStatus()).getBitFieldData();
        data.addBitfieldData(id, 0);//always 0 for reading
        this.setLength(data.getNumOfDataBytes() + MIN_CMD_LENGTH);
        this.checkMsgSize();
    }

    /**
     * Adds a Bitfield Id to the command for getting the data
     * @param bitFieldList of the BitField to get the info
     */
    public void addBitField(Collection<BitFieldId> bitFieldList) throws Exception
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.addBitField(tempId);
        }
    }

    /**
     * Checks if the bitfield is already set to be read from
     * @param bitId thi bitfield to check
     * @return boolean value of whether it is in the list to read.
     */
    public boolean containsBitField(BitFieldId bitId)
    {
        DataBaseCmd data = ((ReadDataSts)this.getStatus()).getBitFieldData();
        return data.cmdContainsBitfield(bitId);
    }

    /**
     * Removes a bitfield from the desired read
     * @param id of the BitField
     */
    public void removeDataField(BitFieldId id) throws Exception
    {
        DataBaseCmd data = ((ReadDataSts)this.getStatus()).getBitFieldData();
        data.removeBitfieldData(id);//always 0 for reading
        this.setLength(data.getNumOfDataBytes() + MIN_CMD_LENGTH);
        this.checkMsgSize();
    }

    /**
     * Removes a bitfield from the desired read
     * @param bitFieldList of the BitField to get the info
     */
    public void removeDataField(Collection<BitFieldId> bitFieldList) throws Exception
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.removeDataField(tempId);
        }
    }

    /**
     * This will setup the command to be ready to be sent.
     * It adds the number of section bytes, the bits in the sections.
     * @return the Command structured to be ready to sent.
     */
    @Override
    public ByteBuffer getCmdMsg() throws Exception{

        ByteBuffer buff;
        DataBaseCmd data = ((ReadDataSts)this.getStatus()).getBitFieldData();
        //load the default items
        buff = super.getCmdMsg();

        //load the command specific data
        data.getMsgDataHeader(buff);

        //get the checksum value
        buff.put(Command.getCheckSum(buff));
        return buff;
    }

    /**
     * Gets a cloned copy of the command
     * @return the cloned copy of the command
     * @throws Exception if
     */
    @Override
    public Command getCommandCopy() throws Exception {
        ReadDataCmd cmdCopy = new ReadDataCmd(this.getDevId());

        DataBaseCmd data = ((ReadDataSts)this.getStatus()).getBitFieldData();
        cmdCopy.addBitField(data.getMsgData().keySet());

        return cmdCopy;
    }
}
