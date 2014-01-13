/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 1/13/14
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.Status;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;


public class DataBaseCmd{

    /**
     * the data to be sent or to be received
     */
    private TreeMap<BitFieldId, Object> mMsgData;

    private int mNumOfDataBytes;//this is the number of bytes that contain the individual bits

    /**
     * this will be used for a variety of commands
     */
    public DataBaseCmd()
    {
        this.mNumOfDataBytes = 0;
        this.mMsgData = new TreeMap<BitFieldId, Object>(new Comparator<BitFieldId>() {
            @Override
            public int compare(BitFieldId bitFieldId, BitFieldId bitFieldId2) {
                return bitFieldId.compareTo(bitFieldId2);
            }
        });
    }

    public void addBitfieldData(BitFieldId id, Object obj)
    {
        if(this.mNumOfDataBytes < id.getSection()+1)
        {
            this.mNumOfDataBytes = id.getSection()+1;
        }
        //obj is the value of the item to be converted
        this.mMsgData.put(id, obj);

    }

    public void removeBitfieldData(BitFieldId id)
    {
        if(this.mMsgData.containsKey(id))
        {
            //check if the number of bytes is the same
            this.mMsgData.remove(id);
            this.mNumOfDataBytes = 0;//recalculate
            for(int i = 0; i < this.mMsgData.size(); i++)
            {
                if(this.mNumOfDataBytes < id.getSection()+1)
                {
                    this.mNumOfDataBytes = id.getSection()+1;
                }
            }
        }
    }

    /**
     * This is the portion of the bytes that need to be sent, from the number of bytes.
     * to the end of the data bits. no data is included in the
     * @return ByteBuffer of the data to be written
     */
    public ByteBuffer getMsgDataHeader()
    {
        //plus one for the number of bytes
        ByteBuffer buffer = ByteBuffer.allocate(this.mNumOfDataBytes +1);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);
        this.getMsgDataHeader(buffer);
        return buffer;
    }

    /**
     * this will populate the buffer with the data, and assumes the position and endian are correct.
     * @param buffer to be populated
     */
    public void getMsgDataHeader(ByteBuffer buffer)
    {
        //plus one for the number of bytes
        buffer.put((byte)this.mNumOfDataBytes);
        //add all the sections we need for the databits
        for(int i = 0; i < this.mNumOfDataBytes; i++)
        {
            buffer.put(getHeaderDataBitBytes(i));
        }
    }

    /**
     * Gets the buffer from the start of the Number of bytes to the end of the last data object.
     * @return buffer formatted for the Write data portion of the command
     */
    public ByteBuffer getWriteMsgData() throws Exception{
        int buffSize = 0;

        //add headerSize
        buffSize += this.mNumOfDataBytes +1;
        //add total Data size
        buffSize += this.getMsgTotalBytes();

        //plus one for the number of bytes
        ByteBuffer buffer = ByteBuffer.allocate(buffSize);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);

        this.getWriteMsgData(buffer);
        return buffer;
    }

    /**
     * Gets the buffer from the start of the Number of bytes to the end of the last data object.
     * @return buffer formatted for the Write data portion of the command
     */
    public void getWriteMsgData(ByteBuffer buffer) throws Exception{
        //populate the header
        getMsgDataHeader(buffer);
        //add the data from the objects to the buffer
        for(BitFieldId id : this.mMsgData.keySet())
        {
            buffer.put(id.getRawFromData(this.mMsgData.get(id)));
        }
    }

    /**
     * gets the number of header data bytes that need to be sent.
     * @return the number of data bytes to be sent
     */
    public int getNumOfDataBytes() {
        return mNumOfDataBytes;
    }

    /**
     * Gets the total number data bytes, not the header number of bytes
     * @return  number of data bytes
     */
    public int getMsgTotalBytes()
    {
        int result = 0;
        //go through all the keys and sum the number of bytes it takes
        for(BitFieldId id : this.mMsgData.keySet())
        {
            result += id.getSize();
        }
        return result;
    }

    /**
     * This will populate all the objects from the byte buffer, assumes position is set correctly
     * @param buffer that holds all the raw data.
     */
    public void handleReadData(ByteBuffer buffer) throws Exception
    {

        //get the objects from the bytebuffer
        for(Map.Entry<BitFieldId, Object> entry : this.mMsgData.entrySet())
        {
            //sets bitfield converter as an object
            entry.setValue(entry.getKey().getData(buffer));
        }
    }

    /**
     * Loops through the HashMap of databits and creates a byte for a specific section
     * @param section the section you need a byte from
     * @return byte of the section
     */
    private byte getHeaderDataBitBytes(int section)
    {
        byte result = 0;
        for(BitFieldId id : this.mMsgData.keySet())
        {
            if(id.getSection() == section)
            {
                result |= (byte)id.getBit();
            }
        }
        return result;
    }

}
