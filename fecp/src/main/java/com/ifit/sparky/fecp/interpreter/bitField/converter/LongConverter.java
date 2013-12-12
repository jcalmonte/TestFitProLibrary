/**
 * Converts the data into its values.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * Converts the value into its exact value.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;

public class LongConverter extends BitfieldDataConverter{

    private int mData;
    public LongConverter()
    {
        super();
        this.mData = 0;
        this.mDataSize = 4;
        this.mRawData = ByteBuffer.allocate(this.mDataSize);
    }

    @Override
    public BitfieldDataConverter getData() throws Exception {
        this.mData = (int)this.getRawToInt();
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        this.mRawData.putShort((Short)obj);
        return this.mRawData;
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public int getValue()
    {
        return this.mData;
    }
}