/**
 * Converts Resistance values into double values with 0.01 precision.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Converts the 2 byte value from a buffer into a Resistance value from  0 to 100.00%.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;

public class ResistanceConverter extends BitfieldDataConverter {

    private double mResistance;

    /**
     * ResistanceConverter Constructor
     */
    public ResistanceConverter()
    {
        super();
        this.mDataSize = 2;
        this.mResistance = 0;
    }

    @Override
    public BitfieldDataConverter getData() throws Exception
    {
        this.mResistance = this.getRawToInt();
        this.mResistance /= 100.0;
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {
        //data coming in as a double
        short rawData;
        double temp = (Double)obj;
        temp *= 100;
        //get a short from it
        rawData = (short)temp;

        this.mRawData = ByteBuffer.allocate(2);
        this.mRawData.putShort(rawData);

        return this.mRawData;
    }

    /**
     * Gets the resistance
     * @return the resistance as a double 0.00 to 100.00
     */
    public double getResistance()
    {
        return this.mResistance;
    }
}