/**
 * Writes and reads the data from the device.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * First the command will write the data to the device, and the reply will hold
 * all of the items to read.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

public class WriteReadDataCmd extends Command implements CommandInterface{

    private static final int MIN_CMD_LENGTH = 6;//2 section bytes

    // The data to be sent down to the device
    private DataBaseCmd mData;

    /**
     * Default constructor for the write data command no device specified
     * @throws Exception
     */
    public WriteReadDataCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.WRITE_READ_DATA);
        this.mData = new DataBaseCmd();
        this.setStatus(new WriteReadDataSts(this.mDevId));
        this.setLength(MIN_CMD_LENGTH);//length varies
    }

    /**
     * Constructor that includes the DeviceId
     * @param devId the device id of the command
     * @throws Exception
     */
    public WriteReadDataCmd(DeviceId devId) throws Exception
    {
        super(new WriteReadDataSts(devId), MIN_CMD_LENGTH, CommandId.WRITE_READ_DATA, devId);
        this.mData = new DataBaseCmd();
    }

    /**
     * Constructor for helping initialize all the data that you want to send
     * @param devId the device id of the command
     * @param writeData the data that you want to write to on the device
     * @throws Exception
     */
    public WriteReadDataCmd(DeviceId devId, Map<BitFieldId, Object> writeData) throws Exception
    {
        super(new WriteReadDataSts(devId), MIN_CMD_LENGTH, CommandId.WRITE_READ_DATA, devId);
        this.mData = new DataBaseCmd();
        this.addWriteData(writeData);
    }

    /**
     * Constructor for helping initialize all the data that you want to send
     * @param devId the device id of the command
     * @param writeData the data that you want to write to on the device
     * @param readBitIds the data that is to be read from the device
     * @throws Exception
     */
    public WriteReadDataCmd(DeviceId devId,
                            Map<BitFieldId,
                            Object> writeData,
                            Collection<BitFieldId> readBitIds) throws Exception
    {
        super(new WriteReadDataSts(devId), MIN_CMD_LENGTH, CommandId.WRITE_READ_DATA, devId);
        this.mData = new DataBaseCmd();
        this.addWriteData(writeData);
        this.addReadBitField(readBitIds);
    }

    /**
     * Adds a Bitfield Id to the command for Writing the data
     * @param id of the BitField to get the info
     * @param data the data to write
     * @exception Exception
     */
    public void addWriteData(BitFieldId id, Object data) throws Exception
    {
        DataBaseCmd readData;
        if(id.getReadOnly())
        {
            throw new Exception("Invalid BitfieldId "+ id.getDescription()+" This bitfield is read only");
        }

        readData = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        this.mData.addBitfieldData(id, data);
        this.mLength = this.mData.getNumOfDataBytes();
        this.mLength += MIN_CMD_LENGTH;
        this.mLength += this.mData.getMsgDataBytesCount();
        //add read data also to the length
        this.mLength += readData.getNumOfDataBytes();
        this.checkMsgSize();
    }

    /**
     * Adds a Bitfield Id to the command for Writing the data
     * @param writeData map of all the data of bitfield ids,
     *                  and the object to write(int,double, etc..).
     * @exception Exception
     */
    public void addWriteData(Map<BitFieldId, Object> writeData) throws Exception
    {
        for(Map.Entry<BitFieldId, Object> entry : writeData.entrySet())
        {
            this.addWriteData(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds a Bitfield Id to the command for getting the data
     * @param id of the BitField to get the info
     */
    public void addReadBitField(BitFieldId id) throws Exception
    {
        DataBaseCmd readData = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        readData.addBitfieldData(id, 0);//always 0 for reading
        this.mLength = this.mData.getNumOfDataBytes();
        this.mLength += MIN_CMD_LENGTH;
        this.mLength += this.mData.getMsgDataBytesCount();
        //add read data also to the length
        this.mLength += readData.getNumOfDataBytes();
        this.checkMsgSize();
    }

    /**
     * Adds a Bitfield Id to the command for getting the data
     * @param bitFieldList of the BitField to get the info
     */
    public void addReadBitField(Collection<BitFieldId> bitFieldList) throws Exception
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.addReadBitField(tempId);
        }
    }

    /**
     * Removes a bitfield from the write
     * @param id of the BitField
     */
    public void removeWriteDataField(BitFieldId id) throws Exception
    {
        DataBaseCmd readData = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        this.mData.removeBitfieldData(id);
        this.mLength = this.mData.getNumOfDataBytes();
        this.mLength += MIN_CMD_LENGTH;
        this.mLength += this.mData.getMsgDataBytesCount();
        //add read data also to the length
        this.mLength += readData.getNumOfDataBytes();

        this.checkMsgSize();
    }

    /**
     * Removes a bitfield and data from the write
     * @param bitFieldList of the BitField ids
     */
    public void removeWriteDataField(Collection<BitFieldId> bitFieldList) throws Exception
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.removeWriteDataField(tempId);
        }
    }

    /**
     * Removes a bitfield from the desired read
     * @param id of the BitField
     */
    public void removeReadDataField(BitFieldId id) throws Exception
    {
        DataBaseCmd readData = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        readData.removeBitfieldData(id);//always 0 for reading
        this.mLength = this.mData.getNumOfDataBytes();
        this.mLength += MIN_CMD_LENGTH;
        this.mLength += this.mData.getMsgDataBytesCount();
        //add read data also to the length
        this.mLength += readData.getNumOfDataBytes();
        this.checkMsgSize();
    }

    /**
     * Removes a bitfield from the desired read
     * @param bitFieldList of the BitField to get the info
     */
    public void removeReadDataField(Collection<BitFieldId> bitFieldList) throws Exception
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.removeReadDataField(tempId);
        }
    }

    /**
     * Checks if the bitfield is already set to be read from
     * @param bitId thi bitfield to check
     * @return boolean value of whether it is in the list to read.
     */
    public boolean readContainsBitField(BitFieldId bitId)
    {
        DataBaseCmd data = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        return data.cmdContainsBitfield(bitId);
    }

    /**
     * Checks if the bitfield is already set to be Written to
     * @param bitId thi bitfield to check
     * @return boolean value of whether it is in the list to written to.
     */
    public boolean writeContainsBitField(BitFieldId bitId)
    {
        return this.mData.cmdContainsBitfield(bitId);
    }

    /**
     * This will setup the command to be ready to be sent.
     * It adds the number of section bytes, the bits in the sections.
     * @return the Command structured to be ready to sent.
     */
    @Override
    public ByteBuffer getCmdMsg() throws Exception{

        ByteBuffer buff;
        DataBaseCmd data = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        //load the default items
        buff = super.getCmdMsg();

        //load the Write Data Header and Data
        this.mData.getWriteMsgData(buff);
        //load the Read Data Header
        data.getMsgDataHeader(buff);
        //get the checksum value
        buff.put(Command.getCheckSum(buff));
        return buff;
    }

}
