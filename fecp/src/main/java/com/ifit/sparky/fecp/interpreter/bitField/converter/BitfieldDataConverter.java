/**
 * The Data converter for data.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * This will provide a set of static require functions to convert raw byte data into the correct
 * format and type.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public abstract class BitfieldDataConverter {

    protected ArrayList<Byte> mRawData;
    protected int mDataSize;

    /**
     * Default Constructor.
     */
    public BitfieldDataConverter()
    {
    }

    /**
     * Sets the rawData for the Converter
     * @param rawData the raw data to convert
     * @param size the number of data bytes
     * @throws InvalidBitFieldException if the sizes don't match up
     */
    public void setRawData(ArrayList<Byte> rawData, int size) throws InvalidBitFieldException
    {
        if(rawData.size() != size)
        {
            throw new InvalidBitFieldException(rawData, size);
        }

        this.mDataSize = size;
        this.mRawData = rawData;
    }

    /**
     * Gets the Int value from the array of bytes
     * @return int value from the byte array
     * @throws Exception if the number of bytes and the required size don't match up
     */
    protected int getRawToInt() throws Exception
    {
        //depending on the size convert to int
        int rawInt = 0;
        ByteBuffer buffer = ByteBuffer.allocate(this.mDataSize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for(Byte b : this.mRawData)
        {
            buffer.put(b);
        }
        buffer.position(0);

        if(this.mDataSize == 1)
        {
            rawInt = buffer.get(0);
        }
        else if(this.mDataSize == 2)
        {
            rawInt = buffer.getShort(0);
        }
        else if(this.mDataSize == 4)
        {
            rawInt = buffer.getInt(0);
        }
        else
        {
            throw new Exception("DataSizeCurrentlyNotSupported");
        }
        return rawInt;
    }

    /**
     * Converts the data into the byte array
     * @param data the byte, short, or int to be converted
     * @return array of bytes
     */
    protected ArrayList<Byte> getRawFromData(int data) throws InvalidBitFieldException
    {
        //depending on the size convert to int
        ByteBuffer buffer = ByteBuffer.allocate(this.mDataSize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        if(this.mDataSize == 1)
        {
            if(data > Byte.MAX_VALUE)
            {
                Byte b = 0;
                throw new InvalidBitFieldException(data, b);
            }
            buffer.put((byte)data);
        }
        else if(this.mDataSize == 2)
        {
            if(data > Short.MAX_VALUE)
            {
                Short s = 0;
                throw new InvalidBitFieldException(data, s);
            }
            buffer.putShort((short)data);
        }
        else if(this.mDataSize == 4)
        {
            if(data > Long.MAX_VALUE)
            {
                throw new InvalidBitFieldException(data, Long.TYPE);
            }
            buffer.putLong((long)data);
        }

        for(int i=0; i < this.mDataSize; i++)
        {
            this.mRawData.add(buffer.get(i));
        }

        return this.mRawData;
    }

    public abstract BitfieldDataConverter getData()throws Exception;

    public abstract ArrayList<Byte> convertData(Object obj)throws InvalidBitFieldException;

}
