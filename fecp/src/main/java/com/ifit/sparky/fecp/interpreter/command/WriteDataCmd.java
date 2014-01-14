/**
 * This command is for sending data to be written on a specific device.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * this will generate the command to send done to the device.
 * The command will write the data to the device.
 * If will only reply Done if it was successful
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.WriteDataSts;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

public class WriteDataCmd extends Command implements CommandInterface{


    private static final int MIN_CMD_LENGTH = 5;

    // The data to be sent down to the device
    private DataBaseCmd mData;

    /**
     * Default constructor for the write data command no device specified
     * @throws Exception
     */
    public WriteDataCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.WRITE_DATA);
        this.mData = new DataBaseCmd();
        this.setStatus(new WriteDataSts(this.mDevId));
        this.setLength(MIN_CMD_LENGTH);//length varies
    }

    /**
     * Constructor that includes the DeviceId
     * @param devId the device id of the command
     * @throws Exception
     */
    public WriteDataCmd(DeviceId devId) throws Exception
    {
        super(new WriteDataSts(devId), MIN_CMD_LENGTH, CommandId.WRITE_DATA, devId);
        this.mData = new DataBaseCmd();
    }

    /**
     * Constructor for helping initialize all the data that you want to send
     * @param devId the device id of the command
     * @param writeData the data that you want to write to on the device
     * @throws Exception
     */
    public WriteDataCmd(DeviceId devId, Map<BitFieldId, Object> writeData) throws Exception
    {
        super(new WriteDataSts(devId), MIN_CMD_LENGTH, CommandId.WRITE_DATA, devId);
        this.mData = new DataBaseCmd();
        this.addBitField(writeData);
    }

    /**
     * Adds a Bitfield Id to the command for Writing the data
     * @param id of the BitField to get the info
     * @param data the data to write
     * @exception Exception
     */
    public void addBitField(BitFieldId id, Object data) throws Exception
    {
        this.mData.addBitfieldData(id, data);
        this.mLength = this.mData.getNumOfDataBytes();
        this.mLength += MIN_CMD_LENGTH;
        this.mLength += this.mData.getMsgDataBytesCount();
        this.checkMsgSize();
    }

    /**
     * Adds a Bitfield Id to the command for Writing the data
     * @param writeData map of all the data of bitfield ids,
     *                  and the object to write(int,double, etc..).
     * @exception Exception
     */
    public void addBitField(Map<BitFieldId, Object> writeData) throws Exception
    {
        for(Map.Entry<BitFieldId, Object> entry : writeData.entrySet())
        {
            this.addBitField(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Checks if the bitfield is already set to be Written to
     * @param bitId thi bitfield to check
     * @return boolean value of whether it is in the list to written to.
     */
    public boolean containsBitField(BitFieldId bitId)
    {
        return this.mData.cmdContainsBitfield(bitId);
    }

    /**
     * Removes a bitfield from the write
     * @param id of the BitField
     */
    public void removeDataField(BitFieldId id) throws Exception
    {
        this.mData.removeBitfieldData(id);//always 0 for reading
        this.mLength = this.mData.getNumOfDataBytes();
        this.mLength += MIN_CMD_LENGTH;
        this.mLength += this.mData.getMsgDataBytesCount();

        this.checkMsgSize();
    }

    /**
     * Removes a bitfield and data from the write
     * @param bitFieldList of the BitField ids
     */
    public void removeDataField(Collection<BitFieldId> bitFieldList) throws Exception
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.removeDataField(tempId);
        }
    }

    /**
     * This will setup the whole message so that it is ready to be sent.
     * this includes the number of sections, bits in the sections, and the data to be sent.
     * @return the Command structured to be ready to sent.
     */
    @Override
    public ByteBuffer getCmdMsg() throws Exception {
        ByteBuffer buffer;

        buffer = super.getCmdMsg();
        //load the data header and data into the buffer
        this.mData.getWriteMsgData(buffer);

        //get the checksum value
        buffer.put(Command.getCheckSum(buffer));
        return buffer;

    }
}
